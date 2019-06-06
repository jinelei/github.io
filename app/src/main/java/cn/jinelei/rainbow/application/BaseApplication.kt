package cn.jinelei.rainbow.application

import android.app.Application
import cn.jinelei.rainbow.BuildConfig
import cn.jinelei.rainbow.handler.CustomCrashHandler
import com.facebook.stetho.Stetho

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(CustomCrashHandler.instance)
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

    companion object {
        val instace = BaseApplication()
    }

}