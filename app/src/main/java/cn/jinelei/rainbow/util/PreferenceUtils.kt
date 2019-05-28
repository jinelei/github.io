package cn.jinelei.rainbow.util

import android.content.ContentValues
import android.content.Context
import android.util.Log

class SharedPreUtil {

    private val arrspeed: String? = null

    companion object {
        /**
         * 取SharedPreferences的key值。大部分数据都是存在这个里面
         */
        val KEY_USER = "KEY_USER"


        fun savePre(context: Context, name: String, key: String, value: String) {
            val preference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = preference.edit()
            editor.putString(key, value)
            editor.commit()
        }

        fun readPre(context: Context?, name: String, key: String): String? {
            if (null == context) {
                Log.e("", "共享参数context为空")
                return ""
            }
            /*if(key.equals(MID)){
			return "test";
		}*/
            val preference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            return preference.getString(key, "")
        }

        fun readPre(context: Context?, name: String, key: String, type: String): String? {
            if (null == context) {
                Log.e("s", "共享参数context为空")
                return type
            }

            val preference = context.getSharedPreferences(name, Context.MODE_PRIVATE)

            return preference.getString(key, type)
        }

        fun delPre(context: Context, name: String, key: String?) {
            val preference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = preference.edit()
            if (key == null || "" == key) {
                editor.clear()
            } else {
                editor.remove(key)
            }
            editor.commit()
        }

        /**
         * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
         *
         * @param context
         * @param key
         * @param object
         */
        fun setParam(context: Context, name: String, key: String, `object`: Any) {
            val type = `object`.javaClass.simpleName
            val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = sp.edit()
            if ("String" == type) {
                editor.putString(key, `object` as String)
            } else if ("Integer" == type) {
                editor.putInt(key, `object` as Int)
            } else if ("Boolean" == type) {
                editor.putBoolean(key, `object` as Boolean)
            } else if ("Float" == type) {
                editor.putFloat(key, `object` as Float)
            } else if ("Long" == type) {
                editor.putLong(key, `object` as Long)
            }
            editor.commit()
        }

        /**
         * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
         *
         * @param context
         * @param key
         * @param defaultObject
         * @return
         */
        fun getParam(context: Context, name: String, key: String, defaultObject: Any): Any? {
            val type = defaultObject.javaClass.simpleName
            val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)

            if ("String" == type) {
                return sp.getString(key, defaultObject as String)
            } else if ("Integer" == type) {
                return sp.getInt(key, defaultObject as Int)
            } else if ("Boolean" == type) {
                return sp.getBoolean(key, defaultObject as Boolean)
            } else if ("Float" == type) {
                return sp.getFloat(key, defaultObject as Float)
            } else if ("Long" == type) {
                return sp.getLong(key, defaultObject as Long)
            }
            return null
        }

        /**
         * 判断是否存在
         *
         * @param context
         * @param name
         */
        fun existPre(context: Context, name: String): Boolean {
            val preference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = preference.edit()
            return if (editor != null) {
                true
            } else false
        }

        /**
         * 清空所有key与值
         *
         * @param context
         * @param name
         */
        fun clearPre(context: Context, name: String) {
            val preference = context.getSharedPreferences(name, Context.MODE_PRIVATE)
            val editor = preference.edit()
            editor?.clear()
            editor!!.commit()
        }
    }

}
