package cn.jinelei.rainbow.activity

import android.Manifest
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.constant.*
import cn.jinelei.rainbow.fragment.DiscoveryFragment
import cn.jinelei.rainbow.fragment.HomeFragment
import cn.jinelei.rainbow.fragment.UserFragment
import cn.jinelei.rainbow.service.MainService
import cn.jinelei.rainbow.util.switchFragment
import com.amap.api.maps.MapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    val TAG = javaClass.simpleName
    private var mMapView: MapView? = null
    private var currentFragment: Fragment = HomeFragment.instance
    private var mainBinder: MainService.MainBinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView(savedInstanceState)
        initData()
    }

    private fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        supportFragmentManager.beginTransaction()
            .add(R.id.main_frame, currentFragment)
            .commit()

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    currentFragment =
                        switchFragment(supportFragmentManager, R.id.main_frame, currentFragment, HomeFragment.instance)
                }
                R.id.navigation_user -> {
                    currentFragment =
                        switchFragment(supportFragmentManager, R.id.main_frame, currentFragment, UserFragment.instance)
                }
                R.id.navigation_discovery -> {
                    currentFragment =
                        switchFragment(
                            supportFragmentManager,
                            R.id.main_frame,
                            currentFragment,
                            DiscoveryFragment.instance
                        )
                }
                else -> Log.d(TAG, "other")
            }
            true
        }
//        mMapView = findViewById<MapView>(R.id.map)
//        mMapView?.onCreate(savedInstanceState)
//        val map = mMapView?.map
//        Log.d(TAG, map.toString())
    }

    private fun initData() {
        GlobalScope.launch(Dispatchers.IO) {
            customRequestPermission(
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                null,
                null
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
//        mMapView?.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
    }

}
