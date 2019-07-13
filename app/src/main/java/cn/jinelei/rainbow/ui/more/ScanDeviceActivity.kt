package cn.jinelei.rainbow.ui.more

import android.Manifest
import android.bluetooth.*
import android.bluetooth.BluetoothDevice.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
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
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.constant.*
import cn.jinelei.rainbow.ui.base.adapter.BaseRecyclerAdapter
import cn.jinelei.rainbow.util.discoveryAllUUIDs
import cn.jinelei.rainbow.util.isFastClick
import kotlinx.android.synthetic.main.activity_scan_device.*
import kotlinx.android.synthetic.main.device_info_layout.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Runnable
import java.util.*

/**
 * 小米手环2的所有UUID
蓝牙发现服务回调 CA:20:AB:0E:99:CE
服务UUID 00001800-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a00-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a01-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a04-0000-1000-8000-00805f9b34fb
服务UUID 00001801-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a05-0000-1000-8000-00805f9b34fb
服务UUID 0000180a-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a25-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a27-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a28-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a23-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a50-0000-1000-8000-00805f9b34fb
服务UUID 00001530-0000-3512-2118-0009af100700
->  特征UUID 00001531-0000-3512-2118-0009af100700
->  特征UUID 00001532-0000-3512-2118-0009af100700
服务UUID 00001811-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a46-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a44-0000-1000-8000-00805f9b34fb
服务UUID 00001802-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a06-0000-1000-8000-00805f9b34fb
服务UUID 0000180d-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a37-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a39-0000-1000-8000-00805f9b34fb
服务UUID 0000fee0-0000-1000-8000-00805f9b34fb
->  特征UUID 00002a2b-0000-1000-8000-00805f9b34fb
->  特征UUID 00000020-0000-3512-2118-0009af100700
->  特征UUID 00000001-0000-3512-2118-0009af100700
->  特征UUID 00000002-0000-3512-2118-0009af100700
->  特征UUID 00000003-0000-3512-2118-0009af100700
->  特征UUID 00002a04-0000-1000-8000-00805f9b34fb
->  特征UUID 00000004-0000-3512-2118-0009af100700
->  特征UUID 00000005-0000-3512-2118-0009af100700
->  特征UUID 00000006-0000-3512-2118-0009af100700
->  特征UUID 00000007-0000-3512-2118-0009af100700
->  特征UUID 00000008-0000-3512-2118-0009af100700
->  特征UUID 00000010-0000-3512-2118-0009af100700
服务UUID 0000fee1-0000-1000-8000-00805f9b34fb    // 用于认证
->  特征UUID 00000009-0000-3512-2118-0009af100700    // 认证
->  特征UUID 0000fedd-0000-1000-8000-00805f9b34fb
->  特征UUID 0000fede-0000-1000-8000-00805f9b34fb
->  特征UUID 0000fedf-0000-1000-8000-00805f9b34fb
->  特征UUID 0000fed0-0000-1000-8000-00805f9b34fb
->  特征UUID 0000fed1-0000-1000-8000-00805f9b34fb
->  特征UUID 0000fed2-0000-1000-8000-00805f9b34fb
->  特征UUID 0000fed3-0000-1000-8000-00805f9b34fb
->  特征UUID 0000fec1-0000-3512-2118-0009af100700
 */

class ScanDeviceActivity : BaseActivity() {
	private var mConnGatt: BluetoothGatt? = null
	private var mConnDesc: BluetoothGattDescriptor? = null
	private var mConnChar: BluetoothGattCharacteristic? = null
	private var mConnService: BluetoothGattService? = null
	private val TAG = ScanDeviceActivity::class.java.simpleName
	private lateinit var rvDeviceScanResult: RecyclerView
	private lateinit var tvDeviceScanTitle: TextView
	private lateinit var tvDeviceScanInfoNothing: TextView
	private lateinit var ivNavRight: ImageView
	
	private val mServiceUUIDs = mutableListOf<UUID>()
	private val mCharacteristicUUIDs = mutableListOf<UUID>()
	private val mDescriptorUUIDs = mutableListOf<UUID>()
	
	private var mBleScanning = false
	private var autoStopScanJob: Job? = null
	private var mBluetoothGatt: BluetoothGatt? = null
	
