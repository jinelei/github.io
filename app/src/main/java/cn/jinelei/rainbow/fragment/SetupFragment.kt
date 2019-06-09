package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.activity.BaseActivity
import cn.jinelei.rainbow.application.BaseApplication
import cn.jinelei.rainbow.util.SharedPreUtil
import cn.jinelei.rainbow.util.SharedPreUtil.Companion.KEY_DEBUG_FLAG


class SetupFragment : PreferenceFragmentCompat() {
    private var debugPref: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }

    private fun initView() {
        debugPref = preferenceManager.findPreference(KEY_DEBUG_FLAG) as ListPreference
        val debugLevel = (activity?.applicationContext as BaseApplication).readPreference(
            name = SharedPreUtil.NAME_USER,
            key = SharedPreUtil.KEY_DEBUG_FLAG,
            defaultValue = 2
        )
        updateDebugLevel(debugLevel)
    }

    private fun initData() {
        debugPref?.setOnPreferenceChangeListener { _, debugFlag ->
            updateDebugLevel((debugFlag as String).toInt())
            (activity?.applicationContext as BaseApplication).savePreference(
                name = SharedPreUtil.NAME_USER,
                key = SharedPreUtil.KEY_DEBUG_FLAG,
                defaultValue = debugFlag.toInt()
            )
            true
        }
    }

    private fun updateDebugLevel(debugLevel: Int) {
        val debugArray = resources.getStringArray(R.array.debug_level)
        val debugArrayValue = resources.getStringArray(R.array.debug_level_value)
        debugPref?.summary = debugArray[debugArrayValue.indexOf(debugLevel.toString())]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    companion object {
        val instance = SetupFragment()
    }

}