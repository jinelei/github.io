package cn.jinelei.rainbow.activity

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.application.BaseApplication
import cn.jinelei.rainbow.components.LoadingDialog
import cn.jinelei.rainbow.util.SharedPreUtil
import cn.jinelei.rainbow.util.getCrc16
import kotlinx.coroutines.*
import java.lang.Runnable
import java.nio.charset.Charset
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class BaseActivity : AppCompatActivity() {
    var baseApplication: BaseApplication? = null
    //    fragment管理器
    protected var fragmentManager: FragmentManager? = null
    protected var currentFragment: Fragment? = null
    //    已经授权应该执行的任务
    private val grantedPermRunnable = HashMap<Int, Runnable>()
    //    拒绝授权应该执行的任务
    private val deniedPermRunnable = HashMap<Int, Runnable>()

    //    初始化数据
    private fun initData() {
        fragmentManager = supportFragmentManager
        baseApplication = this.application as BaseApplication
    }

    //    销毁相关数据
    private fun destoryData() {
        grantedPermRunnable.clear()
        deniedPermRunnable.clear()
        baseApplication = null
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
        initData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
        initData()
    }

    override fun onStart() {
        super.onStart()
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onResume() {
        super.onResume()
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onPause() {
        super.onPause()
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStop() {
        super.onStop()
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestart() {
        super.onRestart()
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDestroy() {
        super.onDestroy()
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
        destoryData()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        baseApplication?.debug(Log.VERBOSE, Thread.currentThread().stackTrace[2].methodName)
    }

    fun getTag(): String {
        return this.javaClass.simpleName
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
                    baseApplication?.debug(Log.VERBOSE, "id ${id} permission ${permission} granted")
                    ActivityCompat.requestPermissions(this, arrayOf(permission), id)
                    grantedRun?.run()
                    return
                } else {
                    baseApplication?.debug(Log.VERBOSE, "id ${id} permission ${permission} denied, try alert dialog")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        needUserAgreePermission.add(permission)
                    } else {
                        needUserAgreePermission.add(permission)
                    }
                }
            } else {
                baseApplication?.debug(Log.VERBOSE, "id ${id} permission ${permission} granted")
                grantedRun?.run()
            }
        }
        if (needUserAgreePermission.isNotEmpty()) {
            val textView = TextView(this)
            textView.text = needUserAgreePermission.reduce { acc, s -> "$acc\n$s" }
            GlobalScope.launch(Dispatchers.Main) {
                baseApplication?.alertDialogBuilder?.setTitle(getString(R.string.please_grant_application_permission))
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
                baseApplication?.debug(Log.VERBOSE, "id ${requestCode} permission ${permission} granted")
                grantedPermRunnable.get(requestCode)?.run()
            } else {
                baseApplication?.debug(Log.VERBOSE, "id ${requestCode} permission ${permission} denied")
                deniedPermRunnable.get(requestCode)?.run()
            }
        }
    }

    fun switchFragmentTo(containerId: Int, targetFragment: Fragment) {
        baseApplication?.debug(
            Log.VERBOSE,
            "${currentFragment?.javaClass?.simpleName} to ${targetFragment::class.java.simpleName}"
        )
        if (currentFragment == null) {
            fragmentManager?.beginTransaction().also {
                if (targetFragment.isAdded) {
                    it?.show(targetFragment)?.commit()
                } else {
                    it?.add(containerId, targetFragment)?.commit()
                }
                currentFragment = targetFragment
            }
        } else if (currentFragment != targetFragment) {
            fragmentManager?.beginTransaction().also {
                if (targetFragment.isAdded) {
                    it?.hide(currentFragment!!)?.show(targetFragment)?.commit()
                } else {
                    it?.hide(currentFragment!!)?.add(containerId, targetFragment)?.commit()
                }
                currentFragment = targetFragment
            }
        }
    }
}