package cn.jinelei.rainbow.ui.activity

import android.Manifest
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.constant.WifiScanMessageEvent
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_device_scan.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.android.synthetic.main.wifi_info_layout.*
import kotlinx.android.synthetic.main.wifi_info_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DeviceScanActivity : BaseActivity() {
    private var rvDeviceScanResult: RecyclerView? = null
    private var tvDeviceScanInfoNothing: TextView? = null
    private var tvDeviceScanTitle: TextView? = null
    private var ivDeviceScanBtn: ImageView? = null
    private var ivBackBtn: ImageView? = null
    private var mWifiInfo: WifiInfo? = null
    private var bScanFinished = true

    val START_SCAN_WIFI = 1
    val STOP_SCAN_WIFI = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        mWifiInfo = wifiManager?.connectionInfo
    }

    private fun initView() {
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_device_scan)
        rvDeviceScanResult = rv_device_scan_info.apply {
            layoutManager = LinearLayoutManager(this@DeviceScanActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@DeviceScanActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        tvDeviceScanTitle = tv_title.apply {
            text = resources.getString(R.string.scan_device)
        }
        ivDeviceScanBtn = iv_right.apply {
            setImageResource(R.mipmap.ic_discovery)
            setOnClickListener { scanDevice() }
        }
        tvDeviceScanInfoNothing = tv_device_scan_info_nothing.apply {
            setOnClickListener { scanDevice() }
        }
        ivBackBtn = iv_left.apply {
            setImageResource(R.mipmap.ic_back)
            setOnClickListener { finish() }
        }
    }

    private fun detectWifi() {
        debug(Log.VERBOSE, "start detect wifi")
        showLoading()
        val scanResults = wifiManager?.scanResults ?: emptyList()
        rvDeviceScanResult?.apply {
            if (scanResults.size == 0) {
                this.visibility = View.INVISIBLE
            } else {
                this.visibility = View.VISIBLE
            }
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.wifi_info_layout,
                dataList = scanResults.sortedWith(compareBy {
                    Math.abs(
                        it.level
                    )
                }).toMutableList()
            ) {
                onBindViewHolder { holder, position ->
                    holder.tv_ssid.text = getItem(position).SSID
                    holder.tv_bssid.text = getItem(position).BSSID
                    holder.tv_level.text = getItem(position).level.toString()
                    holder.tv_operatorFriendlyName.text = getItem(position).operatorFriendlyName
                    holder.tv_venueName.text = getItem(position).venueName
                }
            }
        }
        tvDeviceScanInfoNothing.apply {
            if (scanResults.size == 0) {
                this?.visibility = View.VISIBLE
            } else {
                this?.visibility = View.INVISIBLE
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            // 模拟延时
            delay(5000)
            hideLoading()
        }
    }

    private fun scanDevice() {
        if (wifiManager?.isWifiEnabled == true) {
            customRequestPermission(
                listOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Runnable { detectWifi() },
                null
            )
        } else {
            AlertDialog.Builder(this).setTitle(getString(R.string.wifi_already_closed))
                .setNegativeButton(getString(R.string.please_open_wifi_switch)) { _, _ ->
                    Toast.makeText(
                        this@DeviceScanActivity,
                        getString(R.string.please_open_wifi_switch),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ -> startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateView(event: WifiScanMessageEvent) {
        when (event.finish) {
            true -> tvDeviceScanTitle?.text = getString(R.string.scan_device)
            false -> tvDeviceScanTitle?.text = getString(R.string.scan_device_wait)
        }
    }

}
