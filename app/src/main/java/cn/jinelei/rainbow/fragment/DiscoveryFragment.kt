package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R

class DiscoveryFragment : BaseFragment() {
    val TAG = javaClass.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discovery_fragment, container, false)
    }

    companion object {
        val instance = SingletonHolder.holder
        val name = "DiscoveryFragment"
    }

    private object SingletonHolder {
        val holder = DiscoveryFragment()
    }
}