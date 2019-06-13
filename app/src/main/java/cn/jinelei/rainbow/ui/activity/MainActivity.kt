package cn.jinelei.rainbow.ui.activity

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.ui.fragment.DiscoveryFragment
import cn.jinelei.rainbow.ui.fragment.HomeFragment
import cn.jinelei.rainbow.ui.fragment.UserFragment
import com.amap.api.maps.MapView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        setContentView(R.layout.activity_main)
        val mBottomNavigationView = bnv_main
        switchFragmentTo(R.id.frame_main, HomeFragment.instance)
        mBottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home ->
                    switchFragmentTo(R.id.frame_main, HomeFragment.instance)
                R.id.navigation_user ->
                    switchFragmentTo(R.id.frame_main, UserFragment.instance)
                R.id.navigation_discovery ->
                    switchFragmentTo(R.id.frame_main, DiscoveryFragment.instance)
                else -> debug(Log.ERROR, "invalid switch fragment: ${menuItem.itemId}")
            }
            true
        }
    }

    private fun initData() {
        GlobalScope.launch(Dispatchers.IO) {
            setNecessaryPermission(
                listOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                null,
                Runnable { mBaseApp.toast(getString(R.string.write_log_need_write_permission)) },
                Runnable {
                    alertDialogBuilder.let {
                        it.setTitle(getString(R.string.request_permission))
                        it.setView(TextView(this@MainActivity).apply { text = getString(R.string.request_permission) })
                        it.create()
                        it.show()
                    }
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    companion object {
        val TAG = DiscoveryFragment::class.java.simpleName ?: "MainActivity"
        val instance by lazy { Holder.INSTANCE }

    }

    private object Holder {
        val INSTANCE = MainActivity()

    }
}
