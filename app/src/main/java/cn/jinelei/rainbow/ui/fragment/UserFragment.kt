package cn.jinelei.rainbow.ui.fragment

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.jinelei.rainbow.IBinderQuery
import cn.jinelei.rainbow.ITestService
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.base.BaseFragment
import cn.jinelei.rainbow.bluetooth.IBluetoothService
import cn.jinelei.rainbow.constant.BINDER_REQUEST_CODE_BLUETOOTH
import cn.jinelei.rainbow.constant.BINDER_REQUEST_CODE_TEST
import cn.jinelei.rainbow.service.MainService
import cn.jinelei.rainbow.ui.activity.*
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import cn.jinelei.rainbow.util.isFastClick
import kotlinx.android.synthetic.main.grid_menu_layout.*
import kotlinx.android.synthetic.main.include_top_navigation.view.*
import kotlinx.android.synthetic.main.list_menu_layout.*
import kotlinx.android.synthetic.main.user_fragment.view.*


class UserFragment : BaseFragment() {
    private lateinit var rvListRecyclerView: RecyclerView
    private lateinit var rvGridRecyclerView: RecyclerView
    private lateinit var ivUserHeaderIcon: ImageView
    private lateinit var tvUserHeaderInfo: TextView
    private val mMenuDataSet: MutableList<MenuItem> = mutableListOf()
    private val mGridMenuDataSet: MutableList<MenuItem> = mutableListOf()
    private var mBluetoothService: IBluetoothService? = null
    private var mTestService: ITestService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mTestService = null
            mBluetoothService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val mBinderQuery = IBinderQuery.Stub.asInterface(service)
            mBluetoothService =
                IBluetoothService.Stub.asInterface(mBinderQuery.queryBinder(BINDER_REQUEST_CODE_BLUETOOTH))
            mTestService =
                ITestService.Stub.asInterface(mBinderQuery.queryBinder(BINDER_REQUEST_CODE_TEST))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.user_fragment, container, false).apply {
            initData()
            initView(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyData()
    }

    private fun initView(view: View) {
        rvListRecyclerView = view.rv_list_menu.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(activity)
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.list_menu_layout,
                dataSet = mMenuDataSet
            ) {
                onBindViewHolder { holder, position ->
                    holder.iv_list_item_icon.setImageResource(getItem(position).resourceId)
                    holder.tv_list_item_title.text = getItem(position).title
                    holder.layout_list_menu.setOnClickListener(getItem(position).callback)
                }
            }
        }
        rvGridRecyclerView = view.rv_grid_menu.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(activity, 5)
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.grid_menu_layout,
                dataSet = mGridMenuDataSet
            ) {
                onBindViewHolder { holder, position ->
                    holder.iv_grid_item_icon.setImageResource(getItem(position).resourceId)
                    holder.tv_grid_item_title.text = getItem(position).title
                    holder.layout_grid_menu.setOnClickListener(getItem(position).callback)
                }
            }
        }
        view.iv_nav_right.apply {
            setImageResource(R.mipmap.ic_add)
            setOnClickListener { if (!isFastClick(it)) startActivity(Intent(activity, ScanDeviceActivity::class.java)) }
        }
        view.tv_nav_title
            .apply { text = resources.getString(R.string.navigation_user) }
        ivUserHeaderIcon = view.iv_user_avatar.apply {
            setImageResource(R.mipmap.ic_launcher)
        }
        tvUserHeaderInfo = view.tv_user_info.apply {
            text = resources.getString(R.string.navigation_user)
        }
        view.layout_user_info.apply {
            setOnClickListener { if (!isFastClick(it)) startActivity(Intent(context, UserInfoActivity::class.java)) }
        }
    }

    private fun initData() {
        activity?.bindService(Intent(activity, MainService::class.java), connection, BIND_AUTO_CREATE)
        mGridMenuDataSet.apply {
            clear()
            add(
                MenuItem(
                    View.OnClickListener { (activity as BaseActivity).debug(Log.VERBOSE, "verbose") },
                    "verbose",
                    R.mipmap.ic_test
                )
            )
            add(
                MenuItem(
                    View.OnClickListener { (activity as BaseActivity).debug(Log.DEBUG, "debug") },
                    "debug",
                    R.mipmap.ic_test
                )
            )
            add(
                MenuItem(
                    View.OnClickListener { (activity as BaseActivity).debug(Log.INFO, "info") },
                    "info",
                    R.mipmap.ic_test
                )
            )
            add(
                MenuItem(
                    View.OnClickListener { (activity as BaseActivity).debug(Log.WARN, "warn") },
                    "warn",
                    R.mipmap.ic_test
                )
            )
            add(
                MenuItem(
                    View.OnClickListener { (activity as BaseActivity).debug(Log.ERROR, "error") },
                    "error",
                    R.mipmap.ic_test
                )
            )
            add(
                MenuItem(
                    View.OnClickListener {
                        mTestService?.test("binder test")
                    },
                    "binder test",
                    R.mipmap.ic_test
                )
            )
            add(
                MenuItem(
                    View.OnClickListener {
                        mBluetoothService?.init(0, "init")
                    },
                    "binder bt",
                    R.mipmap.ic_test
                )
            )
            add(
                MenuItem(
                    View.OnClickListener {
                        if (!isFastClick(it)) startActivity(Intent(activity, ScanDeviceActivity::class.java))
                    },
                    resources.getString(R.string.scan_device), R.mipmap.ic_add
                )
            )
            add(
                MenuItem(
                    View.OnClickListener {
                        if (!isFastClick(it)) startActivity(Intent(activity, ScanWifiActivity::class.java))
                    },
                    resources.getString(R.string.scan_wifi), R.mipmap.ic_add
                )
            )
        }
        mMenuDataSet.apply {
            clear()
            add(
                MenuItem(
                    View.OnClickListener {
                        if (!isFastClick(it)) startActivity(
                            Intent(
                                context,
                                ChangeLanguageActivity::class.java
                            )
                        )
                    },
                    resources.getString(R.string.change_language),
                    R.mipmap.ic_language
                )
            )
            add(
                MenuItem(
                    View.OnClickListener {
                        if (!isFastClick(it)) startActivity(
                            Intent(
                                context,
                                SetupActivity::class.java
                            )
                        )
                    },
                    resources.getString(R.string.preference),
                    R.mipmap.ic_setup
                )
            )
        }
    }

    private fun destroyData() {
        activity?.unbindService(connection)
    }

    class MenuItem(var callback: View.OnClickListener, var title: String, var resourceId: Int)

    companion object {
        val TAG = UserFragment::class.java.simpleName ?: "UserFragment"
        val instance by lazy { Holder.INSTANCE }
    }

    private object Holder {
        val INSTANCE = UserFragment()
    }
}
