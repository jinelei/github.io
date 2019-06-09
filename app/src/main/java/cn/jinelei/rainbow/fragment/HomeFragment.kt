package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R
import android.view.Gravity
import android.widget.TextView

class HomeFragment : BaseFragment() {
    val TAG = javaClass.simpleName
    val fragments: ArrayList<Fragment> = arrayListOf(
        TestFragment(),
        TestFragment(),
        TestFragment(),
        TestFragment(),
        TestFragment(),
        TestFragment()
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false).apply {
            initView(this)
        }
    }

    companion object {
        val instance = HomeFragment()
        val name = "HomeFragment"
    }

    fun initView(view: View) {
        val homeViewPager = view.findViewById<ViewPager>(R.id.home_viewpager)
        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(p0: Int) {
                Log.v(TAG, "onPageSelected $p0")
            }

            override fun onPageScrollStateChanged(p0: Int) {
                Log.v(TAG, "onPageScrollStateChanged $p0")
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                Log.v(TAG, "onPageScrolled $p0 $p1 $p2")
            }
        })
        homeViewPager.adapter = object : FragmentPagerAdapter(fragmentManager) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(p0: Int): Fragment {
                return fragments[p0]
            }
        }
    }

}