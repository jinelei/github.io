package cn.jinelei.rainbow.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
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
import cn.jinelei.rainbow.util.isFastClick
import android.os.Build
import cn.jinelei.rainbow.activity.*
import cn.jinelei.rainbow.application.BaseApplication
import cn.jinelei.rainbow.util.SharedPreUtil
import java.util.*


class UserFragment : BaseFragment() {
    val TAG = javaClass.simpleName
    var listRecyclerView: RecyclerView? = null
    var gridRecyclerView: RecyclerView? = null
    var rightBtn: ImageView? = null
    var leftBtn: ImageView? = null
    var navigationTitle: TextView? = null
    var userHeaderIcon: ImageView? = null
    var userHeaderInfo: TextView? = null
    val listMenu = arrayListOf(
        ListMenuItem(
            View.OnClickListener {
                if (!isFastClick(it)) startActivity(
                    Intent(
                        context,
                        ChangeLanguageActivity::class.java
                    )
                )
            }, "切换语言",
//            resources.getString(R.string.change_language),
            R.mipmap.ic_language
        ),
        ListMenuItem(
            View.OnClickListener { if (!isFastClick(it)) startActivity(Intent(context, SetupActivity::class.java)) },
            "首选项",
//            resources.getString(R.string.preference),
            R.mipmap.ic_setup
        )
    )
    val gridMenu = arrayListOf(
        ListMenuItem(
            View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.VERBOSE, "verbose") },
            "verbose",
            R.mipmap.ic_test
        ),
        ListMenuItem(
            View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.DEBUG, "debug") },
            "debug",
            R.mipmap.ic_test
        ),
        ListMenuItem(
            View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.INFO, "info") },
            "info",
            R.mipmap.ic_test
        ),
        ListMenuItem(
            View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.WARN, "warn") },
            "warn",
            R.mipmap.ic_test
        ),
        ListMenuItem(
            View.OnClickListener { _ -> (activity as BaseActivity).debug(Log.ERROR, "error") },
            "error",
            R.mipmap.ic_test
        ),
        ListMenuItem(
            View.OnClickListener {
                if (!isFastClick(it)) startActivity(Intent(activity, DeviceScanActivity::class.java))
            },
            "首选项", R.mipmap.ic_add
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false).apply {
            initView(this)
        }
    }

    private fun initView(view: View) {
        listRecyclerView = view.findViewById<RecyclerView>(R.id.list_menu_recycler_view).apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
            layoutManager = LinearLayoutManager(activity)
            adapter = ListMenuViewAdapter(listMenu)
        }
        gridRecyclerView = view.findViewById<RecyclerView>(R.id.grid_menu_recycler_view).apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(activity, 5)
            adapter = GridMenuViewAdapter(gridMenu)
        }
        rightBtn = view.findViewById<ImageView>(R.id.navigation_header_right).apply {
            setImageResource(R.mipmap.ic_add)
            setOnClickListener { if (!isFastClick(it)) startActivity(Intent(activity, DeviceScanActivity::class.java)) }
        }
        navigationTitle = view.findViewById<TextView>(R.id.navigation_header_title)
            .apply { text = resources.getString(R.string.navigation_user) }
        userHeaderIcon = view.findViewById<ImageView>(R.id.user_header_icon).apply {
            setImageResource(R.mipmap.ic_launcher)
        }
        userHeaderInfo = view.findViewById<TextView>(R.id.user_header_info).apply {
            text = "asdfasdfasdf"
        }
        view.findViewById<ConstraintLayout>(R.id.user_info_layout).apply {
            setOnClickListener { if (!isFastClick(it)) startActivity(Intent(context, UserInfoActivity::class.java)) }
        }
    }

    class ListMenuItem(var callback: View.OnClickListener, var title: String, var resourceId: Int)

    class ListMenuViewAdapter(var data: List<ListMenuItem>) :
        RecyclerView.Adapter<ListMenuViewAdapter.ListMenuItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListMenuItemViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.list_menu_layout, parent, false)
            return ListMenuItemViewHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ListMenuItemViewHolder, pos: Int) {
            val menu = data[pos]
            holder.icon.setImageResource(menu.resourceId)
            holder.title.text = menu.title
            holder.view.setOnClickListener(menu.callback)
        }

        class ListMenuItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val icon = view.findViewById<ImageView>(R.id.list_menu_icon)
            val title = view.findViewById<TextView>(R.id.list_menu_title)
        }

    }

    class GridMenuViewAdapter(var data: List<ListMenuItem>) :
        RecyclerView.Adapter<GridMenuViewAdapter.GridMenuItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridMenuItemViewHolder {
            return GridMenuItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.grid_menu_layout,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: GridMenuItemViewHolder, pos: Int) {
            val menu = data[pos]
            holder.icon.setImageResource(menu.resourceId)
            holder.title.text = menu.title
            holder.view.setOnClickListener(menu.callback)
        }

        class GridMenuItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val icon = view.findViewById<ImageView>(R.id.list_menu_icon)
            val title = view.findViewById<TextView>(R.id.list_menu_title)
        }

    }

    companion object {
        val instance = UserFragment()
        val name = "UserFragment"
    }
}
