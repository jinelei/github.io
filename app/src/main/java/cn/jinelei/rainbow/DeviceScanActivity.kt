package cn.jinelei.rainbow

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
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
import cn.jinelei.rainbow.activity.BaseActivity
import cn.jinelei.rainbow.components.LoadingDialog
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
import kotlin.math.log

class DeviceScanActivity : BaseActivity() {
    private var deviceScanResultRecycler: RecyclerView? = null
    private var deviceScanInfoNothing: TextView? = null
    private var deviceScanTitle: TextView? = null
    private var deviceScanBtn: ImageView? = null
    private var wifiManager: WifiManager? = null
    private var wifiInfo: WifiInfo? = null
    private var loadingDialog: LoadingDialog? = null
    private var scanFinished = true

    val START_SCAN_WIFI = 1
    val STOP_SCAN_WIFI = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initData() {
        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiInfo = wifiManager?.connectionInfo
        loadingDialog = LoadingDialog(this@DeviceScanActivity)
        Log.v(getTag(), wifiInfo.toString())
    }

    private fun initView() {
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_device_scan)
        deviceScanResultRecycler = findViewById(R.id.device_scan_info_recycler_view)
        deviceScanInfoNothing = findViewById(R.id.device_scan_info_nothing)
        deviceScanResultRecycler?.layoutManager = LinearLayoutManager(this@DeviceScanActivity)
        deviceScanResultRecycler?.addItemDecoration(
            DividerItemDecoration(
                this@DeviceScanActivity,
                DividerItemDecoration.VERTICAL
            )
        );
        deviceScanTitle = findViewById(R.id.device_scan_title)
        deviceScanBtn = findViewById(R.id.device_scan_title_btn)
        deviceScanBtn?.setOnClickListener { scanDevice() }
        deviceScanInfoNothing?.setOnClickListener { scanDevice() }
    }

    private fun detectWifi() {
        Log.v(getTag(), "detect wifi")
        showLoading()
        val scanResults = wifiManager?.scanResults ?: emptyList()
        if (scanResults == null || scanResults.size == 0) {
            deviceScanInfoNothing?.visibility = View.VISIBLE
            deviceScanResultRecycler?.visibility = View.INVISIBLE
        } else {
            deviceScanInfoNothing?.visibility = View.INVISIBLE
            deviceScanResultRecycler?.visibility = View.VISIBLE
            deviceScanResultRecycler?.adapter =
                WifiResultAdapter(scanResults.sortedWith(compareBy { Math.abs(it.level) }))
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
                    REQUEST_CODE_CHANGE_WIFI_STATE,
                    REQUEST_CODE_ACCESS_WIFI_STATE,
                    REQUEST_CODE_ACCESS_COARSE_LOCATION
                ),
                listOf(
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
            else -> deviceScanTitle?.text = getString(R.string.scan_device)
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
