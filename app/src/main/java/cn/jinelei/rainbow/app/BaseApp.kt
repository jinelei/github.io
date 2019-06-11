package cn.jinelei.rainbow.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.jinelei.rainbow.BuildConfig
import cn.jinelei.rainbow.app.handler.CustomCrashHandler
import com.facebook.stetho.Stetho

@Suppress("UNCHECKED_CAST")
class BaseApp : Application() {

    private fun initData() {
    }

    //    销毁相关数据
    private fun destroyData() {
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
        destroyData()
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

    @SuppressLint("ApplySharedPref")
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
        val instace = BaseApp()
    }
}