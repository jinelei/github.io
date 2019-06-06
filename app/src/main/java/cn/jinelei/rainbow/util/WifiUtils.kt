package cn.jinelei.rainbow.util

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.ConnectivityManager
import android.net.NetworkInfo



class WifiHelper(val context: Context, var wifiManager: Any, var wifiInfo: WifiInfo) {
    var wifiLock: WifiManager.WifiLock? = null

    init {
        wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiInfo = (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).connectionInfo
    }

    fun openWifi(context: Context) {
    }

    fun searchWifi(){
    }

    fun acquireWifiLock() {
        wifiLock?.acquire()
    }

}

fun isWifiConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return if (wifiNetworkInfo.isConnected) {
        true
    } else false
}