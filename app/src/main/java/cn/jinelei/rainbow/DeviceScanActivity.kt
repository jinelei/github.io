package cn.jinelei.rainbow

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class DeviceScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {
        setContentView(R.layout.activity_device_scan)
    }
}
