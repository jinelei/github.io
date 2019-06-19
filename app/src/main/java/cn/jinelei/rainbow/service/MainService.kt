package cn.jinelei.rainbow.service

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.IBinder
import android.util.Log
import cn.jinelei.rainbow.IBinderQuery
import cn.jinelei.rainbow.ITestService
import cn.jinelei.rainbow.app.BaseApp
import cn.jinelei.rainbow.bluetooth.IBluetoothService
import cn.jinelei.rainbow.bluetooth.IConnectionCallback
import cn.jinelei.rainbow.constant.*
import org.greenrobot.eventbus.EventBus

class MainService : Service() {
	private lateinit var mBaseApp: BaseApp
	
	override fun onBind(intent: Intent): IBinder {
		return mBinderQuery
	}
	
	override fun onCreate() {
		super.onCreate()
		mBaseApp = application as BaseApp
		EventBus.getDefault().register(this)
	}
	
	override fun onDestroy() {
		super.onDestroy()
		EventBus.getDefault().unregister(this)
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
		override fun sendBytes(device: BluetoothDevice?, cmd: String?, data: ByteArray?, priority: Int): Long {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		override fun getConnectionState(device: BluetoothDevice?): Int {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		override fun close(device: BluetoothDevice?) {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		val deviceCallback = mutableMapOf<BluetoothDevice, IConnectionCallback>()
		var mIConnectionCallback: IConnectionCallback? = null
		
		override fun registerConnectionCallback(device: BluetoothDevice?, callback: IConnectionCallback?) {
			callback?.let {
				mIConnectionCallback = it
			}
		}
		
		override fun unregisterConnectionCallback(device: BluetoothDevice?, callback: IConnectionCallback?) {
			callback?.let {
				if (it.equals(mIConnectionCallback)) {
					mIConnectionCallback = null
				}
			}
		}
		
	}
	
	private var mScanCallback: ScanCallback = object : ScanCallback() {
		override fun onScanFailed(errorCode: Int) {
			mBaseApp.debug(Log.DEBUG, "onScanFailed errorCode: $errorCode")
			EventBus.getDefault().post(BtScanStatus())
		}
		
		override fun onScanResult(callbackType: Int, result: ScanResult?) {
			result?.let {
				mBaseApp.debug(Log.VERBOSE, "onScanResult $it.toString()}")
				EventBus.getDefault().post(BtScanResult(it.device))
			}
		}
		
		override fun onBatchScanResults(results: MutableList<ScanResult>?) {
			super.onBatchScanResults(results)
			mBaseApp.debug(Log.DEBUG, "onBatchScanResults ${results?.size}")
			EventBus.getDefault().post(BtBatchScanResults(results))
		}
	}
	
	private val mGattCallback = object : BluetoothGattCallback() {
		override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mBaseApp.debug(Log.DEBUG, "Connected to GATT server.")
				mBaseApp.debug(Log.DEBUG, "Attempting to start service discovery: ${gatt?.discoverServices()}");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				gatt?.let {
					mBaseApp.debug(Log.DEBUG, "Disconnected from GATT server. name: ${gatt?.device?.name}")
					it.close()
					it.disconnect()
				}
			} else if (newState == BluetoothProfile.STATE_CONNECTING) {
				mBaseApp.debug(Log.DEBUG, "connecting from GATT server. name: ${gatt?.device?.name}")
			} else {
				mBaseApp.debug(Log.DEBUG, "Disconnecting from GATT server.");
			}
		}
		
		override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
			mBaseApp.debug(Log.VERBOSE, "onServicesDiscovered: status $status")
			gatt?.let {
				displayUuids(gatt)
			}
		}
	}
	
}
