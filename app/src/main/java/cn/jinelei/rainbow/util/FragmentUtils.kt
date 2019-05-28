package cn.jinelei.rainbow.util

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log

const val SWITCH_FRAGMENT_TAG = "SwitchFragment"

fun switchFragment(
    fragmentManager: FragmentManager, containerId: Int,
    currentFragment: Fragment, targetFragment: Fragment
): Fragment {
    Log.v(
        SWITCH_FRAGMENT_TAG,
        "from ${currentFragment::class.java.simpleName} jump to ${targetFragment::class.java.simpleName}"
    )
    if (currentFragment != targetFragment) {
        val transaction = fragmentManager.beginTransaction()
        if (targetFragment.isAdded)
            transaction.hide(currentFragment).show(targetFragment).commit()
        else
            transaction.hide(currentFragment).add(containerId, targetFragment).commit()
        return targetFragment
    }
    return currentFragment
}