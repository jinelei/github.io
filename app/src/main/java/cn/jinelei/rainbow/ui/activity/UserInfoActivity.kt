package cn.jinelei.rainbow.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.jinelei.rainbow.R
import kotlinx.android.synthetic.main.include_top_navigation.*

class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_user_info)
        iv_nav_left.let{
            it.setImageResource(R.mipmap.ic_back)
            it.setOnClickListener { finish() }
        }
        tv_nav_title.text = getString(R.string.user_info)
    }
}
