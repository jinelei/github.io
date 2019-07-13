package cn.jinelei.rainbow.ui.home

import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.util.Log
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BasePreferenceFragmentCompat
import cn.jinelei.rainbow.constant.PRE_KEY_DEBUG
import cn.jinelei.rainbow.constant.PRE_NAME_MINE


class SetupFragment : BasePreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }

    private fun initView() {
        (preferenceManager.findPreference(PRE_KEY_DEBUG) as ListPreference).let {
            val debugArray = resources.getStringArray(R.array.debug_level)
            val debugArrayValue = resources.getStringArray(R.array.debug_level_value)
            it.summary = debugArray[debugArrayValue.indexOf(readDebugLevel().toString())]
            it.setOnPreferenceChangeListener { preference, value ->
                when (value) {
                    is Int -> {
                        saveDebugLevel(value)
                        (preference as ListPreference).summary = debugArray[debugArrayValue.indexOf(value.toString())]
                    }
                    is String -> {
                        saveDebugLevel(value.toInt())
                        (preference as ListPreference).summary = debugArray[debugArrayValue.indexOf(value)]
                    }
                }
                true
            }
        }
    }

    private fun saveDebugLevel(level: Int) {
        mBaseApp.savePreference(
            name = PRE_NAME_MINE,
            key = PRE_KEY_DEBUG,
            defaultValue = level
        )
    }

    private fun readDebugLevel(): Int {
        return mBaseApp.readPreference(
            name = PRE_NAME_MINE,
            key = PRE_KEY_DEBUG,
            defaultValue = Log.VERBOSE
        ).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    companion object {
        val TAG = SetupFragment::class.java.simpleName ?: "SetupFragment"
        val instance by lazy { Holder.INSTANCE }
    }

    private object Holder {
        val INSTANCE = SetupFragment()
    }

}