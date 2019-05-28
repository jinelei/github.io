package cn.jinelei.rainbow.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.greenrobot.eventbus.EventBus

class MainService : Service() {
    companion object {
        val TAG: String = MainService::class.java.simpleName
    }

    override fun onBind(intent: Intent): IBinder {
        return MainBinder()
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    class MainBinder : Binder() {
        fun testTask() {
            Thread(Runnable {
                Log.d(TAG, "test task");
            }).start()
        }
    }
}
