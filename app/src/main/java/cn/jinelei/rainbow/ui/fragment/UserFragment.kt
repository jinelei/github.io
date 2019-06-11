package cn.jinelei.rainbow.ui.fragment

import android.content.Intent
import android.os.Bundle
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
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseActivity
import cn.jinelei.rainbow.base.BaseFragment
import cn.jinelei.rainbow.ui.activity.ChangeLanguageActivity
import cn.jinelei.rainbow.ui.activity.DeviceScanActivity
import cn.jinelei.rainbow.ui.activity.SetupActivity
import cn.jinelei.rainbow.ui.activity.UserInfoActivity
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import cn.jinelei.rainbow.util.isFastClick
import kotlinx.android.synthetic.main.list_menu_layout.*
import kotlinx.android.synthetic.main.include_top_navigation.view.*
import kotlinx.android.synthetic.main.user_fragment.view.*


class UserFragment : BaseFragment() {
    val TAG = javaClass.simpleName
    var rvListRecyclerView: RecyclerView? = null
    var cyGridRecyclerView: RecyclerView? = null
    var ivRightBtn: ImageView? = null
    var ivLeftBtn: ImageView? = null
    var tvNavigationTitle: TextView? = null
    var ivUserHeaderIcon: ImageView? = null
    var tvUserHeaderInfo: TextView? = null
    var mListMenuDataSet: MutableList<ListMenuItem> = mutableListOf()
    var mGridMenuDataSet: MutableList<ListMenuItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false).apply {
            initData()
            initView(this)
        }
    }

    private fun initView(view: View) {
        rvListRecyclerView = view.rv_list_menu.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
            layoutManager = LinearLayoutManager(activity)
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.list_menu_layout,
                dataList = mListMenuDataSet
            ) {
                onBindViewHolder { holder, position ->
                    holder.iv_icon.setImageResource(getItem(position).resourceId)
                    holder.tv_title.text = getItem(position).title
                    holder.layout_grid_menu.setOnClickListener(getItem(position).callback)
                }
            }
        }
        cyGridRecyclerView = view.rv_grid_menu.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(activity, 5)
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.grid_menu_layout,
                dataList = mGridMenuDataSet
            ) {
                onBindViewHolder { holder, position ->
                    holder.iv_icon.setImageResource(getItem(position).resourceId)
                    holder.tv_title.text = getItem(position).title
                    holder.layout_grid_menu.setOnClickListener(getItem(position).callback)
                }
            }
        }
        ivRightBtn = view.iv_right.apply {
            setImageResource(R.mipmap.ic_add)
            setOnClickListener { if (!isFastClick(it)) startActivity(Intent(activity, DeviceScanActivity::class.java)) }
        }
        tvNavigationTitle = view.tv_title
            .apply { text = resources.getString(R.string.navigation_user) }
        ivUserHeaderIcon = view.iv_user_avatar.apply {
            setImageResource(R.mipmap.ic_launcher)
        }
        tvUserHeaderInfo = view.tv_user_info.apply {
            text = "asdfasdfasdf"
        }
        view.layout_user_info.apply {
            setOnClickListener { if (!isFastClick(it)) startActivity(Intent(context, UserInfoActivity::class.java)) }
        }
    }

    private fun initData() {
        mListMenuDataSet.apply {
            clear()
            add(
                ListMenuItem(
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
                ListMenuItem(
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
        mGridMenuDataSet.apply {
            clear()
            add(
                ListMenuItem(
                    View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.VERBOSE, "verbose") },
                    "verbose",
                    R.mipmap.ic_test
                )
            )
            add(
                ListMenuItem(
                    View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.DEBUG, "debug") },
                    "debug",
                    R.mipmap.ic_test
                )
            )
            add(
                ListMenuItem(
                    View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.INFO, "info") },
                    "info",
                    R.mipmap.ic_test
                )
            )
            add(
                ListMenuItem(
                    View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.WARN, "warn") },
                    "warn",
                    R.mipmap.ic_test
                )
            )
            add(
                ListMenuItem(
                    View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.ERROR, "error") },
                    "error",
                    R.mipmap.ic_test
                )
            )
            add(
                ListMenuItem(
                    View.OnClickListener {
                        if (!isFastClick(it)) startActivity(Intent(activity, DeviceScanActivity::class.java))
                    },
                    resources.getString(R.string.preference), R.mipmap.ic_add
                )
            )
        }
    }

    class ListMenuItem(var callback: View.OnClickListener, var title: String, var resourceId: Int)

    companion object {
        val instance = UserFragment()
        val name = "UserFragment"
    }
}