	private var mScanCallback: ScanCallback = object : ScanCallback() {
		override fun onScanFailed(errorCode: Int) {
			Log.d(TAG, "扫描蓝牙设备失败：$errorCode")
			EventBus.getDefault().post(BtScanStatus())
		}
		
		override fun onScanResult(callbackType: Int, result: ScanResult?) {
			result?.let {
				Log.d(TAG, "扫描蓝牙设备成功：${it.toString()}")
				EventBus.getDefault().post(BtScanResult(it.device))
			}
		}
		
		override fun onBatchScanResults(results: MutableList<ScanResult>) {
			super.onBatchScanResults(results)
			Log.d(TAG, "批量扫描蓝牙设备成功：${results.size}")
			EventBus.getDefault().post(BtBatchScanResults(results))
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initView()
		initData()
	}
	
	private fun initData() {
		EventBus.getDefault().register(this)
		initUUID()
		startScanBleDevice()
	}
	
	private fun initUUID() {
		mServiceUUIDs.let {
			it.clear()
		}
		mCharacteristicUUIDs.let {
			it.clear()
			it.addAll(
				listOf(
					UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb"), /* alert */
					UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb"), /* 计步 */
					UUID.fromString("0000ff0c-0000-1000-8000-00805f9b34fb"), /* 电量信息 */
					UUID.fromString("0000ff04-0000-1000-8000-00805f9b34fb"), /* 用户信息 */
					UUID.fromString("0000ff05-0000-1000-8000-00805f9b34fb") /* 控制点 */
				)
			)
		}
		mDescriptorUUIDs.let {
			it.clear()
		}
		
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
						it.setOnClickListener {
							tryConnectBle(getItem(position))
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
			setOnClickListener { if (!isFastClick(this)) startScanBleDevice() }
		}
		tvDeviceScanTitle = tv_nav_title.apply {
			text = resources.getString(R.string.scan_device)
		}
		ivNavRight = iv_nav_right.apply {
			setImageResource(R.mipmap.ic_query)
			setOnClickListener { if (!isFastClick(tvDeviceScanInfoNothing)) startScanBleDevice() }
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
		if (mBleScanning) {
			stopScanBleDevice()
		}
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when (requestCode) {
			REQUEST_CODE_OPEN_BT -> Log.d(TAG, "请求打开蓝牙回调：resultCode: $resultCode, data: $data")
			else -> super.onActivityResult(requestCode, resultCode, data)
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		destroyData()
		EventBus.getDefault().unregister(this)
	}
	
	private val mBluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
		
		override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
			Log.d(TAG, "蓝牙发现服务回调 ${gatt?.device?.address}: status: $status")
			mBaseApp.mBluetoothAdapter.cancelDiscovery()
			when (status) {
				BluetoothGatt.GATT_SUCCESS -> gatt?.let {
					discoveryAllUUIDs(it)
					mConnGatt = it
					mConnService = it.getService(UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb"))
					mConnChar = mConnService?.getCharacteristic(UUID.fromString("00000009-0000-3512-2118-0009af100700"))
					it.setCharacteristicNotification(mConnChar, true)
					mConnDesc = mConnChar?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
					
					if (mConnDesc != null) {
						mConnDesc!!.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
						val resultCode = it.writeDescriptor(mConnDesc)
						EventBus.getDefault().post("found")
					}
				}
			}
		}
		
		override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
			when (newState) {
				BluetoothProfile.STATE_DISCONNECTED -> {
					mBaseApp.savePreference(PRE_NAME_MINE, PRE_KEY_BT_DEVICE_MAC, "")
					mBaseApp.mConnectedBleGatt.remove(gatt)
					mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_HIDE)
				}
				BluetoothProfile.STATE_CONNECTED -> {
					Log.d(TAG, "设备已连接：${gatt.device.address}, 准备发现服务: ${gatt.discoverServices()}")
					mBaseApp.savePreference(PRE_NAME_MINE, PRE_KEY_BT_DEVICE_MAC, gatt.device?.address)
					mBaseApp.mConnectedBleGatt.add(gatt)
					mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_HIDE)
				}
			}
		}
		
