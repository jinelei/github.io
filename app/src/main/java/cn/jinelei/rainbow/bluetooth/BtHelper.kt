package cn.jinelei.rainbow.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import cn.jinelei.rainbow.constant.STATE_CONNECTING
import cn.jinelei.rainbow.constant.STATE_CONNECT_FAIL
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

interface IBtHelper {
	fun runOnMainThread(runnable: Runnable)
	fun runOnMainThreadDelayed(runnable: Runnable, delayMillis: Long)
	fun isMainThread(): Boolean
	fun onConnecting(gatt: BluetoothGatt)
	fun onConnected(gatt: BluetoothGatt)
	fun onDisconnecting(gatt: BluetoothGatt)
	fun onDisconnected(gatt: BluetoothGatt, reason: Int)
	fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, bytes: ByteArray)
	fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, bytes: ByteArray)
	fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, bytes: ByteArray)
	fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int)
	fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int)
	fun setConnectState(device: BluetoothDevice, state: Int)
}

class BtHelper : IBtHelper {
	private val TAG = BtHelper::class.java.simpleName
	private val mConnMapLock = Object()
	private val mConnectedConnMap = mutableMapOf<BluetoothDevice, Conn>()
	private val mAllConnMap = mutableMapOf<BluetoothDevice, Conn>()
	private val mIConnectListenerList = mutableListOf<IConnectListener>()
	private var receiveCommand: ReceiveCommand? = null
	private var sendCommand: SendCommand? = null
	private val handler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message?) {
			super.handleMessage(msg)
		}
	}
	
	override fun runOnMainThread(runnable: Runnable) {
		if (isMainThread()) {
			runnable.run()
		} else {
			handler.post(runnable)
		}
	}
	
	override fun runOnMainThreadDelayed(runnable: Runnable, delayMillis: Long) {
		handler.postDelayed(runnable, delayMillis)
	}
	
	override fun isMainThread(): Boolean {
		return Looper.myLooper() == Looper.getMainLooper()
	}
	
	override fun onConnecting(gatt: BluetoothGatt) {
		Log.d(TAG, "onConnecting: ${gatt.device.toString()}")
		var conn: Conn? = null
		synchronized(mConnMapLock) {
			mConnectedConnMap.remove(gatt.device)
			conn = mAllConnMap.get(gatt.device)
		}
		if (conn != null) {
			setConnectState(conn!!.device, STATE_CONNECTING)
		}
	}
	
	override fun onConnected(gatt: BluetoothGatt) {
		Log.d(TAG, "onConnected: ${gatt.device.toString()}")
		if (receiveCommand == null) {
			receiveCommand = ReceiveCommand(handler)
		}
		if (sendCommand == null) {
			sendCommand = SendCommand(handler, this)
		}
		var conn: Conn? = null
		synchronized(mConnMapLock) {
			conn = mAllConnMap[gatt.device]
		}
		conn?.let {
			it.onConnected(sendCommand, receiveCommand)
		}
	}
	
	override fun onDisconnecting(gatt: BluetoothGatt) {
		Log.d(TAG, "onConnectSuccess: ${gatt.device.toString()}")
	}
	
	override fun onDisconnected(gatt: BluetoothGatt, reason: Int) {
		Log.d(TAG, "onConnectSuccess: ${gatt.device.toString()}")
	}
	
	override fun onCharacteristicRead(
		gatt: BluetoothGatt,
		characteristic: BluetoothGattCharacteristic,
		bytes: ByteArray
	) {
		Log.d(TAG, "onConnectSuccess: ${gatt.device.toString()}")
	}
	
	override fun onCharacteristicWrite(
		gatt: BluetoothGatt,
		characteristic: BluetoothGattCharacteristic,
		bytes: ByteArray
	) {
		Log.d(TAG, "onConnectSuccess: ${gatt.device.toString()}")
	}
	
	override fun onCharacteristicChanged(
		gatt: BluetoothGatt,
		characteristic: BluetoothGattCharacteristic,
		bytes: ByteArray
	) {
		Log.d(TAG, "onConnectSuccess: ${gatt.device.toString()}")
	}
	
	override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
		Log.d(TAG, "onConnectSuccess: ${gatt.device.toString()}")
	}
	
	override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
		Log.d(TAG, "onConnectSuccess: ${gatt.device.toString()}")
	}
	
	override fun setConnectState(device: BluetoothDevice, state: Int) {
		Log.d(TAG, "iConnectListeners num = " + mIConnectListenerList.size + " device.size = " + mConnectedConnMap.size)
		for (connectListener in mIConnectListenerList) {
			connectListener.onConnectState(device, state)
		}
		if (state == STATE_CONNECT_FAIL) {
			receiveCommand?.dataClear(device)
		}
	}
	
}

