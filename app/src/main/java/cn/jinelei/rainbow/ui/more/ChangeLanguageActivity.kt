package cn.jinelei.rainbow.ui.more

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.constant.PRE_KEY_LANGUAGE
import cn.jinelei.rainbow.constant.PRE_NAME_MINE
import cn.jinelei.rainbow.ui.base.MainActivity
import cn.jinelei.rainbow.ui.base.adapter.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_change_language.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.android.synthetic.main.include_top_navigation.tv_nav_title
import kotlinx.android.synthetic.main.language_item_layout.*
import java.util.*

class ChangeLanguageActivity : BaseActivity() {
    var iCurrentLocaleIdx: Int = 0

    private val allSupportLocales = mutableListOf<LanguageItem>()

    private fun changeLocaleType(t: Locale) {
        mBaseApp.savePreference(
            name = PRE_NAME_MINE,
            key = PRE_KEY_LANGUAGE,
            defaultValue = t.language
        )
        iCurrentLocaleIdx =
            allSupportLocales.indexOfFirst { languageItem -> languageItem.locale.equals(t.language) }
        resources.apply {
            updateConfiguration(configuration.apply { setLocale(t) }, this.displayMetrics)
        }
        resources.displayMetrics
        startActivity(
            Intent(
                this@ChangeLanguageActivity,
                MainActivity::class.java
            ).apply {
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        this@ChangeLanguageActivity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initView() {
        setContentView(R.layout.activity_change_language)
        val sLanguage = mBaseApp.readPreference(
            PRE_NAME_MINE,
            PRE_KEY_LANGUAGE,
            Locale.getDefault().language
        )
        iCurrentLocaleIdx =
            allSupportLocales.indexOfFirst { languageItem -> languageItem.locale.equals(sLanguage) }
        this.rv_language.apply {
            layoutManager = LinearLayoutManager(this@ChangeLanguageActivity)
            adapter = BaseRecyclerAdapter(
	            itemLayoutId = R.layout.language_item_layout,
	            dataSet = allSupportLocales
            ) {
	            onBindViewHolder { holder, position ->
		            holder.iv_icon.setImageResource(getItem(position).resId)
		            holder.tv_nav_title.text = getItem(position).locale
		            holder.layout_language_item.setOnClickListener { getItem(position).callback.run() }
		            holder.iv_rta.visibility = when (iCurrentLocaleIdx == position) {
			            true -> VISIBLE
			            false -> GONE
		            }
	            }
            }
        }
        tv_nav_title.text = resources.getString(R.string.change_language)
        iv_nav_left.apply {
            setImageResource(R.mipmap.ic_back)
            setOnClickListener { finish() }
        }
    }

    private fun initData() {
        allSupportLocales.let {
            it.add(
	            LanguageItem(
		            R.mipmap.ic_option,
		            getString(R.string.follow_system),
		            Runnable { changeLocaleType(Locale.getDefault()) })
            )
            it.add(
	            LanguageItem(
		            R.mipmap.ic_option,
		            Locale.ENGLISH.language,
		            Runnable { changeLocaleType(Locale.ENGLISH) })
            )
            it.add(
	            LanguageItem(
		            R.mipmap.ic_option,
		            Locale.SIMPLIFIED_CHINESE.language,
		            Runnable { changeLocaleType(Locale.SIMPLIFIED_CHINESE) })
            )
        }
    }

    class LanguageItem(val resId: Int, val locale: String, val callback: Runnable) {}

}
