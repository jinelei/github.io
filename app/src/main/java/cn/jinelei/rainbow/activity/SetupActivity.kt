package cn.jinelei.rainbow.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.fragment.SetupFragment

class SetupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {
        setContentView(R.layout.activity_setup)
        fragmentManager?.beginTransaction()?.add(R.id.preference_container, SetupFragment.instance)?.commit()
        findViewById<ImageView>(R.id.navigation_header_left).apply {
            this.setImageResource(R.mipmap.ic_back)
            this.setOnClickListener { _: View? -> finish() }
        }
        findViewById<TextView>(R.id.navigation_header_title).text = resources.getString(R.string.preference)
    }
}