		override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
			Log.d(TAG, "特征已改变 ${characteristic.uuid} 数据：${characteristic.value}")
			super.onCharacteristicChanged(gatt, characteristic)
		}
		
		override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
			Log.d(TAG, "写入描述 ${descriptor.uuid} 状态: $status")
			super.onDescriptorWrite(gatt, descriptor, status)
		}
		
		override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
			Log.d(TAG, "读取描述 ${descriptor.uuid} 状态: $status")
			super.onDescriptorRead(gatt, descriptor, status)
		}
		
		override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
			Log.d(TAG, "真实写入完成 ${gatt.device.address} 状态: $status")
			super.onReliableWriteCompleted(gatt, status)
		}
		
		override fun onCharacteristicRead(
			gatt: BluetoothGatt,
			characteristic: BluetoothGattCharacteristic,
			status: Int
		) {
			Log.d(TAG, "读取特征 ${characteristic.uuid} 状态: $status")
			super.onCharacteristicRead(gatt, characteristic, status)
		}
		
		override fun onCharacteristicWrite(
			gatt: BluetoothGatt,
			characteristic: BluetoothGattCharacteristic,
			status: Int
		) {
			Log.d(TAG, "写入特征 ${characteristic.uuid} 状态: $status")
			super.onCharacteristicWrite(gatt, characteristic, status)
		}
		
		override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
			Log.d(TAG, "MTU调整 ${gatt.device.address} mtu: $mtu 状态: $status")
			super.onMtuChanged(gatt, mtu, status)
		}
		
		override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
			Log.d(TAG, "读取信号强度 ${gatt.device.address} rssi: $rssi 状态: $status")
			super.onReadRemoteRssi(gatt, rssi, status)
		}
		
	}
	
	//    尝试连接蓝牙
	private fun tryConnectBle(device: BluetoothDevice) {
		mLoadingHandler.sendEmptyMessage(HANLDER_LOADINGDIALOG_SHOW)
		stopScanBleDevice()
		for (gatt in mBaseApp.mConnectedBleGatt) {
			Log.d(TAG, "准备断开连接： ${gatt.device.address}")
			gatt.disconnect()
		}
		device.connectGatt(this@ScanDeviceActivity, false, mBluetoothGattCallback)
//		mBluetoothService?.registerConnectionCallback(device, mConnectionCallback)
//		mBluetoothService?.connect(device)
//		stopScanBleDevice()
	}
	
	//    重置设备列表
	private fun resetDevice() {
		(rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).clear()
		when ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.size) {
			0 -> {
				rvDeviceScanResult.visibility = View.GONE
				tvDeviceScanInfoNothing.visibility = View.VISIBLE
			}
			else -> {
				tvDeviceScanInfoNothing.visibility = View.GONE
				rvDeviceScanResult.visibility = View.VISIBLE
			}
		}
	}
	
	//    开始扫描设备
	private fun startScanBleDevice() {
		Log.d(TAG, "准备扫描 扫描中标记：$mBleScanning 蓝牙启用状态: ${mBaseApp.mBluetoothAdapter.isEnabled}")
		if (mBaseApp.mBluetoothAdapter.isEnabled) {
			setNecessaryPermission(
				listOf(
					Manifest.permission.BLUETOOTH,
					Manifest.permission.BLUETOOTH_ADMIN,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION
				),
				Runnable {
					if (mBleScanning) {
						Log.d(TAG, "停止扫描设备")
						stopScanBleDevice()
					}
					Log.d(TAG, "开始扫描蓝牙设备")
					resetDevice()
					// 开始扫描
					mBaseApp.mBluetoothAdapter.bluetoothLeScanner.startScan(mScanCallback)
					// 设置动画
					ivNavRight.let {
						it.setImageDrawable(resources.getDrawable(R.mipmap.ic_loading))
						it.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.loading_anim))
					}
					mBleScanning = true
					// 设置自动停止
					autoStopScanJob = GlobalScope.launch(IO) {
						delay(DEFAULT_BLUETOOTH_SCAN_TIMEOUT)
						stopScanBleDevice()
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
	
	//    停止扫描
	private fun stopScanBleDevice() {
		mBaseApp.mBluetoothAdapter.bluetoothLeScanner.stopScan(mScanCallback)
		autoStopScanJob?.let {
			if (it.isActive && !it.isCompleted && !it.isCancelled) {
				Log.d(TAG, "取消自动停止扫描任务")
				it.cancel()
			}
		}
		// 关闭动画
		ivNavRight.let {
			it.setImageDrawable(resources.getDrawable(R.mipmap.ic_query))
			it.clearAnimation()
		}
		mBleScanning = false
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
	fun onFoundBleDevice(mBtScanResult: BtScanResult) {
		if (mBtScanResult.device.name.isNullOrBlank())
			return
		val device = mBtScanResult.device
		if ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.filter { dev: BluetoothDevice -> dev.address == device.address }.isEmpty()) {
			(rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).append(device)
			when ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.size) {
				0 -> {
					rvDeviceScanResult.visibility = View.GONE
					tvDeviceScanInfoNothing.visibility = View.VISIBLE
				}
				else -> {
					tvDeviceScanInfoNothing.visibility = View.GONE
					rvDeviceScanResult.visibility = View.VISIBLE
				}
			}
		}
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onDeviceConnectedSuccess(str: String) {
		when (str) {
			"found" -> {
				Log.d(TAG, "准备写入描述")
//				mConnDesc?.value = byteArrayOf(0x01, 0x00)
//				mConnGatt?.writeDescriptor(mConnDesc)
//				SystemClock.sleep(1000)
//				mConnDesc?.value = byteArrayOf(0x02, 0x00)
//				mConnGatt?.writeDescriptor(mConnDesc)
				Log.d(TAG, "准备写入特征")
				mConnChar?.value = byteArrayOf(0x01, 0x00)
				mConnChar?.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
				mConnGatt?.writeCharacteristic(mConnChar)
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
