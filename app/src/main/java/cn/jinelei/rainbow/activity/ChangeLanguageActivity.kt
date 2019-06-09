package cn.jinelei.rainbow.activity

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.application.BaseApplication
import cn.jinelei.rainbow.util.SharedPreUtil
import java.util.*
import java.util.function.Consumer

class ChangeLanguageActivity : AppCompatActivity() {
    var currentLocaleIdx: Int = 0

    val allSupportLocales = arrayListOf(
        LanguageItem(R.mipmap.ic_option, "跟随系统", Consumer { changeLocale(Locale.getDefault()) }),
        LanguageItem(R.mipmap.ic_option, Locale.ENGLISH.language, Consumer { changeLocale(Locale.ENGLISH) }),
        LanguageItem(
            R.mipmap.ic_option,
            Locale.SIMPLIFIED_CHINESE.language,
            Consumer { changeLocale(Locale.SIMPLIFIED_CHINESE) })
    )

    fun changeLocale(t: Locale) {
        (applicationContext as BaseApplication).savePreference(
            name = SharedPreUtil.NAME_USER,
            key = SharedPreUtil.KEY_LANGUAGE,
            defaultValue = t.language
        )
        currentLocaleIdx =
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
        val languageString = (applicationContext as BaseApplication).readPreference(
            SharedPreUtil.NAME_USER,
            SharedPreUtil.KEY_LANGUAGE,
            Locale.getDefault().language
        )
        currentLocaleIdx =
            allSupportLocales.indexOfFirst { languageItem -> languageItem.locale.equals(languageString) }
        val languageRecyclerView = findViewById<RecyclerView>(R.id.language_recyclerview).apply {
            layoutManager = LinearLayoutManager(this@ChangeLanguageActivity)
            adapter = LanguageAdapter(allSupportLocales, currentLocaleIdx)
        }
        findViewById<TextView>(R.id.navigation_header_title).text = resources.getString(R.string.change_language)
        findViewById<ImageView>(R.id.navigation_header_left).apply {
            setImageResource(R.mipmap.ic_back)
            setOnClickListener { finish() }
        }
    }

    override fun onResume() {
        super.onResume()
    }


    class LanguageAdapter(val languages: List<LanguageItem>, val currentLocaleIdx: Int) :
        RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
            return LanguageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.language_item_layout,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return languages.size
        }

        override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
            val languageItem = languages[position]
            holder.icon.setImageResource(languageItem.resId)
            holder.title.text = languageItem.locale
            holder.iconOk.visibility = when (currentLocaleIdx == position) {
                true -> VISIBLE
                false -> GONE
            }
            holder.container.setOnClickListener { languageItem.callback.accept(languageItem.locale) }
        }

        class LanguageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val icon = view.findViewById<ImageView>(R.id.language_item_icon)
            val title = view.findViewById<TextView>(R.id.language_item_title)
            val iconOk = view.findViewById<ImageView>(R.id.language_item_icon_ok)
            val container = view.findViewById<ConstraintLayout>(R.id.language_item_container)
        }

    }

    class LanguageItem(val resId: Int, val locale: String, val callback: Consumer<String>) {}

}
