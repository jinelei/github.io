package cn.jinelei.rainbow.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.activity.SettingActivity

class UserFragment : Fragment() {
    val TAG = javaClass.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)
        val menuBtn = view.findViewById<ImageView>(R.id.menu_setting)
        menuBtn.setOnClickListener { v: View? -> startActivity(Intent(activity, SettingActivity::class.java)) }
        return view
    }

    companion object {
        val instance = SingletonHolder.holder
        val name = "UserFragment"
    }

    private object SingletonHolder {
        val holder = UserFragment()
    }
}