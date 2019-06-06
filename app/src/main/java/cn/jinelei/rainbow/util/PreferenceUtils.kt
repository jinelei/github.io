package cn.jinelei.rainbow.util

import android.content.Context

class SharedPreUtil {

    companion object {
        /**
         * 取SharedPreferences的key值。大部分数据都是存在这个里面
         */
        val NAME_USER = "NAME_USER"

        val KEY_DEBUG_FLAG = "KEY_DEBUG_FLAG"

        fun savePre(context: Context, name: String, key: String, value: Any) {
            val preference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = preference.edit()
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
                else -> editor.putString(key, value as String)
            }
            editor.commit()
        }

        fun readPre(context: Context, name: String, key: String, default: Any): Any {
            val value = context.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, default.toString())
            when (default) {
                is Boolean -> return value.toBoolean()
                is Int -> return value.toInt()
                is Long -> return value.toLong()
                is Float -> return value.toFloat()
                is String -> return value
                else -> return value
            }
        }
    }
}