class Conn(val device: BluetoothDevice) {
	fun onConnected(sendCommand: SendCommand, receiveCommand: ReceiveCommand) {
	
	}
}

interface IConnectListener {
	fun onConnectState(device: BluetoothDevice, state: Int)
}

class ReceiveCommand(handler: Handler) {
	fun dataClear(device: BluetoothDevice) {}
}

class SendCommand(handler: Handler, btHelper: BtHelper) {
	fun dataClear(device: BluetoothDevice) {}
}

class BtDataSender(
	val handler: Handler, val btHelper: BtHelper,
	var mIsRun: Boolean = true,
	val sequenceId: AtomicInteger = AtomicInteger(),
	val mInnerLock: ReentrantLock = ReentrantLock(),
	val mInnerCondition: Condition = mInnerLock.newCondition(),
	val mSendQueue: LinkedBlockingQueue<BtDataBean> = LinkedBlockingQueue()
) : Thread() {
	private val TAG = BtDataSender::class.java.simpleName
	private var mSingleFinish = true // 单次请求完成
	private var mWorkingCmdBean: BtDataBean? = null
	private var mEndCmdBean: BtDataBean? = null
	
	override fun run() {
		Log.d(TAG, "BtDataSender start")
		var cmdBean: BtDataBean? = null
		while (mIsRun) {
			mInnerLock.lock()
			try {
				do {
					if (mWorkingCmdBean == null) {
						mWorkingCmdBean = mSendQueue.poll()
						if (mWorkingCmdBean != null) {
							// 取得一个新命令
							mSingleFinish = false
							mEndCmdBean = null
							break
						}
					} else if (mSingleFinish) {
						mWorkingCmdBean = null
						continue
					}
					mInnerCondition.await()
				} while (mIsRun)
			} catch (e: InterruptedException) {
				Log.e(TAG, "InterruptedException")
				e.printStackTrace()
			} finally {
				mInnerLock.unlock()
			}
			cmdBean?.let {
				//				val flag = btHelper.writeToDevice(it.gatt, it.data)
				handler.sendMessage(Message.obtain().apply {
					what = START_SEND_TIMEOUT_TIMER
					obj = it.gatt
//					data = Bundle().apply { putBoolean("flag", flag) }
				})
				cmdBean = null
			}
			mInnerLock.unlock()
		}
		Log.d(TAG, "BtDataSender stop")
	}
	
	@Synchronized
	fun addCommand(gatt: BluetoothGatt, data: ByteArray?, isAck: Boolean) {
		Log.v(TAG, "addCommand:  mac: ${gatt.device.address}, size: ${data?.size}, isAck: $isAck")
		if (data == null || data.size < 0) {
			Log.e(TAG, "addCommand: data is null or data.size == 0")
			return
		}
		mInnerLock.lock()
		mSendQueue.offer(
			BtDataBean(
				gatt = gatt,
				data = data,
				isAck = isAck,
				retry = 0,
				sequence_id = sequenceId.incrementAndGet()
			)
		)
		mInnerCondition.signalAll()
		mInnerLock.unlock()
	}
	
	fun cancel() {
		mInnerLock.lock()
		mIsRun = false
		mSendQueue.clear()
		mWorkingCmdBean = null
		mInnerCondition.signalAll()    // UnLock
		mInnerLock.unlock()
		
		Log.d("KCTSend", "KCTSendCommand is cancel")
	}
	
	fun reCancel(gatt: BluetoothGatt?, throwable: Throwable?) {
		var workingCmdBean: BtDataBean? = null
		
		mInnerLock.lock()
		if (mWorkingCmdBean != null && gatt != null && gatt == mWorkingCmdBean!!.gatt) {
			workingCmdBean = mWorkingCmdBean
			mSingleFinish = true
			mInnerCondition.signal()
		}
		mInnerLock.unlock()
		
		if (throwable != null && workingCmdBean != null) {
			Log.w("KCTSend", "send data occur some error", throwable)
			handler.sendMessage(Message.obtain().apply {
				what = CLEAR_RECEIVE_DATA
				obj = workingCmdBean.gatt
			})
			handler.sendMessage(Message.obtain().apply {
				what = CANCEL_SEND_TIMEOUT_TIMER
				obj = workingCmdBean.gatt
			})
		}
	}
	
	// 发送数据方法，实际发送数据由sendData@DataSenderCallback完成
	private fun doSendData() {
		Log.v(TAG, "doSendData")
		mWorkingCmdBean?.let {
			// 当次请求未完成，自旋等待
			Log.v(TAG, "spin enter")
			var retryCount = 10
			while (!mSingleFinish && --retryCount > 0) {
				sleep(DEFAULT_SLEEP_TIME)
				yield()
			}
			when (mSingleFinish) {
				true -> {
					Log.v(TAG, "spin leave")
					it.status = BtDataBeanStatus.SENDING
					it.updateDeadline()
				}
				false -> { // 超出重试次数退出，抛出异常退出
					onError(EXCEEDED_RETRIES)
				}
			}
		}
	}
	
	// 发送完成的回调方法
	fun onSent(data: ByteArray) {
		mWorkingCmdBean?.let {
			mInnerLock.lock()
			mSingleFinish = !it.isAck
			try {
				if (it.data === data) {
					Log.v(TAG, "onSent")
					it.status = BtDataBeanStatus.SENT
					mInnerCondition.signal()
				}
			} finally {
				mInnerLock.unlock()
			}
		}
	}
	
	// 收到数据的回调方法
	fun onReceived(data: ByteArray) {
		mWorkingCmdBean?.let {
			if (it.isAck)
				mSingleFinish = true
			mInnerLock.lock()
			mSingleFinish = !it.isAck
			try {
				if (it.data === data) {
					Log.v(TAG, "onReceived")
					it.status = BtDataBeanStatus.END
					mInnerCondition.signal()
				}
			} finally {
				mInnerLock.unlock()
			}
		}
	}
	
	// 出错
	fun onError(reason: String? = null, data: ByteArray? = null) {
		try {
			mInnerLock.lock()
			Log.v(TAG, "onError $reason")
			if (mWorkingCmdBean != null && data != null && mWorkingCmdBean?.data === data) {
				mWorkingCmdBean?.status = BtDataBeanStatus.END
				mInnerCondition.signal()
			}
			mIsRun = false // 出错停止线程
		} finally {
			mInnerLock.unlock()
		}
	}
}

interface DataSenderCallback {
	fun sendData(data: ByteArray)
	fun onReceivedData(data: ByteArray)
}

class BtDataBean(
	val gatt: BluetoothGatt,
	val data: ByteArray,
	val isAck: Boolean = false,
	var retry: Int = 3,
	val sequence_id: Int,
	var status: BtDataBeanStatus = BtDataBeanStatus.INIT
) {
	var deadline = Date(System.currentTimeMillis())
	fun updateDeadline() {
		deadline = when (isAck) {
			true -> Date(System.currentTimeMillis() + WITH_ACK_TIMEOUT)
			false -> Date(System.currentTimeMillis() + WITHOUT_ACK_TIMEOUT)
		}
	}
}

enum class BtDataBeanStatus(val code: Int, val status: String) {
	INIT(0, "init"),
	SENDING(1, "sending"),
	SENT(2, "sent"),
	END(3, "end")
}