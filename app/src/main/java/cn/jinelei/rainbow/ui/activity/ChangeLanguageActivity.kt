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
import cn.jinelei.rainbow.constant.PRE_KEY_LANGUAGE
import cn.jinelei.rainbow.constant.PRE_NAME_USER
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.activity_change_language.*
import kotlinx.android.synthetic.main.include_top_navigation.*
import kotlinx.android.synthetic.main.include_top_navigation.tv_title
import kotlinx.android.synthetic.main.language_item_layout.*
import java.util.*
import java.util.function.Consumer

class ChangeLanguageActivity : AppCompatActivity() {
    var iCurrentLocaleIdx: Int = 0

    private val allSupportLocales = arrayListOf(
        LanguageItem(R.mipmap.ic_option, "跟随系统", Consumer { changeLocaleType(Locale.getDefault()) }),
        LanguageItem(R.mipmap.ic_option, Locale.ENGLISH.language, Consumer { changeLocaleType(Locale.ENGLISH) }),
        LanguageItem(
            R.mipmap.ic_option,
            Locale.SIMPLIFIED_CHINESE.language,
            Consumer { changeLocaleType(Locale.SIMPLIFIED_CHINESE) })
    )

    private fun changeLocaleType(t: Locale) {
        (applicationContext as BaseApp).savePreference(
            name = PRE_NAME_USER,
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initView() {
        setContentView(R.layout.activity_change_language)
        val sLanguage = (applicationContext as BaseApp).readPreference(
            PRE_NAME_USER,
            PRE_KEY_LANGUAGE,
            Locale.getDefault().language
        )
        iCurrentLocaleIdx =
            allSupportLocales.indexOfFirst { languageItem -> languageItem.locale.equals(sLanguage) }
        val rvLanguage = this.rv_language.apply {
            layoutManager = LinearLayoutManager(this@ChangeLanguageActivity)
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.language_item_layout,
                dataList = allSupportLocales
            ) {
                onBindViewHolder { holder, position ->
                    holder.iv_icon.setImageResource(getItem(position).resId)
                    holder.tv_title.text = getItem(position).locale
                    holder.layout_language_item.setOnClickListener { getItem(position).callback.accept(getItem(position).locale) }
                    holder.iv_rta.visibility = when (iCurrentLocaleIdx == position) {
                        true -> VISIBLE
                        false -> GONE
                    }
                }
            }
        }
        tv_title.text = resources.getString(R.string.change_language)
        iv_left.apply {
            setImageResource(R.mipmap.ic_back)
            setOnClickListener { finish() }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    class LanguageItem(val resId: Int, val locale: String, val callback: Consumer<String>) {}

}
