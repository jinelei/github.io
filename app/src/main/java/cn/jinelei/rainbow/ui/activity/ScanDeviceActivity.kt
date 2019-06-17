package cn.jinelei.rainbow.ui.activity

import android.Manifest
import android.bluetooth.*
import android.bluetooth.BluetoothDevice.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import cn.jinelei.rainbow.constant.DEFAULT_BLUETOOTH_SCAN_TIMEOUT
import cn.jinelei.rainbow.constant.REQUEST_CODE_OPEN_BT
import cn.jinelei.rainbow.constant.WifiScanMessageEvent
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


class ScanDeviceActivity : BaseActivity() {
    private lateinit var rvDeviceScanResult: RecyclerView
    private lateinit var tvDeviceScanTitle: TextView
    private lateinit var tvDeviceScanInfoNothing: TextView
    private lateinit var ivNavRight: ImageView
    private var scanning = false
    private var autoStopScanJob: Job? = null
    private var mBluetoothGatt: BluetoothGatt? = null

    private var mScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            debug(Log.DEBUG, "onScanFailed errorCode: $errorCode")
            resetDevice()
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                debug(Log.VERBOSE, "onScanResult $it.toString()}")
                addDevice(it.device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            debug(Log.DEBUG, "onBatchScanResults ${results?.size}")
        }
    }

    private val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                debug(Log.DEBUG, "Connected to GATT server.")
                debug(Log.DEBUG, "Attempting to start service discovery: ${mBluetoothGatt?.discoverServices()}");
                if (mBluetoothGatt != null)
                    displayUuids(mBluetoothGatt!!)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mBluetoothGatt?.let {
                    debug(Log.DEBUG, "Disconnected from GATT server. name: ${mBluetoothGatt?.device?.name}")
                    it.close()
                    it.disconnect()
                }
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                debug(Log.DEBUG, "connecting from GATT server. name: ${mBluetoothGatt?.device?.name}")
            } else {
                debug(Log.DEBUG, "Disconnecting from GATT server.");
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        prepareScanDevice()
    }

    private fun initView() {
        EventBus.getDefault().register(this)
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
                        debug(
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
            REQUEST_CODE_OPEN_BT -> debug(Log.INFO, "resultCode: $resultCode, data: $data")
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
        debug(Log.DEBUG, "displayUuids: ${mBluetoothGatt.device.name} services: $services")
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
                            tv_name.text = mBluetoothGatt.device.address
                        }
                    }
                    it.setView(rvUuidService)
                    it.create()
                    it.show()
                }
            }
        }
    }

    //    连接蓝牙
    private fun connectGatt(device: BluetoothDevice) {
        mBluetoothGatt?.let {
            debug(Log.DEBUG, "disconnecting from GATT server. name: ${mBluetoothGatt?.device?.name}")
            it.close()
            it.disconnect()
        }
        debug(Log.DEBUG, "connectGatt: device ${device.name}")
        val remoteDevice = mBaseApp.mBluetoothAdapter.getRemoteDevice(device.address.toUpperCase().trim())
        mBluetoothGatt = remoteDevice.connectGatt(this, false, mGattCallback)
    }

    //    添加设备到列表
    private fun addDevice(device: BluetoothDevice) {
        if (device.name.isNullOrBlank())
            return
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
        debug(Log.DEBUG, "scanning: $scanning enabled: ${mBaseApp.mBluetoothAdapter.isEnabled}")
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

    private fun stopScanDevice() {
        mBaseApp.mBluetoothAdapter.bluetoothLeScanner.stopScan(mScanCallback)
        autoStopScanJob?.let {
            if (it.isActive && !it.isCompleted && !it.isCancelled) {
                debug(Log.DEBUG, "cancel auto stop scan job")
                it.cancel()
            }
        }
        ivNavRight.let {
            it.setImageDrawable(resources.getDrawable(R.mipmap.ic_query))
            it.clearAnimation()
        }
        scanning = false
    }

    private fun startScanDevice() {
        debug(Log.VERBOSE, "start detect bt")
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateView(event: WifiScanMessageEvent) {
        when (event.finish) {
            true -> tvDeviceScanTitle.text = getString(R.string.scan_device)
            false -> tvDeviceScanTitle.text = getString(R.string.scan_device_wait)
        }
    }

}
