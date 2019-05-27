package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R

class SettingFragment : Fragment() {
    val TAG = javaClass.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    companion object {
        val instance = SingletonHolder.holder
        val name = "SettingFragment"
    }

    private object SingletonHolder {
        val holder = SettingFragment()
    }
}