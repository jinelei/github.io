package cn.jinelei.rainbow.util

import android.view.View


const val MIN_DELAY_TIME = 500
val map: MutableMap<View?, Long> = mutableMapOf()

fun isFastClick(view: View?): Boolean {
    val lastClickTime:Long = map[view] ?: 0L
    val currentClickTime = System.currentTimeMillis()
    var result = true
    if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
        result = false
    }
    map[view] = currentClickTime
    return result
}
