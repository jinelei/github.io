package cn.jinelei.rainbow.base

import android.app.AlertDialog
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.jinelei.rainbow.app.BaseApp
import cn.jinelei.rainbow.constant.DEFAULT_HIDE_LOADING_TIMEOUT
import cn.jinelei.rainbow.constant.PRE_KEY_DEBUG
import cn.jinelei.rainbow.constant.PRE_NAME_MINE
import cn.jinelei.rainbow.ui.view.LoadingDialog
import cn.jinelei.rainbow.util.getCrc16
import kotlinx.coroutines.*


open class BaseActivity : AppCompatActivity() {
    protected lateinit var mBaseApp: BaseApp
    protected lateinit var mContext: Context
    // 公共的管理器
    lateinit var mWifiManager: WifiManager    //    wifi管理器
    lateinit var mBluetoothManager: BluetoothManager    //    蓝牙管理器
    lateinit var mNotificationManager: NotificationManager    //    通知管理器
    // 弹窗相关
    private lateinit var loadingDialog: LoadingDialog    //    加载中弹窗
    lateinit var alertDialogBuilder: AlertDialog.Builder    //    请求权限的弹窗
    private var loadingDialogTimeoutJob: Job? = null    //    自动隐藏加载中弹窗
    // Fragment相关
    lateinit var fragmentManager: FragmentManager    //    fragment管理器
    private var currentFragment: Fragment? = null // 当前的Fragment
    private var previewFragment: Fragment? = null // 上一个Fragment
    //权限相关
    private val grantedActions = mutableMapOf<Int, Runnable>()    //    已经授权应该执行的任务
    private val deniedActions = mutableMapOf<Int, Runnable>()    //    拒绝授权应该执行的任务
    private var grantedTaskCount = 0    //    已经授权的数量
    private var deniedTaskCount = 0    //    已经拒绝的数量

    //    初始化数据
    private fun initData() {
        mBaseApp = this.application as BaseApp
        mContext = this
        loadingDialog = LoadingDialog(mContext)
        mWifiManager = mBaseApp.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mBluetoothManager = mBaseApp.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mNotificationManager = mBaseApp.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alertDialogBuilder = AlertDialog.Builder(mContext)
        fragmentManager = supportFragmentManager
        grantedActions.clear()
        deniedActions.clear()
    }

