package cn.jinelei.rainbow.application

import android.app.AlertDialog
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.net.wifi.WifiManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.Toast
import cn.jinelei.rainbow.BuildConfig
import cn.jinelei.rainbow.components.LoadingDialog
import cn.jinelei.rainbow.handler.CustomCrashHandler
import cn.jinelei.rainbow.util.SharedPreUtil
import com.facebook.stetho.Stetho
import kotlinx.coroutines.*
import java.lang.Runnable

class BaseApplication : Application() {
    //    默认隐藏加载中弹窗的超时时间
    val DEFAULT_HIDE_LOADING_TIMEOUT = 10000L
    //    加载中弹窗
    var loadingDialog: LoadingDialog? = null
    //    自动隐藏加载中弹窗
    var loadingDialogTimeoutJob: Job? = null
    //    wifi管理器
    var wifiManager: WifiManager? = null
    //    通知管理器
    var notificationManager: NotificationManager? = null
    //    请求权限的弹窗
    var alertDialogBuilder: AlertDialog.Builder? = null

    private fun initData() {
        loadingDialog = LoadingDialog(this)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        alertDialogBuilder = AlertDialog.Builder(this)
    }

    //    销毁相关数据
    private fun destoryData() {
        loadingDialog?.dismiss()
        loadingDialog = null
        loadingDialogTimeoutJob = null
        wifiManager = null
        notificationManager = null
    }


    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(CustomCrashHandler.instance)
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        initData()
    }

    override fun onTerminate() {
        super.onTerminate()
        destoryData()
    }

    companion object {
        val instace = BaseApplication()
    }

    fun debug(level: Int, message: String) {
        val debug: Int = SharedPreUtil.readPre(this, SharedPreUtil.NAME_USER, SharedPreUtil.KEY_DEBUG_FLAG, 0) as Int
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

    fun toast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(this@BaseApplication, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showLoading(timeout: Long = DEFAULT_HIDE_LOADING_TIMEOUT) {
        GlobalScope.launch(Dispatchers.Main) {
            if (loadingDialog?.isShowing == false) {
                debug(Log.VERBOSE, "show loading dialog and set timeout: $timeout")
                loadingDialog?.show()
            }
        }
        loadingDialogTimeoutJob = GlobalScope.launch(Dispatchers.Default) {
            delay(timeout)
            GlobalScope.launch(Dispatchers.Main) {
                if (loadingDialog?.isShowing == true) {
                    debug(Log.VERBOSE, "dismiss loading dialog in timeout job")
                    loadingDialog?.dismiss()
                }
            }
        }
    }

    fun hideLoading() {
        GlobalScope.launch(Dispatchers.Main) {
            if (loadingDialog?.isShowing == true) {
                debug(Log.VERBOSE, "dismiss loading dialog")
                loadingDialog?.dismiss()
            }
        }
        if (loadingDialogTimeoutJob?.isActive == true) {
            debug(Log.VERBOSE, "cancel loading dialog timeout job")
            loadingDialogTimeoutJob?.cancel()
        }
    }
}