package cn.jinelei.rainbow.app

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.Toast
import cn.jinelei.rainbow.BuildConfig
import cn.jinelei.rainbow.app.handler.CustomCrashHandler
import cn.jinelei.rainbow.constant.PRE_KEY_DEBUG
import cn.jinelei.rainbow.constant.PRE_NAME_MINE
import com.facebook.stetho.Stetho
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BaseApp : Application() {
    // 公共的管理器
    lateinit var mWifiManager: WifiManager    //    wifi管理器
    lateinit var mNotificationManager: NotificationManager    //    通知管理器
    lateinit var mBluetoothManager: BluetoothManager    //    蓝牙管理器
    lateinit var mBluetoothAdapter: BluetoothAdapter   //    蓝牙适配器
    //    初始化数据
    private fun initData() {
        if (!existPreference(PRE_NAME_MINE, PRE_KEY_DEBUG))
            savePreference(PRE_NAME_MINE, PRE_KEY_DEBUG, Log.DEBUG)
        mBluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
        mWifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    fun toast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    fun debug(level: Int, message: String) {
        val debug = readPreference(
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
            toast(message)
    }

}