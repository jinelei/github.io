package cn.jinelei.rainbow.ui.more

import android.os.Bundle
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.ui.home.SetupFragment
import kotlinx.android.synthetic.main.include_top_navigation.*

class SetupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {
        setContentView(R.layout.activity_setup)
        fragmentManager.beginTransaction().add(R.id.frame_setup, SetupFragment.instance).commit()
        iv_nav_left.apply {
            this.setImageResource(R.mipmap.ic_back)
            this.setOnClickListener { finish() }
        }
        tv_nav_title.text = resources.getString(R.string.preference)
    }
}
