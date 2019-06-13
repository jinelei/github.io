package cn.jinelei.rainbow.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseFragment
import cn.jinelei.rainbow.ui.common.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.discovery_card_layout.*
import kotlinx.android.synthetic.main.discovery_fragment.view.*

class DiscoveryFragment : BaseFragment() {
    private val mDataSet: MutableList<DiscoveryCardItem> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discovery_fragment, container, false).apply {
            initData()
            initView(this)
        }
    }

    private fun initData() {
        mDataSet.apply {
            clear()
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 1") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 2") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 3") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 4") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 5") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 6") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 7") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 8") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 9") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 10") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 11") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 12") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 13") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 14") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 15") })
            )
            add(
                DiscoveryCardItem(
                    R.mipmap.ws2812,
                    R.string.app_name,
                    View.OnClickListener { Log.d(TAG, "discovery 16") })
            )
        }
    }

    private fun initView(view: View) {
        val recyclerView = view.rv_discovery.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = BaseRecyclerAdapter(
                itemLayoutId = R.layout.discovery_card_layout,
                dataList = mDataSet
            ) {
                onBindViewHolder { holder, position ->
                    if (getItem(position).titleRes!! > 0)
                        holder.tv_nav_title.text = getString(getItem(position).titleRes!!)
                    if (getItem(position).backgroundRes!! > 0)
                        holder.iv_background.setImageResource(getItem(position).backgroundRes!!)
                    if (getItem(position).callback!! != null)
                        holder.layout_discovery_card.setOnClickListener(getItem(position).callback)
                }
            }
        }
    }

    class DiscoveryCardItem(val backgroundRes: Int?, val titleRes: Int?, val callback: View.OnClickListener?) {}

    companion object {
        val TAG = DiscoveryFragment::class.java.simpleName ?: "DiscoveryFragment"
        val instance by lazy { Holder.INSTANCE }

    }

    private object Holder {
        val INSTANCE = DiscoveryFragment()

    }
}