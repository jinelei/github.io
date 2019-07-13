package cn.jinelei.rainbow.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.app.BaseApp

open class BaseFragment : Fragment() {
    protected lateinit var mBaseApp: BaseApp
    protected lateinit var mBaseActivity: BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
        mBaseApp = this.activity?.application as BaseApp
        mBaseActivity = this.activity as BaseActivity
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

    override fun onDestroy() {
        super.onDestroy()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDetach() {
        super.onDetach()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.v(this.javaClass.simpleName, "${Thread.currentThread().stackTrace[2].methodName} hidden: ${hidden}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.v(this.javaClass.simpleName, Thread.currentThread().stackTrace[2].methodName)
    }

}