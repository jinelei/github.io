package cn.jinelei.rainbow.ui.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import android.view.animation.Animation
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Runnable


class ScanDeviceActivity : BaseActivity() {
    private lateinit var rvDeviceScanResult: RecyclerView
    private lateinit var tvDeviceScanTitle: TextView
    private lateinit var tvDeviceScanInfoNothing: TextView
    private lateinit var ivNavRight: ImageView
    private lateinit var animation: Animation
    private var scanning = false
    private var autoStopScanJob: Job? = null

    private var mScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            debug(Log.DEBUG, "onScanFailed errorCode: $errorCode")
            resetDevice()
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                debug(Log.DEBUG, "onScanResult $it.toString()}")
                addDevice(it.device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            debug(Log.DEBUG, "onBatchScanResults ${results?.size}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        animation = AnimationUtils.loadAnimation(mContext, R.anim.loading_anim);
    }

    private fun initView() {
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_scan_device)
        rvDeviceScanResult = rv_device_scan_info.apply {
            layoutManager = LinearLayoutManager(this@ScanDeviceActivity)
            addItemDecoration(
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
                    holder.tv_name.text = getItem(position).name
                    holder.tv_mac.text = getItem(position).address
                    holder.tv_type.text = when (getItem(position).type) {
                        DEVICE_TYPE_CLASSIC -> "classic"
                        DEVICE_TYPE_LE -> "le"
                        DEVICE_TYPE_DUAL -> "dual"
                        else -> ""
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OPEN_BT -> debug(Log.INFO, "resultCode: $resultCode, data: $data")
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    //    添加设备到列表
    private fun addDevice(device: BluetoothDevice) {
        if (device.name.isNullOrBlank())
            return
        if ((rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).dataSet.filter { dev: BluetoothDevice -> dev.address == device.address }.isEmpty()) {
            (rvDeviceScanResult.adapter as BaseRecyclerAdapter<BluetoothDevice>).add(device)
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
        debug(Log.DEBUG, "scanning: $scanning enabled: ${mBluetoothManager.adapter.isEnabled}")
        if (scanning) {
            stopScanDevice()
        } else {
            if (mBluetoothManager.adapter.isEnabled) {
                setNecessaryPermission(
                    listOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
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
        mBluetoothManager.adapter.bluetoothLeScanner.stopScan(mScanCallback)
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
        mBluetoothManager.adapter.bluetoothLeScanner.startScan(mScanCallback)
        ivNavRight.let {
            it.setImageDrawable(resources.getDrawable(R.mipmap.ic_loading))
            it.startAnimation(animation)
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
