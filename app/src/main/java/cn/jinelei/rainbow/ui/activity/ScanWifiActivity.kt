package cn.jinelei.rainbow.ui.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiInfo
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_scan_wifi.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.android.synthetic.main.wifi_info_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanWifiActivity : BaseActivity() {
    private lateinit var rvWifiScanResult: RecyclerView
    private lateinit var tvWifiScanInfoNothing: TextView
    private lateinit var tvWifiScanTitle: TextView
    private var mWifiInfo: WifiInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        mWifiInfo = mBaseApp.mWifiManager.connectionInfo
    }

    private fun initView() {
        setContentView(R.layout.activity_scan_wifi)
        rvWifiScanResult = rv_wifi_scan_info.apply {
            layoutManager = LinearLayoutManager(this@ScanWifiActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@ScanWifiActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        tvWifiScanTitle = tv_nav_title.apply {
            text = resources.getString(R.string.scan_wifi)
        }
        tvWifiScanInfoNothing = tv_wifi_scan_info_nothing.apply {
            setOnClickListener { scanWifi() }
        }
        iv_nav_right.apply {
            setImageResource(R.mipmap.ic_discovery)
            setOnClickListener { scanWifi() }
        }
        iv_nav_left.apply {
            setImageResource(R.mipmap.ic_back)
            setOnClickListener { finish() }
        }
    }

    private fun detectWifi() {
        debug(Log.VERBOSE, "start detect wifi")
        showLoading()
        val scanResults = mBaseApp.mWifiManager.scanResults
        GlobalScope.launch(IO) {
            delay(2000)
            hideLoading()
            GlobalScope.launch(Main) {
                rvWifiScanResult.apply {
                    visibility = when (scanResults.size == 0) {
                        true -> View.INVISIBLE
                        false -> View.VISIBLE
                    }
                    adapter = BaseRecyclerAdapter(
                        itemLayoutId = R.layout.wifi_info_layout,
                        dataSet = scanResults.sortedWith(compareBy { Math.abs(it.level) }).toMutableList()
                    ) {
                        onBindViewHolder { holder, position ->
                            holder.tv_name.text = getItem(position).SSID
                            holder.tv_mac.text = getItem(position).BSSID
                            holder.tv_type.text = getItem(position).level.toString()
                            holder.iv_frequency.let {
                                if (getItem(position).frequency in 4901..5899)
                                    it.setImageResource(R.mipmap.ic_5ghz)
                                else
                                    it.setImageResource(R.mipmap.ic_2_4ghz)
                            }
                        }
                    }
                }
                tvWifiScanInfoNothing.let {
                    it.visibility = when (scanResults.size) {
                        0 -> View.VISIBLE
                        else -> View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun scanWifi() {
        if (mBaseApp.mWifiManager.isWifiEnabled) {
            setNecessaryPermission(
                listOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Runnable { detectWifi() },
                Runnable { mBaseApp.toast(getString(R.string.failed_to_turn_on_wifi_please_turn_on_wifi_permissions_manually)) },
                Runnable { showExplainDialog() }
            )
        } else {
            setNecessaryPermission(
                listOf(
                    Manifest.permission.CHANGE_WIFI_STATE
                ),
                Runnable { showOpenWifiDialog() },
                Runnable { mBaseApp.toast(getString(R.string.failed_to_turn_on_wifi__please_turn_on_wifi_manually)) },
                Runnable { showExplainDialog() }
            )
        }
    }

    private fun showOpenWifiDialog() {
        alertDialogBuilder.apply {
            setTitle(getString(R.string.wifi_already_closed))
            setView(TextView(this@ScanWifiActivity).apply {
                text = getString(R.string.scan_wifi_need_open_wifi)
                setPadding(
                    resources.getDimensionPixelOffset(R.dimen.default_padding),
                    resources.getDimensionPixelOffset(R.dimen.default_padding),
                    resources.getDimensionPixelOffset(R.dimen.default_padding),
                    resources.getDimensionPixelOffset(R.dimen.default_padding)
                )
            })
            setPositiveButton(
                getString(R.string.open)
            ) { _, _ ->
                mBaseApp.mWifiManager.isWifiEnabled = true
            }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> finish() }
            create()
            show()
        }
    }

    private fun showExplainDialog() {
        GlobalScope.launch(Dispatchers.Main) {
            alertDialogBuilder.apply {
                setTitle(getString(R.string.please_grant_application_permission))
                setView(TextView(this@ScanWifiActivity).apply {
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

    override fun onDestroy() {
        super.onDestroy()
    }

}
