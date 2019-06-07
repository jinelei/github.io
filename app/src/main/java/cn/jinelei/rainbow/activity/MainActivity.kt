package cn.jinelei.rainbow.activity

import android.Manifest
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.fragment.DiscoveryFragment
import cn.jinelei.rainbow.fragment.HomeFragment
import cn.jinelei.rainbow.fragment.UserFragment
import cn.jinelei.rainbow.service.MainService
import com.amap.api.maps.MapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    val TAG = javaClass.simpleName
    private var mMapView: MapView? = null
    private var mainBinder: MainService.MainBinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        switchFragmentTo(R.id.main_frame, HomeFragment.instance)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home ->
                    switchFragmentTo(R.id.main_frame, HomeFragment.instance)
                R.id.navigation_user ->
                    switchFragmentTo(R.id.main_frame, UserFragment.instance)
                R.id.navigation_discovery ->
                    switchFragmentTo(R.id.main_frame, DiscoveryFragment.instance)
                else -> debug(Log.ERROR, "invalid switch fragment: ${menuItem.itemId}")
            }
            true
        }
    }

    private fun initData() {
        GlobalScope.launch(Dispatchers.IO) {
            customRequestPermission(
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), Runnable {
                    GlobalScope.launch(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "grant write log", Toast.LENGTH_SHORT).show()
                    }
                }, Runnable {
                    alertDialogBuilder?.setTitle(getString(R.string.request_permission))
                        ?.setView(TextView(this@MainActivity).also {
                            it.text = getString(R.string.request_permission)
                        })
                        ?.create()?.show()
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
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

}
