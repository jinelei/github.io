package cn.jinelei.rainbow.base

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.app.BaseApp
import cn.jinelei.rainbow.constant.DEFAULT_HIDE_LOADING_TIMEOUT
import cn.jinelei.rainbow.constant.PRE_KEY_DEBUG
import cn.jinelei.rainbow.constant.PRE_NAME_USER
import cn.jinelei.rainbow.ui.view.LoadingDialog
import cn.jinelei.rainbow.util.getCrc16
import kotlinx.coroutines.*
import java.nio.charset.Charset
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


open class BaseActivity : AppCompatActivity() {
    lateinit var mBaseApp: BaseApp
    // 公共的管理器
    lateinit var mWifiManager: WifiManager    //    wifi管理器
    lateinit var mNotificationManager: NotificationManager    //    通知管理器
    // 弹窗相关
    private lateinit var loadingDialog: LoadingDialog    //    加载中弹窗
    lateinit var alertDialogBuilder: AlertDialog.Builder    //    请求权限的弹窗
    private var loadingDialogTimeoutJob: Job? = null    //    自动隐藏加载中弹窗
    // Fragment相关
    lateinit var fragmentManager: FragmentManager    //    fragment管理器
    var currentFragment: Fragment? = null // 当前的Fragment
    var previewFragment: Fragment? = null // 上一个Fragment
    //权限相关
    private val grantedPermRunnable = HashMap<Int, Runnable>()    //    已经授权应该执行的任务
    private val deniedPermRunnable = HashMap<Int, Runnable>()    //    拒绝授权应该执行的任务

    //    初始化数据
    private fun initData() {
        loadingDialog = LoadingDialog(this)
        mWifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mNotificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alertDialogBuilder = AlertDialog.Builder(this)
        fragmentManager = supportFragmentManager
        mBaseApp = this.application as BaseApp
    }

    //    销毁相关数据
    private fun destroyData() {
        grantedPermRunnable.clear()
        deniedPermRunnable.clear()
        loadingDialog.dismiss()
        loadingDialogTimeoutJob = null
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
        initData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
        initData()
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

    protected fun customRequestPermission(
        permissions: List<String>,
        grantedRun: Runnable?,
        deniedRun: Runnable?
    ) {
        val needUserAgreePermission = ArrayList<String>()
        for (permission in permissions) {
            val id = getCrc16(permission.toByteArray(Charset.defaultCharset()))
            if (grantedRun != null)
                grantedPermRunnable[id] = grantedRun
            if (deniedRun != null)
                deniedPermRunnable[id] = deniedRun
            if (Build.VERSION.SDK_INT > 23) {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)) {
                    debug(Log.VERBOSE, "id ${id} permission ${permission} granted")
                    ActivityCompat.requestPermissions(this, arrayOf(permission), id)
                    grantedRun?.run()
                    return
                } else {
                    debug(Log.VERBOSE, "id ${id} permission ${permission} denied, try alert dialog")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        needUserAgreePermission.add(permission)
                    } else {
                        needUserAgreePermission.add(permission)
                    }
                }
            } else {
                debug(Log.VERBOSE, "id ${id} permission ${permission} granted")
                grantedRun?.run()
            }
        }
        if (needUserAgreePermission.isNotEmpty()) {
            val textView = TextView(this)
            textView.text = needUserAgreePermission.reduce { acc, s -> "$acc\n$s" }
            GlobalScope.launch(Dispatchers.Main) {
                alertDialogBuilder.setTitle(getString(R.string.please_grant_application_permission))
                    ?.setView(textView)
                    ?.setPositiveButton(getString(R.string.ok)) { _, _ ->
                        requestPermissions(
                            needUserAgreePermission.toTypedArray(),
                            getCrc16(needUserAgreePermission[0].toByteArray(Charset.defaultCharset()))
                        )
                    }
                    ?.setNegativeButton(getString(R.string.cancel)) { _, _ -> deniedRun?.run() }
                    ?.create()
                    ?.show()
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
                debug(Log.VERBOSE, "id ${requestCode} permission ${permission} granted")
                grantedPermRunnable.get(requestCode)?.run()
            } else {
                debug(Log.VERBOSE, "id ${requestCode} permission ${permission} denied")
                deniedPermRunnable.get(requestCode)?.run()
            }
        }
    }

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
        val debug = (applicationContext as BaseApp).readPreference(
            name = PRE_NAME_USER,
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
            toast(message)
    }

    private fun toast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

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