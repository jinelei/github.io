package cn.jinelei.rainbow.ui.fragment

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.app.BaseApp
import cn.jinelei.rainbow.constant.PRE_KEY_DEBUG
import cn.jinelei.rainbow.constant.PRE_NAME_USER


class SetupFragment : PreferenceFragmentCompat() {
    private lateinit var lpDebug: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }

    private fun initView() {
        lpDebug = preferenceManager.findPreference(PRE_KEY_DEBUG) as ListPreference
        val debugLevel = (activity?.applicationContext as BaseApp).readPreference(
            name = PRE_NAME_USER,
            key = PRE_KEY_DEBUG,
            defaultValue = 2
        )
        updateDebugLevel(debugLevel)
    }

    private fun initData() {
        lpDebug.setOnPreferenceChangeListener { _, debugFlag ->
            updateDebugLevel((debugFlag as String).toInt())
            (activity?.applicationContext as BaseApp).savePreference(
                name = PRE_NAME_USER,
                key = PRE_KEY_DEBUG,
                defaultValue = debugFlag.toInt()
            )
            true
        }
    }

    private fun updateDebugLevel(debugLevel: Int) {
        val debugArray = resources.getStringArray(R.array.debug_level)
        val debugArrayValue = resources.getStringArray(R.array.debug_level_value)
        lpDebug.summary = debugArray[debugArrayValue.indexOf(debugLevel.toString())]
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