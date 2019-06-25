package cn.jinelei.rainbow.ui.activity

import android.Manifest
import android.bluetooth.*
import android.bluetooth.BluetoothDevice.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import cn.jinelei.rainbow.IBinderQuery
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.bluetooth.IBluetoothService
import cn.jinelei.rainbow.bluetooth.IConnectionCallback
import cn.jinelei.rainbow.constant.*
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import cn.jinelei.rainbow.util.isFastClick
import kotlinx.android.synthetic.main.activity_scan_device.*
import kotlinx.android.synthetic.main.device_info_layout.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Runnable
import java.util.*

val RX_SERVICE_UUIDS = listOf<UUID>(
	UUID.fromString("C3E6FEA0-E966-1000-8000-BE99C223DF6A"),
	UUID.fromString("b75c49d2-04a3-4071-a0b5-35853eb08307"),
	UUID.fromString("0783B03E-8535-B5A0-7140-A304D2495CB7")
)

val RX_CHAR_UUIDS = listOf<UUID>(
	UUID.fromString("C3E6FEA1-E966-1000-8000-BE99C223DF6A"),
	UUID.fromString("C3E6FEA2-E966-1000-8000-BE99C223DF6A")
)

val TX_CHAR_UUIDS = listOf<UUID>(
	UUID.fromString("C3E6FEA1-E966-1000-8000-BE99C223DF6A"),
	UUID.fromString("0783b03e-8535-b5a0-7140-a304d2495cb8")
)

val DESC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

class ScanDeviceActivity : BaseActivity() {
	private lateinit var rvDeviceScanResult: RecyclerView
	private lateinit var tvDeviceScanTitle: TextView
	private lateinit var tvDeviceScanInfoNothing: TextView
	private lateinit var ivNavRight: ImageView
	private var scanning = false
	private var autoStopScanJob: Job? = null
	private var mBluetoothGatt: BluetoothGatt? = null
	private var mBluetoothService: IBluetoothService? = null
	
