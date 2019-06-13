package cn.jinelei.rainbow.base

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import cn.jinelei.rainbow.app.BaseApp

abstract class BasePreferenceFragmentCompat : PreferenceFragmentCompat() {
    protected lateinit var mBaseApp: BaseApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseApp = this.activity?.application as BaseApp
    }
}