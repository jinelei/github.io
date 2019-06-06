package cn.jinelei.rainbow.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

fun isIntentAvailable(context: Context, action: String): Boolean {
    val packageManager = context.packageManager
    val intent = Intent(action)
    val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return list.size > 0
}