	private var mRxService: BluetoothGattService? = null
	private var mRxServiceUUID: UUID? = null
	private var mRxChar: BluetoothGattCharacteristic? = null
	private var mRxCharUUID: UUID? = null
	private var mTxChar: BluetoothGattCharacteristic? = null
	private var mTxCharUUID: UUID? = null
	
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
	private val connection = object : ServiceConnection {
		override fun onServiceDisconnected(name: ComponentName?) {
			mBluetoothService = null
		}
		
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			val mBinderQuery = IBinderQuery.Stub.asInterface(service)
			mBluetoothService =
				IBluetoothService.Stub.asInterface(mBinderQuery.queryBinder(BINDER_REQUEST_CODE_BLUETOOTH))
		}
	}
	private val mConnectionCallback = object : IConnectionCallback {
		override fun onConnectionStateChange(newStatus: Int, oldState: Int) {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		override fun asBinder(): IBinder {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
		override fun onBytesReceived(dataBuffer: ByteArray?) {
			TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
		}
		
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initView()
		initData()
	}
	
	private fun initData() {
		EventBus.getDefault().register(this)
		prepareScanDevice()
	}
	
	private fun initView() {
		setContentView(R.layout.activity_scan_device)
		rvDeviceScanResult = rv_device_scan_info.apply {
			layoutManager = LinearLayoutManager(this@ScanDeviceActivity)
			this.addItemDecoration(
				DividerItemDecoration(
					this@ScanDeviceActivity,
					DividerItemDecoration.VERTICAL
				)
			)
			itemAnimator = DefaultItemAnimator()
			adapter = BaseRecyclerAdapter(
				itemLayoutId = R.layout.device_info_layout,
				dataSet = mutableListOf<BluetoothDevice>()
			) {
				onBindViewHolder { holder, position ->
					val device = getItem(position)
					holder.tv_name.text = device.name
					holder.tv_mac.text = device.address
					// 点击事件
					holder.layout_device_info.let {
						mBaseApp.debug(
							Log.VERBOSE,
							"device address: ${device.address} fetchUuidsWithSdp: ${device.fetchUuidsWithSdp()} uuid: ${device.uuids}"
						)
						it.setOnClickListener {
							connectGatt(getItem(position))
						}
					}
					// 蓝牙类型
					holder.tv_type.let {
						it.text = when (getItem(position).type) {
							DEVICE_TYPE_CLASSIC -> "classic"
							DEVICE_TYPE_LE -> "le"
							DEVICE_TYPE_DUAL -> "dual"
							else -> ""
						}
					}
				}
			}
		}
		tvDeviceScanInfoNothing = tv_device_scan_info_nothing.apply {
			setOnClickListener { if (!isFastClick(this)) prepareScanDevice() }
		}
		tvDeviceScanTitle = tv_nav_title.apply {
			text = resources.getString(R.string.scan_device)
		}
		ivNavRight = iv_nav_right.apply {
			setImageResource(R.mipmap.ic_query)
			setOnClickListener { if (!isFastClick(tvDeviceScanInfoNothing)) prepareScanDevice() }
		}
		iv_nav_left.apply {
			setImageResource(R.mipmap.ic_back)
			setOnClickListener { finish() }
		}
	}
	
	private fun destroyData() {
		EventBus.getDefault().unregister(this)
		mBluetoothGatt?.let {
			it.close()
			it.disconnect()
		}
		if (scanning) {
			stopScanDevice()
		}
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			REQUEST_CODE_OPEN_BT -> mBaseApp.debug(Log.INFO, "resultCode: $resultCode, data: $data")
			else -> super.onActivityResult(requestCode, resultCode, data)
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		destroyData()
		EventBus.getDefault().unregister(this)
	}
	
	//    显示设备uuids
	private fun displayUuids(mBluetoothGatt: BluetoothGatt) {
		val services = mBluetoothGatt.services
		mBaseApp.debug(Log.DEBUG, "displayUuids: ${mBluetoothGatt.device.name} services: $services")
		if (services != null && services.size != 0) {
			GlobalScope.launch(Main) {
				alertDialogBuilder.let {
					it.setTitle(R.string.all_uuid_service)
					val rvUuidService = RecyclerView(mContext).apply {
						this.addItemDecoration(
							DividerItemDecoration(
								this@ScanDeviceActivity,
								DividerItemDecoration.VERTICAL
							)
						)
						this.itemAnimator = DefaultItemAnimator()
						this.layoutManager = LinearLayoutManager(this@ScanDeviceActivity)
						this.adapter = BaseRecyclerAdapter<BluetoothGattService>(
							itemLayoutId = R.layout.device_uuid_layout,
							dataSet = services
						) {
							onBindViewHolder { holder, position ->
								holder.tv_name.text = getItem(position).uuid.toString()
							}
						}
					}
					it.setView(rvUuidService)
					it.create()
					it.show()
				}
			}
		}
	}
	
	private val mBluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
		override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
			super.onCharacteristicChanged(gatt, characteristic)
		}
		
		override fun onCharacteristicRead(
			gatt: BluetoothGatt?,
			characteristic: BluetoothGattCharacteristic?,
			status: Int
		) {
			super.onCharacteristicRead(gatt, characteristic, status)
		}
		
		override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
			super.onReadRemoteRssi(gatt, rssi, status)
		}
		
		override fun onCharacteristicWrite(
			gatt: BluetoothGatt?,
			characteristic: BluetoothGattCharacteristic?,
			status: Int
		) {
			super.onCharacteristicWrite(gatt, characteristic, status)
		}
		
		override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
			mBaseApp.debug(Log.VERBOSE, "onServicesDiscovered gatt: ${gatt?.device?.address} status: $status")
			gatt?.let {
				mBluetoothGatt = gatt
				mRxService = null
				mRxServiceUUID = null
				for (gattService: BluetoothGattService in it.services) {
					if (RX_SERVICE_UUIDS.contains(gattService.uuid)) {
						mRxService = gattService
						mRxServiceUUID = gattService.uuid
						break
					}
				}
				if (mRxService == null || mRxServiceUUID == null) {
					mHandler.sendEmptyMessage(HANDLER_ERROR_SERVICE)
					mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_HIDE)
					return
				}
				mRxChar = null
				mRxCharUUID = null
				for (characteristics: BluetoothGattCharacteristic in mRxService!!.characteristics) {
					if (TX_CHAR_UUIDS.contains(characteristics.uuid)) {
						mRxChar = characteristics
						mRxCharUUID = characteristics.uuid
					}
				}
				if (mRxChar == null || mRxCharUUID == null) {
					mHandler.sendEmptyMessage(HANDLER_ERROR_SERVICE)
					mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_HIDE)
					return
				}
				mTxChar = null
				mTxCharUUID = null
				for (characteristics: BluetoothGattCharacteristic in mRxService!!.characteristics) {
					if (TX_CHAR_UUIDS.contains(characteristics.uuid)) {
						mTxChar = characteristics
						mTxCharUUID = characteristics.uuid
					}
				}
				if (mTxChar == null || mTxCharUUID == null) {
					mHandler.sendEmptyMessage(HANDLER_ERROR_SERVICE)
					mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_HIDE)
					return
				}
				gatt.setCharacteristicNotification(mRxChar, true)
				gatt.setCharacteristicNotification(mTxChar, true)
				for (descriptor: BluetoothGattDescriptor in mTxChar!!.descriptors) {
					if (descriptor.uuid != null && descriptor.uuid.equals(DESC)) {
						when (descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
							true -> {
								gatt.writeDescriptor(descriptor)
							}
							false -> {
								gatt.disconnect()
							}
						}
					}
				}
				gatt.readRemoteRssi()
				mBaseApp.debug(Log.VERBOSE, "")
				mHandler.sendEmptyMessage(HANDLER_CONNECTED)
			}
		}
		
		override fun onPhyUpdate(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
			super.onPhyUpdate(gatt, txPhy, rxPhy, status)
		}
		
		override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
			super.onMtuChanged(gatt, mtu, status)
		}
		
		override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
			super.onReliableWriteCompleted(gatt, status)
		}
		
		override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
			super.onDescriptorWrite(gatt, descriptor, status)
		}
		
		override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
			super.onDescriptorRead(gatt, descriptor, status)
		}
		
		override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
			super.onPhyRead(gatt, txPhy, rxPhy, status)
		}
		
		override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
			mBaseApp.debug(
				Log.VERBOSE,
				"onConnectionStateChange gatt: ${gatt?.device?.address} status: $status, newState: $newState"
			)
			mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_HIDE)
			when (newState) {
				BluetoothProfile.STATE_DISCONNECTED -> {
					mBaseApp.debug(
						Log.VERBOSE,
						"STATE_DISCONNECTED gatt: ${gatt?.device?.address}"
					)
				}
				BluetoothProfile.STATE_CONNECTED -> {
					mBaseApp.debug(
						Log.VERBOSE,
						"STATE_CONNECTED startDiscoverServices: ${gatt?.discoverServices()}"
					)
				}
			}
		}
	}
	
	//    连接蓝牙
	private fun connectGatt(device: BluetoothDevice) {
		mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_SHOW)
		device.connectGatt(this@ScanDeviceActivity, false, mBluetoothGattCallback)
