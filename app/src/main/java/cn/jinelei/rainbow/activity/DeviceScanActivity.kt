package cn.jinelei.rainbow.activity

import android.Manifest
import android.app.Notification
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
import cn.jinelei.rainbow.constant.REQUEST_CODE_ACCESS_COARSE_LOCATION
import cn.jinelei.rainbow.constant.REQUEST_CODE_ACCESS_WIFI_STATE
import cn.jinelei.rainbow.constant.REQUEST_CODE_CHANGE_WIFI_STATE
import cn.jinelei.rainbow.message.WifiScanMessageEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DeviceScanActivity : BaseActivity() {
    private var deviceScanResultRecycler: RecyclerView? = null
    private var deviceScanInfoNothing: TextView? = null
    private var deviceScanTitle: TextView? = null
    private var deviceScanBtn: ImageView? = null
    private var backBtn: ImageView? = null
    private var wifiInfo: WifiInfo? = null
    private var scanFinished = true

    val START_SCAN_WIFI = 1
    val STOP_SCAN_WIFI = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        wifiInfo = wifiManager?.connectionInfo
    }

    private fun initView() {
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_device_scan)
        deviceScanResultRecycler = findViewById<RecyclerView>(R.id.device_scan_info_recycler_view).apply {
            this.layoutManager = LinearLayoutManager(this@DeviceScanActivity)
            this.addItemDecoration(
                DividerItemDecoration(
                    this@DeviceScanActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        deviceScanTitle = findViewById<TextView>(R.id.navigation_header_title).apply {
            this.text = resources.getString(R.string.scan_device)
        }
        deviceScanBtn = findViewById<ImageView>(R.id.navigation_header_right).apply {
            this.setImageResource(R.mipmap.ic_discovery)
            this.setOnClickListener { scanDevice() }
        }
        deviceScanInfoNothing = findViewById<TextView>(R.id.device_scan_info_nothing).apply {
            this.setOnClickListener { scanDevice() }
        }
        backBtn = findViewById<ImageView>(R.id.navigation_header_left).apply {
            this.setImageResource(R.mipmap.ic_back)
            this.setOnClickListener { finish() }
        }
    }

    private fun detectWifi() {
        debug(Log.VERBOSE, "start detect wifi")

        showLoading()
        val scanResults = wifiManager?.scanResults ?: emptyList()
        if (scanResults.size == 0) {
            deviceScanInfoNothing?.visibility = View.VISIBLE
            deviceScanResultRecycler?.visibility = View.INVISIBLE
        } else {
            deviceScanInfoNothing?.visibility = View.INVISIBLE
            deviceScanResultRecycler?.visibility = View.VISIBLE
            deviceScanResultRecycler?.adapter =
                WifiResultAdapter(scanResults.sortedWith(compareBy {
                    Math.abs(
                        it.level
                    )
                }))
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
            true -> deviceScanTitle?.text = getString(R.string.scan_device)
            false -> deviceScanTitle?.text = getString(R.string.scan_device_wait)
        }
    }

    class WifiResultAdapter(val dataset: List<ScanResult>) :
        RecyclerView.Adapter<WifiResultAdapter.WifiInfoViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WifiInfoViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.wifi_info_layout, p0, false)
            return WifiInfoViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataset.size
        }

        override fun onBindViewHolder(viewHolder: WifiInfoViewHolder, p1: Int) {
            val scanResult = dataset[p1]
            viewHolder.ssid.text = scanResult.SSID
            viewHolder.bssid.text = scanResult.BSSID
            viewHolder.level.text = scanResult.level.toString()
            viewHolder.operatorFriendlyName.text = scanResult.operatorFriendlyName
            viewHolder.venueName.text = scanResult.venueName
        }

        class WifiInfoViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
            val ssid: TextView = view.findViewById(R.id.wifi_info_ssid)
            val bssid: TextView = view.findViewById(R.id.wifi_info_bssid)
            val level: TextView = view.findViewById(R.id.wifi_info_level)
            val operatorFriendlyName: TextView = view.findViewById(R.id.wifi_info_operatorFriendlyName)
            val venueName: TextView = view.findViewById(R.id.wifi_info_venueName)
        }
    }

}
