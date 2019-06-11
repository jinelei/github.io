package cn.jinelei.rainbow.ui.activity

import android.os.Bundle
import android.view.View
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.ui.fragment.SetupFragment
import kotlinx.android.synthetic.main.include_top_navigation.*

class SetupActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    fun initView() {
        setContentView(R.layout.activity_setup)
        fragmentManager?.beginTransaction()?.add(R.id.frame_setup, SetupFragment.instance)?.commit()
        iv_left.apply {
            this.setImageResource(R.mipmap.ic_back)
            this.setOnClickListener { _: View? -> finish() }
        }
        tv_title.text = resources.getString(R.string.preference)
    }
}