//		mBluetoothService?.registerConnectionCallback(device, mConnectionCallback)
//		mBluetoothService?.connect(device)
//		stopScanDevice()
	}
	
	//    重置设备列表
	private fun resetDevice() {
		(rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).clear()
		rvDeviceScanResult.visibility =
			when ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.size) {
				0 -> View.INVISIBLE
				else -> View.VISIBLE
			}
		tvDeviceScanInfoNothing.visibility =
			when ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.size) {
				0 -> View.VISIBLE
				else -> View.INVISIBLE
			}
	}
	
	//    准备开始扫描设备
	private fun prepareScanDevice() {
		mBaseApp.debug(Log.DEBUG, "scanning: $scanning enabled: ${mBaseApp.mBluetoothAdapter.isEnabled}")
		if (scanning) {
			stopScanDevice()
		} else {
			if (mBaseApp.mBluetoothAdapter.isEnabled) {
				setNecessaryPermission(
					listOf(
						Manifest.permission.BLUETOOTH,
						Manifest.permission.BLUETOOTH_ADMIN,
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION
					),
					Runnable {
						startScanDevice()
						autoStopScanJob = GlobalScope.launch(IO) {
							delay(DEFAULT_BLUETOOTH_SCAN_TIMEOUT)
							stopScanDevice()
						}
					},
					Runnable { mBaseApp.toast(getString(R.string.failed_to_turn_on_bluetooth_please_turn_on_bluetooth_permissions_manually)) },
					Runnable { showExplainDialog() }
				)
			} else {
				setNecessaryPermission(
					listOf(
						Manifest.permission.BLUETOOTH,
						Manifest.permission.BLUETOOTH_ADMIN
					),
					Runnable {
						startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_OPEN_BT)
					},
					Runnable { mBaseApp.toast(getString(R.string.failed_to_turn_on_bluetooth__please_turn_on_bluetooth_manually)) },
					Runnable { showExplainDialog() }
				)
			}
		}
	}
	
	//    停止扫描
	private fun stopScanDevice() {
		mBaseApp.mBluetoothAdapter.bluetoothLeScanner.stopScan(mScanCallback)
		autoStopScanJob?.let {
			if (it.isActive && !it.isCompleted && !it.isCancelled) {
				mBaseApp.debug(Log.DEBUG, "cancel auto stop scan job")
				it.cancel()
			}
		}
		ivNavRight.let {
			it.setImageDrawable(resources.getDrawable(R.mipmap.ic_query))
			it.clearAnimation()
		}
		scanning = false
	}
	
	//    开始扫描
	private fun startScanDevice() {
		mBaseApp.debug(Log.VERBOSE, "start detect bt")
		resetDevice()
		mBaseApp.mBluetoothAdapter.bluetoothLeScanner.startScan(mScanCallback)
		ivNavRight.let {
			it.setImageDrawable(resources.getDrawable(R.mipmap.ic_loading))
			it.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.loading_anim))
		}
		scanning = true
	}
	
	//    打开解释权限对话框
	private fun showExplainDialog() {
		GlobalScope.launch(Dispatchers.Main) {
			alertDialogBuilder.apply {
				setTitle(getString(R.string.please_grant_application_permission))
				setView(TextView(this@ScanDeviceActivity).apply {
					text = getString(R.string.please_grant_application_permission)
					setPadding(
						resources.getDimensionPixelOffset(R.dimen.default_padding),
						resources.getDimensionPixelOffset(R.dimen.default_padding),
						resources.getDimensionPixelOffset(R.dimen.default_padding),
						resources.getDimensionPixelOffset(R.dimen.default_padding)
					)
				})
				setPositiveButton(
					getString(R.string.ok)
				) { _, _ ->
					startActivity(Intent().apply {
						action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
						data = Uri.fromParts("package", packageName, null)
					})
				}
				create()
				show()
			}
		}
	}
	
	//    添加设备到列表
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun addSingleDevice(mBtScanResult: BtScanResult) {
		if (mBtScanResult.device.name.isNullOrBlank())
			return
		val device = mBtScanResult.device
		if ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.filter { dev: BluetoothDevice -> dev.address == device.address }.isEmpty()) {
			(rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).append(device)
			rvDeviceScanResult.visibility =
				when ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.size) {
					0 -> View.INVISIBLE
					else -> View.VISIBLE
				}
			tvDeviceScanInfoNothing.visibility =
				when ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.size) {
					0 -> View.VISIBLE
					else -> View.INVISIBLE
				}
		}
	}
	
	private val HANDLER_START_SCAN = 10
	private val HANDLER_STOP_SCAN = 11
	private val HANDLER_ERROR_SERVICE = 30
	private val HANDLER_CONNECTED = 40
	private val mHandler = object : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message?) {
			when (msg?.what) {
				HANDLER_START_SCAN -> {
				
				}
				HANDLER_STOP_SCAN -> {
				
				}
				HANDLER_ERROR_SERVICE -> {
					mBaseApp.toast("unsupport device")
				}
				HANDLER_CONNECTED -> {
					mBaseApp.toast("连接成功")
				}
			}
		}
	}
}