    //    销毁相关数据
    private fun destroyData() {
        grantedActions.clear()
        deniedActions.clear()
        loadingDialog.dismiss()
        loadingDialogTimeoutJob = null
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initData()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStart() {
        super.onStart()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onResume() {
        super.onResume()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onPause() {
        super.onPause()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStop() {
        super.onStop()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestart() {
        super.onRestart()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDestroy() {
        super.onDestroy()
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
        destroyData()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    //    设置必要权限
    protected fun setNecessaryPermission(
        permissions: List<String>,
        grantedAction: java.lang.Runnable? = null,
        deniedAction: java.lang.Runnable? = null,
        explainAction: Runnable? = null
    ) {
        grantedTaskCount = permissions.size
        deniedTaskCount = 0
        var couldShowExplainAction = true
        for (permission in permissions) {
            val requestCode = getCrc16(permission.toByteArray())
            if (grantedAction != null)
                grantedActions[requestCode] = grantedAction
            if (deniedAction != null)
                deniedActions[requestCode] = deniedAction
            val grantResult = ActivityCompat.checkSelfPermission(this, permission)
            if (PackageManager.PERMISSION_GRANTED == grantResult) { // 已经获得授权
                debug(Log.VERBOSE, "permission $permission granted")
                if (deniedTaskCount + --grantedTaskCount == 0) { // 请求权限结束
                    if (grantedTaskCount == 0) { // 完全授权成功
                        grantedAction?.run()
                    }
                }
            } else { // 没有授权
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    // 弹窗解释
                    debug(Log.VERBOSE, "permission $permission explain")
                    --deniedTaskCount
                    if (couldShowExplainAction) {
                        couldShowExplainAction = false
                        explainAction?.run()
                    }
                } else {
                    debug(Log.VERBOSE, "permission $permission denied")
                    ActivityCompat.requestPermissions(this, listOf(permission).toTypedArray(), requestCode)
                    if (--deniedTaskCount + grantedTaskCount == 0) { // 请求权限结束
                        deniedAction?.run()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val resultIterator = grantResults.iterator()
        val permissionIterator = permissions.iterator()
        while (resultIterator.hasNext()) {
            val result = resultIterator.next()
            val permission = permissionIterator.next()
            if (result == PackageManager.PERMISSION_GRANTED) {
                debug(Log.VERBOSE, "requestCode $requestCode permission $permission granted")
                if (deniedTaskCount + --grantedTaskCount == 0) { // 请求权限结束
                    if (grantedTaskCount == 0) { // 完全授权成功
                        grantedActions[requestCode]?.run()
                    }
                }
            } else {
                debug(Log.VERBOSE, "requestCode $requestCode permission $permission denied")
                if (--deniedTaskCount + grantedTaskCount == 0) { // 请求权限结束
                    deniedActions[requestCode]?.run()
                }
            }
        }
    }

    //    切换Fragment
    fun switchFragmentTo(containerId: Int, targetFragment: Fragment) {
        debug(
            Log.VERBOSE,
            "${currentFragment?.javaClass?.simpleName} to ${targetFragment::class.java.simpleName}"
        )
        if (currentFragment == null) {
            fragmentManager.beginTransaction().also {
                if (targetFragment.isAdded) {
                    it.show(targetFragment).commit()
                } else {
                    it.add(containerId, targetFragment).commit()
                }
                currentFragment = targetFragment
            }
        } else if (currentFragment != targetFragment) {
            fragmentManager.beginTransaction().also {
                if (targetFragment.isAdded) {
                    it.hide(currentFragment!!).show(targetFragment).commit()
                } else {
                    it.hide(currentFragment!!).add(containerId, targetFragment).commit()
                }
                previewFragment = currentFragment
                currentFragment = targetFragment
            }
        }
    }

    //    回退Fragment
    fun restorePreviewFragment(recyclerCurrent: Boolean = false) {
        if (previewFragment != null && currentFragment != null) {
            fragmentManager.beginTransaction().also {
                it.hide(currentFragment!!).show(previewFragment!!).commit()
                if (recyclerCurrent) {
                    previewFragment = currentFragment
                } else {
                    previewFragment = null
                }
            }
        }
    }

    fun debug(level: Int, message: String) {
        val debug = mBaseApp.readPreference(
            name = PRE_NAME_MINE,
            key = PRE_KEY_DEBUG,
            defaultValue = 0
        )
        when (level) {
            Log.VERBOSE -> Log.v(this.javaClass.simpleName, message)
            Log.DEBUG -> Log.d(this.javaClass.simpleName, message)
            Log.INFO -> Log.i(this.javaClass.simpleName, message)
            Log.WARN -> Log.w(this.javaClass.simpleName, message)
            Log.ERROR -> Log.e(this.javaClass.simpleName, message)
        }
        if (level >= debug)
            mBaseApp.toast(message)
    }

    //    显示加载框
    fun showLoading(timeout: Long = DEFAULT_HIDE_LOADING_TIMEOUT) {
        GlobalScope.launch(Dispatchers.Main) {
            if (!loadingDialog.isShowing) {
                debug(Log.VERBOSE, "show loading dialog and set timeout: $timeout")
                loadingDialog.show()
            }
        }
        loadingDialogTimeoutJob = GlobalScope.launch(Dispatchers.Default) {
            delay(timeout)
            GlobalScope.launch(Dispatchers.Main) {
                if (loadingDialog.isShowing) {
                    debug(Log.VERBOSE, "dismiss loading dialog in timeout job")
                    loadingDialog.dismiss()
                }
            }
        }
    }

    //    隐藏加载框
    fun hideLoading() {
        GlobalScope.launch(Dispatchers.Main) {
            if (loadingDialog.isShowing) {
                debug(Log.VERBOSE, "dismiss loading dialog")
                loadingDialog.dismiss()
            }
        }
        if (loadingDialogTimeoutJob?.isActive == true) {
            debug(Log.VERBOSE, "cancel loading dialog timeout job")
            loadingDialogTimeoutJob?.cancel()
        }
    }
}