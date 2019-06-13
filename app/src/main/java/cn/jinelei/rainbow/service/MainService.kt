package cn.jinelei.rainbow.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import cn.jinelei.rainbow.ITestService
import org.greenrobot.eventbus.EventBus

class MainService : Service() {
    private lateinit var mTestService: TestService
    override fun onBind(intent: Intent): IBinder {
        return mTestService
    }

    override fun onCreate() {
        super.onCreate()
        mTestService = TestService()
    }

    class TestService : ITestService.Stub() {
        override fun test(aString: String?) {
            Log.d(TestService::class.java.simpleName, "test() receive: $aString")
        }

    }

}
