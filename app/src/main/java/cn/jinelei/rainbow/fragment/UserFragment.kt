package cn.jinelei.rainbow.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat.canScrollVertically
import android.support.v7.widget.DividerItemDecoration
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
    var recyclerView: RecyclerView? = null
    val menus = listOf(
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
        recyclerView = view.findViewById<RecyclerView>(R.id.menu_recycler_view)
        recyclerView?.setHasFixedSize(true)
        var layoutManager = LinearLayoutManager(activity)
        recyclerView?.isNestedScrollingEnabled = false
        recyclerView?.setHasFixedSize(true)
        var itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        recyclerView?.addItemDecoration(itemDecoration);
        recyclerView?.layoutManager = layoutManager
        var listAdapter = ListMenuViewAdapter(menus)
        recyclerView?.adapter = listAdapter
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

}
