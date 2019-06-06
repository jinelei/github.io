package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.util.SharedPreUtil
import cn.jinelei.rainbow.util.SharedPreUtil.Companion.KEY_DEBUG_FLAG


class SetupFragment : PreferenceFragmentCompat() {
    private var debugPref: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }

    private fun initView() {
        debugPref = preferenceManager.findPreference(KEY_DEBUG_FLAG) as ListPreference
        val debugLevel: Int =
            SharedPreUtil.readPre(context!!, SharedPreUtil.NAME_USER, SharedPreUtil.KEY_DEBUG_FLAG, 0) as Int
        debugPref?.summary = resources.getStringArray(R.array.debug_level)[debugLevel.dec()]
    }

    private fun initData() {
        debugPref?.setOnPreferenceChangeListener { _, debugFlag ->
            debugPref?.summary = resources.getStringArray(R.array.debug_level)[(debugFlag as String).toInt().dec()]
            SharedPreUtil.savePre(context!!, SharedPreUtil.NAME_USER, SharedPreUtil.KEY_DEBUG_FLAG, debugFlag)
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = SetupFragment()
    }
}