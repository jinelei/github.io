package cn.jinelei.rainbow.application

import android.app.Activity
import android.app.Application
import cn.jinelei.rainbow.handler.CustomCrashHandler

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(CustomCrashHandler.INSTANCE)
    }

    companion object {
        val INSTANCE = BaseApplication()
    }

}