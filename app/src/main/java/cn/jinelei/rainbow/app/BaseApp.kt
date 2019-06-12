package cn.jinelei.rainbow.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import cn.jinelei.rainbow.BuildConfig
import cn.jinelei.rainbow.app.handler.CustomCrashHandler
import cn.jinelei.rainbow.constant.PRE_KEY_DEBUG
import cn.jinelei.rainbow.constant.PRE_NAME_MINE
import com.facebook.stetho.Stetho

@Suppress("UNCHECKED_CAST")
class BaseApp : Application() {

    //    初始化数据
    private fun initData() {
        if (!existPreference(PRE_NAME_MINE, PRE_KEY_DEBUG))
            savePreference(PRE_NAME_MINE, PRE_KEY_DEBUG, Log.DEBUG)
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

    fun existPreference(name: String, key: String): Boolean {
        return getSharedPreferences(name, Context.MODE_PRIVATE).contains(key)
    }

    companion object {
        val instace = BaseApp()
    }
}