package cn.jinelei.rainbow.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStart() {
        super.onStart()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onResume() {
        super.onResume()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onPause() {
        super.onPause()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStop() {
        super.onStop()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestart() {
        super.onRestart()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }
}