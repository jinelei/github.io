package cn.jinelei.rainbow.service

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import cn.jinelei.rainbow.IBinderQuery
import cn.jinelei.rainbow.ITestService
import cn.jinelei.rainbow.app.BaseApp
import cn.jinelei.rainbow.bluetooth.IBluetoothService
import cn.jinelei.rainbow.bluetooth.IConnectionCallback
import cn.jinelei.rainbow.constant.BINDER_REQUEST_CODE_BLUETOOTH
import cn.jinelei.rainbow.constant.BINDER_REQUEST_CODE_TEST
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock

class MainService : Service() {
	private val TAG = MainService::class.java.simpleName
	private lateinit var mBaseApp: BaseApp
	private lateinit var mContext: Context
	private var mBtLock = ReentrantLock()
	private var mConnectCond = mBtLock.newCondition()
	private var mConnectCdl = CountDownLatch(0)
	val allCallback = mutableMapOf<BluetoothDevice, Set<IConnectionCallback>>()
	
	val START_CONNECT_DEVICE = 0
	var handler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message) {
			when (msg.what) {
				START_CONNECT_DEVICE -> {
					val gatt = (msg.obj as BluetoothDevice).connectGatt(this@MainService, false, mGattCallback)
					Log.d(TAG, "return gatt $gatt")
				}
			}
		}
	}
	
	override fun onBind(intent: Intent): IBinder {
		return mBinderQuery
	}
	
	override fun onCreate() {
		super.onCreate()
		mBaseApp = application as BaseApp
		mContext = this@MainService
//		EventBus.getDefault().register(this)
	}
	
	override fun onDestroy() {
		super.onDestroy()
//		EventBus.getDefault().unregister(this)
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
	
	private val mGattCallback = object : BluetoothGattCallback() {
		override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
			Log.d(TAG, "onConnectionStateChange oldState: $status, newState: $newState")
			allCallback[gatt?.device]?.forEach { t -> t.onConnectionStateChange(newState, status) }
			when(newState){
				BluetoothProfile.STATE_CONNECTING->{
					Log.d(TAG, "连接中")
				}
				BluetoothProfile.STATE_CONNECTED->{
					Log.d(TAG, "连接成功")
					mConnectCdl.countDown()
					Log.d(TAG, "Connected to GATT server.")
					Log.d(TAG, "Attempting to start service discovery: ${gatt?.discoverServices()}")
				}
				BluetoothProfile.STATE_DISCONNECTING->{
					gatt?.let {
						it.close()
						it.disconnect()
					}
				}
				BluetoothProfile.STATE_DISCONNECTED->{
					gatt?.let {
						it.close()
						it.disconnect()
					}
				}
			}
		}
		
		override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
			Log.d(TAG, "onServicesDiscovered: ${gatt?.device?.address}")
			gatt?.let {
				Log.d(TAG, "${gatt.services}")
			}
		}
	}
	
	private val mBluetoothService = object : IBluetoothService.Stub() {
		override fun sendBytes(device: BluetoothDevice?, data: ByteArray?, priority: Int, isAck: Boolean): Long {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		override fun getConnectionState(device: BluetoothDevice?): Int {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		override fun disconnect(device: BluetoothDevice?) {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		override fun connect(device: BluetoothDevice?) {
			Log.d(TAG, "try connect to device: ${device?.address}")
			if (mConnectCdl.count != 0L) {
				Log.d(TAG, "等待连接")
				mConnectCdl.await()
			}
			mConnectCdl = CountDownLatch(1)
			Log.d(TAG, "开始连接")
			
			handler.sendMessage(Message.obtain().apply {
				what = START_CONNECT_DEVICE
				obj = device
			})
		}
		
		override fun registerConnectionCallback(device: BluetoothDevice, callback: IConnectionCallback?) {
			if (device == null || callback == null) {
				Log.e(TAG, "device or callback is null")
				return
			}
			if (allCallback.containsKey(device) && !allCallback[device]!!.contains(callback)) {
				allCallback[device]!!.plusElement(callback)
			}
		}
		
		override fun unregisterConnectionCallback(device: BluetoothDevice, callback: IConnectionCallback?) {
			if (device == null) {
				Log.e(TAG, "device is null")
				return
			}
			if (callback == null) {
				allCallback.remove(device)
			} else {
				allCallback[device]!!.minus(callback)
			}
		}
		
	}
	
}
