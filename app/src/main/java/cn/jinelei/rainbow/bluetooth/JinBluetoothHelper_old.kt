package cn.jinelei.rainbow.bluetooth

//import android.bluetooth.*
//import android.content.Context
//import android.os.Handler
//import android.util.Log
//import cn.jinelei.rainbow.constant.DEFAULT_MTU_SIZE
//import java.util.*
//import java.util.concurrent.atomic.AtomicInteger
//import java.util.concurrent.locks.Condition
//import java.util.concurrent.locks.Lock
//import java.util.concurrent.locks.ReentrantLock
//
//class JinBluetoothHelper(val context: Context) : IBluetoothGattCallback {
//	override fun onConnecting(gatt: BluetoothGatt) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onConnectSuccess(gatt: BluetoothGatt) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onDisconnecting(gatt: BluetoothGatt) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onDisconnect(gatt: BluetoothGatt, status: Int) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onCharacteristicRead(
//		gatt: BluetoothGatt,
//		char: BluetoothGattCharacteristic,
//		status: Int,
//		value: ByteArray?
//	) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onCharacteristicWrite(
//		gatt: BluetoothGatt,
//		char: BluetoothGattCharacteristic,
//		status: Int,
//		value: ByteArray
//	) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onCharacteristicChanged(
//		gatt: BluetoothGatt,
//		characteristic: BluetoothGattCharacteristic,
//		value: ByteArray
//	) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
//		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//	}
//
//	private var mBluetoothManager: BluetoothManager =
//		context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//	private var mBluetoothAdapter: BluetoothAdapter = mBluetoothManager.adapter
//	private var mBluetoothDevice: BluetoothDevice? = null
//	private var mConnectAddress: String? = null
//	private var mGattCallback: BluetoothGattCallback? = null
//	private var state: BtConnectState = BtConnectState.NONE
//
//	private var RxChar: BluetoothGattCharacteristic? = null     // 特征值
//	private var mtuSize = DEFAULT_MTU_SIZE    //  默认Mtu大小
//	private var RxService: BluetoothGattService? = null            //GattService
//	private var serviceUuid: UUID? = null                          //蓝牙UUID
//
//
//	private fun connectDevice(address: String, listener: List<JinConnectCallback>) {
//		if (mBluetoothManager == null || mBluetoothAdapter == null || address == null)
//			return
//	}
//
//}
//
//class Conn(val device: BluetoothLeDevice, val btHelper: JinBluetoothHelper) {
//	private lateinit var mBluetoothGattCallback: BluetoothGattCallback
//	private lateinit var mBluetoothGatt: BluetoothGatt
//	private lateinit var mConnectTimeoutRunnable: ConnectTimeoutRunnable
//
//	init {
//
//	}
//
//}
//
//class ConnectTimeoutRunnable(private val helper: JinBluetoothHelper, private val mBluetoothGatt: BluetoothGatt) :
//	Runnable {
//	override fun run() {
//		Log.v("ConnectTimeoutRunnable", "${mBluetoothGatt.device.address} connect timeout")
//		helper.disconnect(mBluetoothGatt)
//	}
//}
//
//class SendDataTimeoutRunnable(
//	private val mSendCommand: SendCommand?,
//	private val mReceiveCommand: ReceiveCommand?,
//	private val handler: Handler,
//	private val gatt: BluetoothGatt
//) : Runnable {
//	override fun run() {
//		mSendCommand?.let {
//			mReceiveCommand.getDataBuffer(gatt.device)?.let {
//				if (it.burDataBegin) {
//					mSendCommand.reCancel(gatt, Throwable("send data timeout"))
//					handler.postDelayed(this, 8000)
//				}
//			}
//		}
//	}
//}
//
//class SendCommand(private val handler: Handler, private val helper: JinBluetoothHelper) : Thread() {
//	private val TAG = SendCommand::class.java.simpleName
//	private var mIsRun = false
//	private val mInnerLock: Lock
//	private val mInnerCondition: Condition
//	private val nSendQueue = LinkedList<BleCmdBean>()
//	private var mWorkingCmdBean: BleCmdBean? = null
//	private var cmdEnd = true
//	private val sequenceId = AtomicInteger()
//
//	init {
//		mIsRun = true
//		mInnerLock = ReentrantLock()
//		mInnerCondition = mInnerLock.newCondition()
//	}
//
//	@Synchronized
//	private fun addCommand(gatt: BluetoothGatt, data: ByteArray?) {
//		data?.let {
//			val bean = BleCmdBean(
//				content = data,
//				retry = 0,
//				gatt = gatt,
//				sequence_id = sequenceId.getAndIncrement()
//			)
//			mInnerLock.lock()
//			nSendQueue.offer(bean)
//			mInnerCondition.signal()
//			mInnerLock.unlock()
//		}
//	}
//
//	override fun run() {
//		Log.d("JinSend", "JinSendCommand is run")
//		var cmdBean: BleCmdBean? = null
//		while (mIsRun) {
//			mInnerLock.lock()
//			try {
//				do {
//					if (mWorkingCmdBean == null) {
//						// 需要新的命令
//						mWorkingCmdBean = nSendQueue.poll()
//						if (mWorkingCmdBean != null) {
//							// 取到了一个新的命令
//							cmdEnd = false
//							cmdBean = mWorkingCmdBean
//							break
//						}
//					} else if (cmdEnd) {
//						// 命令结束
//						mWorkingCmdBean = null
//						// 准备取下一个命令
//						continue
//					}
//
//					// 没有任何确切的事件，等待条件
//					mInnerCondition.await()
//				} while (mIsRun)
//			} catch (e: InterruptedException) {
//				Log.w(TAG, "JinSendCommand mInnerCondition.await()", e)
//			} finally {
//				mInnerLock.unlock()
//			}
//
//			cmdBean?.let {
//				val flag = helper.writeToDevice(it.gatt, it.content)
//				updateDataBuffer(JinBluetoothHelper.Handler.START_SEND_TIMEOUT_TIMER, cmdBean!!.gatt, flag)
//				cmdBean = null
//			}
//		}
//
//		Log.d("JinSend", "JinSendCommand is stop")
//	}
//
//	fun cancel() {
//		mInnerLock.lock()
//		mIsRun = false
//		nSendQueue.clear()
//		mWorkingCmdBean = null
//		mInnerCondition.signalAll()    // UnLock
//		mInnerLock.unlock()
//
//		Log.d("KCTSend", "KCTSendCommand is cancel")
//	}
//}
//
//class ReceiveCommand {
//
//}
//
//class BleCmdBean(
//	val content: ByteArray,
//	private val retry: Int,
//	val gatt: BluetoothGatt,
//	private val sequence_id: Int
//) {}
//
//class BluetoothLeDevice(val device: BluetoothDevice, val name: String?, val rssi: Int?, val scanRecord: ByteArray?) {
//	constructor(dev: BluetoothDevice) : this(dev, dev.name, -127, null)
//	constructor(dev: BluetoothDevice, name: String) : this(dev, name, -127, null)
//	constructor(dev: BluetoothDevice, scanRecord: ByteArray) : this(dev, dev.name, -127, scanRecord)
//	constructor(dev: BluetoothDevice, rssi: Int) : this(dev, dev.name, rssi, null)
//	constructor(dev: BluetoothDevice, name: String, scanRecord: ByteArray) : this(dev, name, -127, scanRecord)
//	constructor(dev: BluetoothDevice, name: String, rssi: Int) : this(dev, name, rssi, null)
//	constructor(dev: BluetoothDevice, rssi: Int, scanRecord: ByteArray) : this(dev, dev.name, rssi, scanRecord)
//}
//
//enum class BtConnectState(val state: String) {
//	NONE("NONE"),
//	CONNECTED("CONNECTED"),
//	CONNECTING("CONNECTING"),
//	DISCONNECTED("DISCONNECTED"),
//}
//
//interface IBluetoothGattCallback {
//	fun onConnecting(gatt: BluetoothGatt)
//	fun onConnectSuccess(gatt: BluetoothGatt)
//	fun onDisconnecting(gatt: BluetoothGatt)
//	fun onDisconnect(gatt: BluetoothGatt, status: Int)
//	fun onCharacteristicRead(gatt: BluetoothGatt, char: BluetoothGattCharacteristic, status: Int, value: ByteArray?)
//	fun onCharacteristicWrite(gatt: BluetoothGatt, char: BluetoothGattCharacteristic, status: Int, value: ByteArray)
//	fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray)
//	fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int)
//	fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int)
//}