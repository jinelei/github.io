package cn.jinelei.rainbow.ui.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.net.wifi.ScanResult
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
import cn.jinelei.rainbow.constant.WifiScanMessageEvent
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_device_scan.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.android.synthetic.main.wifi_info_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class DeviceScanActivity : BaseActivity() {
    private lateinit var rvDeviceScanResult: RecyclerView
    private lateinit var tvDeviceScanInfoNothing: TextView
    private lateinit var tvDeviceScanTitle: TextView
    private var mWifiInfo: WifiInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        mWifiInfo = mWifiManager.connectionInfo
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
        tvDeviceScanTitle = tv_nav_title.apply {
            text = resources.getString(R.string.scan_device)
        }
        tvDeviceScanInfoNothing = tv_device_scan_info_nothing.apply {
            setOnClickListener { scanDevice() }
        }
        iv_nav_right.apply {
            setImageResource(R.mipmap.ic_discovery)
            setOnClickListener { scanDevice() }
        }
        iv_nav_left.apply {
            setImageResource(R.mipmap.ic_back)
            setOnClickListener { finish() }
        }
    }

    private fun detectWifi() {
        debug(Log.VERBOSE, "start detect wifi")
        showLoading()
        val scanResults = mWifiManager.scanResults
        rvDeviceScanResult.apply {
            visibility = when (scanResults.size == 0) {
                true -> View.INVISIBLE
                false -> View.VISIBLE
            }
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.wifi_info_layout,
                dataList = scanResults.sortedWith(compareBy { Math.abs(it.level) }).toMutableList()
            ) {
                onBindViewHolder { holder, position ->
                    holder.tv_ssid.text = getItem(position).SSID
                    holder.tv_bssid.text = getItem(position).BSSID
                    holder.tv_level.text = getItem(position).level.toString()
                    holder.iv_frequency.let {
                        if (getItem(position).frequency in 4901..5899)
                            it.setImageResource(R.mipmap.ic_5ghz)
                        else
                            it.setImageResource(R.mipmap.ic_2_4ghz)
                    }
                }
            }
        }
        tvDeviceScanInfoNothing.let {
            it.visibility = when (scanResults.size) {
                0 -> View.VISIBLE
                else -> View.INVISIBLE
            }
        }
        hideLoading()
    }

    private fun scanDevice() {
        if (mWifiManager.isWifiEnabled) {
            setNecessaryPermission(
                listOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Runnable { detectWifi() },
                Runnable { toast(getString(R.string.failed_to_turn_on_wifi_please_turn_on_wifi_permissions_manually)) },
                Runnable { showExplainDialog() }
            )
        } else {
            setNecessaryPermission(
                listOf(
                    Manifest.permission.CHANGE_WIFI_STATE
                ),
                Runnable { showOpenWifiDialog() },
                Runnable { toast(getString(R.string.failed_to_turn_on_wifi__please_turn_on_wifi_manually)) },
                Runnable { showExplainDialog() }
            )
        }
    }

    private fun showOpenWifiDialog() {
        alertDialogBuilder.apply {
            setTitle(getString(R.string.wifi_already_closed))
            setView(TextView(this@DeviceScanActivity).apply {
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
                mWifiManager.isWifiEnabled = true
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
                setView(TextView(this@DeviceScanActivity).apply {
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
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateView(event: WifiScanMessageEvent) {
        when (event.finish) {
            true -> tvDeviceScanTitle.text = getString(R.string.scan_device)
            false -> tvDeviceScanTitle.text = getString(R.string.scan_device_wait)
        }
    }

}
