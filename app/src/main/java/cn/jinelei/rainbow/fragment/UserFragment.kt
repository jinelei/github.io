package cn.jinelei.rainbow.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import cn.jinelei.rainbow.activity.SettingActivity
import cn.jinelei.rainbow.util.isFastClick

class UserFragment : Fragment() {
    val TAG = javaClass.simpleName
    var listRecyclerView: RecyclerView? = null
    var gridRecyclerView: RecyclerView? = null
    val listMenu = listOf(
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home)
    )
    val gridMenu = listOf(
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home),
        ListMenuItem(View.OnClickListener { v -> Log.d(TAG, "asdfasdf") }, "设置", R.mipmap.ic_home)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)
        initView(view)
        val menuBtn = view.findViewById<ImageView>(R.id.menu_setting)
        menuBtn.setOnClickListener { v: View? ->
            if (!isFastClick(v))
                startActivity(Intent(activity, SettingActivity::class.java))
        }
        return view
    }

    fun initView(view: View) {
        listRecyclerView = view.findViewById(R.id.list_menu_recycler_view)
        listRecyclerView?.setHasFixedSize(true)
        listRecyclerView?.isNestedScrollingEnabled = false
        listRecyclerView?.setHasFixedSize(true)
        listRecyclerView?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        listRecyclerView?.layoutManager = LinearLayoutManager(activity)
        listRecyclerView?.adapter = ListMenuViewAdapter(listMenu)

        gridRecyclerView = view.findViewById(R.id.grid_menu_recycler_view)
        gridRecyclerView?.setHasFixedSize(true)
        gridRecyclerView?.isNestedScrollingEnabled = false
        gridRecyclerView?.setHasFixedSize(true)
        gridRecyclerView?.layoutManager = GridLayoutManager(activity, 5)
        gridRecyclerView?.adapter = GridMenuViewAdapter(gridMenu)

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
