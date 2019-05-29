package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
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
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        initView(view)
        return view
    }

    companion object {
        val instance = SingletonHolder.holder
        val name = "HomeFragment"
    }

    fun initView(view: View) {
        val homeViewPager = view.findViewById<ViewPager>(R.id.home_viewpager)
        homeViewPager.adapter = object : FragmentPagerAdapter(fragmentManager) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(p0: Int): Fragment {
                return fragments[p0]
            }
        }
    }

    private object SingletonHolder {
        val holder = HomeFragment()
    }

}