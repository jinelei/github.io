package cn.jinelei.rainbow

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.jinelei.rainbow.fragment.DiscoveryFragment
import cn.jinelei.rainbow.fragment.HomeFragment
import cn.jinelei.rainbow.fragment.UserFragment
import com.amap.api.maps.MapView

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
    var mMapView: MapView? = null
    var currentFragment: Fragment = HomeFragment.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        supportFragmentManager.beginTransaction()
            .add(R.id.main_frame, currentFragment)
            .commit()

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.main_frame)
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    switchFragment(HomeFragment.instance)
                    Log.d(TAG, "navigation_home")
                }
                R.id.navigation_user -> {
                    switchFragment(UserFragment.instance)
                    Log.d(TAG, "navigation_user")
                }
                R.id.navigation_discovery -> {
                    switchFragment(DiscoveryFragment.instance)
                    Log.d(TAG, "navigation_discovery")
                }
                else -> Log.d(TAG, "other")
            }
            true
        }
//        mMapView = findViewById<MapView>(R.id.map)
        mMapView?.onCreate(savedInstanceState)
        val map = mMapView?.map
        Log.d(TAG, map.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState)
    }

    fun switchFragment(targetFragment: Fragment) {
        if (currentFragment != targetFragment) {
            val transaction = supportFragmentManager.beginTransaction()
            if (targetFragment.isAdded)
                transaction.hide(currentFragment).show(targetFragment).commit()
            else
                transaction.hide(currentFragment).add(R.id.main_frame, targetFragment).commit()
            currentFragment = targetFragment
        }
    }

}
