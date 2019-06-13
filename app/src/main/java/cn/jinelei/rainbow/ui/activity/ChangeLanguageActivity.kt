package cn.jinelei.rainbow.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.app.BaseApp
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.constant.PRE_KEY_LANGUAGE
import cn.jinelei.rainbow.constant.PRE_NAME_MINE
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_change_language.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.android.synthetic.main.include_top_navigation.tv_nav_title
import kotlinx.android.synthetic.main.language_item_layout.*
import java.util.*
import java.util.function.Consumer

class ChangeLanguageActivity : BaseActivity() {
    var iCurrentLocaleIdx: Int = 0

    private val allSupportLocales = arrayListOf(
        LanguageItem(R.mipmap.ic_option, "跟随系统", Runnable { changeLocaleType(Locale.getDefault()) }),
        LanguageItem(R.mipmap.ic_option, Locale.ENGLISH.language, Runnable { changeLocaleType(Locale.ENGLISH) }),
        LanguageItem(
            R.mipmap.ic_option,
            Locale.SIMPLIFIED_CHINESE.language,
            Runnable { changeLocaleType(Locale.SIMPLIFIED_CHINESE) })
    )

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
                dataList = allSupportLocales
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

    class LanguageItem(val resId: Int, val locale: String, val callback: Runnable) {}

}
