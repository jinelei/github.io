package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.util.SharedPreUtil
import cn.jinelei.rainbow.util.SharedPreUtil.Companion.KEY_DEBUG_FLAG


class SetupFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.findPreference(KEY_DEBUG_FLAG).apply {
            this.setOnPreferenceChangeListener { _, debugFlag ->
                this.summary = debugFlag.toString()
                true
            }
        }
    }

    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = SetupFragment()
    }
}