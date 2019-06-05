package cn.jinelei.rainbow.activity

import android.content.pm.PackageManager
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import cn.jinelei.rainbow.components.LoadingDialog
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

open class BaseActivity : AppCompatActivity() {
    private val grantedPermRunnable = HashMap<Int, Runnable>()
    private val deniedPermRunnable = HashMap<Int, Runnable>()
    private var loadingDialog: LoadingDialog? = null
    private var loadingDialogTimeoutJob: Job? = null
    private val DEFAULT_HIDE_LOADING_TIMEOUT = 10000L

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
        loadingDialog = LoadingDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
        loadingDialog = LoadingDialog(this)
    }

    override fun onStart() {
        super.onStart()
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onResume() {
        super.onResume()
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onPause() {
        super.onPause()
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStop() {
        super.onStop()
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestart() {
        super.onRestart()
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        Log.v(getTag(), Thread.currentThread().stackTrace[2].methodName)
    }

    fun getTag(): String {
        return this.javaClass.simpleName
    }

    protected fun customRequestPermission(id: Int, permission: String, grantedRun: Runnable?, deniedRun: Runnable?) {
        if (grantedRun != null)
            grantedPermRunnable[id] = grantedRun
        if (deniedRun != null)
            deniedPermRunnable[id] = deniedRun
        if (Build.VERSION.SDK_INT > 23) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), id)
                return
            } else {
                Log.v(getTag(), "id ${id} permission ${permission} granted")
                grantedRun?.run()
            }
        } else {
            Log.v(getTag(), "id ${id} permission ${permission} granted")
            grantedRun?.run()
        }
    }

    protected fun customRequestPermission(
        ids: List<Int>,
        permissions: List<String>,
        grantedRun: Runnable?,
        deniedRun: Runnable?
    ) {
        if (ids.size == permissions.size) {
            for (i in ids.indices) {
                val id = ids[i]
                val permission = permissions[i]
                this.customRequestPermission(id, permission, grantedRun, deniedRun)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val resultIterator = grantResults.iterator()
        val permissionIterator = permissions.iterator()
        while (resultIterator.hasNext()) {
            val result = resultIterator.next()
            val permission = permissionIterator.next()
            if (result == PackageManager.PERMISSION_GRANTED) {
                Log.v(getTag(), "id ${requestCode} permission ${permission} granted")
                grantedPermRunnable.get(requestCode)?.run()
            } else {
                Log.v(getTag(), "id ${requestCode} permission ${permission} denied")
                deniedPermRunnable.get(requestCode)?.run()
            }
        }
    }

    protected fun showLoading(timeout: Long = DEFAULT_HIDE_LOADING_TIMEOUT) {
        GlobalScope.launch(Dispatchers.Main) {
            if (loadingDialog?.isShowing == false) {
                Log.v(getTag(), "show loading dialog and set timeout: $timeout")
                loadingDialog?.show()
            }
        }
        loadingDialogTimeoutJob = GlobalScope.launch(Dispatchers.Default) {
            delay(timeout)
            GlobalScope.launch(Dispatchers.Main) {
                if (loadingDialog?.isShowing == true) {
                    Log.v(getTag(), "dismiss loading dialog in timeout job")
                    loadingDialog?.dismiss()
                }
            }
        }
    }

    protected fun hideLoading() {
        GlobalScope.launch(Dispatchers.Main) {
            if (loadingDialog?.isShowing == true) {
                Log.v(getTag(), "dismiss loading dialog")
                loadingDialog?.dismiss()
            }
        }
        if (loadingDialogTimeoutJob?.isActive == true) {
            Log.v(getTag(), "cancel loading dialog timeout job")
            loadingDialogTimeoutJob?.cancel()
        }
    }

}