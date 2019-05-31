package cn.jinelei.rainbow.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.jinelei.rainbow.R

class DiscoveryFragment : BaseFragment() {
    val TAG = javaClass.simpleName

    val datas = arrayListOf(
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.mipmap.ws2812,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 1") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 2") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 3") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 4") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 5") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 6") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 7") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 8") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 9") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 10") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 11") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 12") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 13") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 14") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 15") }),
        DiscoveryCardAdapter.DiscoveryCardItem(
            R.drawable.abc_cab_background_internal_bg,
            R.string.app_name,
            View.OnClickListener { Log.d(TAG, "discovery 16") })
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.discovery_fragment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.discovery_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = DiscoveryCardAdapter(datas)
    }

    companion object {
        val instance = SingletonHolder.holder
        val name = "DiscoveryFragment"
    }

    private object SingletonHolder {
        val holder = DiscoveryFragment()
    }

    class DiscoveryCardAdapter(val data: ArrayList<DiscoveryCardItem>) :
        RecyclerView.Adapter<DiscoveryCardAdapter.DiscoveryCardViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DiscoveryCardViewHolder {
            val view = LayoutInflater.from(p0.context).inflate(R.layout.discovery_card_layout, p0, false)
            return DiscoveryCardViewHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(viewHolder: DiscoveryCardViewHolder, position: Int) {
            val item = data[position]
            if (item.titleRes!! > 0) {
                viewHolder.title.text = instance.getString(item.titleRes)
            }
            if (item.backgroundRes!! > 0) {
                viewHolder.background.setImageResource(item.backgroundRes)
            }
            if (item.callback != null) {
                viewHolder.container.setOnClickListener(item.callback)
            }
        }

        class DiscoveryCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.discovery_card_title)
            val background: ImageView = itemView.findViewById(R.id.discovery_card_image)
            val container: ConstraintLayout = itemView.findViewById(R.id.discovery_card_container)
        }

        class DiscoveryCardItem(val backgroundRes: Int?, val titleRes: Int?, val callback: View.OnClickListener?) {}
    }
}