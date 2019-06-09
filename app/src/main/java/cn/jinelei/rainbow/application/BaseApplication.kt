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

    fun <T> readPreference(name: String, key: String, defaultValue: T): T {
        val preference = getSharedPreferences(name, Context.MODE_PRIVATE)
        return when (defaultValue) {
            is Boolean -> preference.getBoolean(key, defaultValue) as T
            is Int -> preference.getInt(key, defaultValue) as T
            is Float -> preference.getFloat(key, defaultValue) as T
            is Long -> preference.getLong(key, defaultValue) as T
            is String -> preference.getString(key, defaultValue) as T
            else -> preference.getString(key, defaultValue.toString()) as T
        }
    }

    fun <T> savePreference(name: String, key: String, defaultValue: T) {
        val preference = getSharedPreferences(name, Context.MODE_PRIVATE)
        when (defaultValue) {
            is Boolean -> preference.edit().putBoolean(key, defaultValue).commit()
            is Int -> preference.edit().putInt(key, defaultValue).commit()
            is Float -> preference.edit().putFloat(key, defaultValue).commit()
            is Long -> preference.edit().putLong(key, defaultValue).commit()
            is String -> preference.edit().putString(key, defaultValue).commit()
            else -> preference.edit().putString(key, defaultValue.toString()).commit()
        }
    }

    companion object {
        val instace = BaseApplication()
    }
}