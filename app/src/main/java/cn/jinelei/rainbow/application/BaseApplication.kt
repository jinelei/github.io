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

    private fun initData() {
    }

    //    销毁相关数据
    private fun destoryData() {
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
}