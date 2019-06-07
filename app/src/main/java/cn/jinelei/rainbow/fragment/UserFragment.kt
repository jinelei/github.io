package cn.jinelei.rainbow.fragment

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
import cn.jinelei.rainbow.activity.DeviceScanActivity
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.activity.BaseActivity
import cn.jinelei.rainbow.activity.SetupActivity
import cn.jinelei.rainbow.util.isFastClick


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
            View.OnClickListener { v ->
                if (!isFastClick(v))
                    startActivity(Intent(context, SetupActivity::class.java))
            },
            "首选项",
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
        ListMenuItem(View.OnClickListener { v ->
            if (!isFastClick(v)) startActivity(
                Intent(activity, DeviceScanActivity::class.java)
            )
        }, "添加设备", R.mipmap.ic_add)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)
        initView(view)
        return view
    }

    fun initView(view: View) {
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
            setOnClickListener {
                if (!isFastClick(it))
                    startActivity(Intent(activity, DeviceScanActivity::class.java))
            }
        }
        navigationTitle = view.findViewById<TextView>(R.id.navigation_header_title)
            .apply { text = resources.getString(R.string.navigation_user) }
        userHeaderIcon = view.findViewById<ImageView>(R.id.user_header_icon).apply {
            setImageResource(R.mipmap.ic_launcher)
        }
        userHeaderInfo = view.findViewById<TextView>(R.id.user_header_info).apply {
            text = "asdfasdfasdf"
        }
    }

    companion object {
        val instance = SingletonHolder.holder
        val name = "UserFragment"
    }

    private object SingletonHolder {
        val holder = UserFragment()
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
            var view = LayoutInflater.from(parent.context).inflate(R.layout.grid_menu_layout, parent, false)
            return GridMenuItemViewHolder(view)
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

}
