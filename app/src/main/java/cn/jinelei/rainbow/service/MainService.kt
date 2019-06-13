package cn.jinelei.rainbow.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import cn.jinelei.rainbow.IBinderQuery
import cn.jinelei.rainbow.ITestService
import cn.jinelei.rainbow.bluetooth.IConnectionCallback
import cn.jinelei.rainbow.bluetooth.IBluetoothService
import cn.jinelei.rainbow.constant.BINDER_REQUEST_CODE_BLUETOOTH
import cn.jinelei.rainbow.constant.BINDER_REQUEST_CODE_TEST

class MainService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return mBinderQuery
    }

    override fun onCreate() {
        super.onCreate()
    }

    private val mBinderQuery = object : IBinderQuery.Stub() {
        override fun queryBinder(binderCode: Int): IBinder {
            return when (binderCode) {
                BINDER_REQUEST_CODE_BLUETOOTH -> mBluetoothService
                BINDER_REQUEST_CODE_TEST -> mTestService
                else -> mTestService
            }
        }
    }

    private val mTestService = object : ITestService.Stub() {
        override fun test(aString: String?) {
            Log.d(ITestService::class.java.simpleName, "test() receive: $aString")
        }
    }

    private val mBluetoothService = object : IBluetoothService.Stub() {
        override fun init(cmd_type: Int, tagName: String?): Int {
            Log.d(IConnectionCallback::class.java.simpleName, "init")
            return 0
        }

        override fun sendBytes(tagName: String?, cmd: String?, data: ByteArray?, priority: Int): Long {
            return 0L
        }

        override fun getConnectionState(): Int {
            return 0
        }

        override fun close(tagName: String?) {
        }

        override fun registerConnectionCallback(tagName: String?, callback: IConnectionCallback?) {
        }

        override fun unregisterConnectionCallback(tagName: String?, callback: IConnectionCallback?) {
        }

        override fun getRemoteDeviceName(): String {
            return "getRemoteDeviceName"
        }

    }

}
