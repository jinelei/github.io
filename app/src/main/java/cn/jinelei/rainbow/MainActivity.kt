package cn.jinelei.rainbow

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.amap.api.maps.MapView

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
    var mMapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.navigation_home -> Log.d(TAG, "navigation_home")
                    R.id.navigation_user -> Log.d(TAG, "navigation_user")
                    R.id.navigation_discovery -> Log.d(TAG, "navigation_discovery")
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
}
