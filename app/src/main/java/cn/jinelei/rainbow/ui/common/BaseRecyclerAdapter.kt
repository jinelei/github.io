package cn.jinelei.rainbow.ui.common

import android.service.autofill.Dataset
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer


open class BaseRecyclerAdapter<M>(
    @LayoutRes val itemLayoutId: Int,
    var dataList: MutableList<M> = mutableListOf(),
    bind: (BaseRecyclerAdapter<M>.() -> Unit)? = null
) : RecyclerView.Adapter<BaseRecyclerAdapter.CommonViewHolder>() {
    init {
        if (bind != null) {
            apply(bind)
        }
    }

    private var mOnItemClickListener: ((v: View, position: Int) -> Unit)? = null
    private val mOnItemLongClickListener: ((v: View, position: Int) -> Boolean) = { _, _ -> false }
    private var onBindViewHolder: ((holder: CommonViewHolder, position: Int) -> Unit)? = null

    fun onBindViewHolder(onBindViewHolder: ((holder: CommonViewHolder, position: Int) -> Unit)) {
        this.onBindViewHolder = onBindViewHolder
    }

    fun append(m: M) {
        val idx = dataList.size
        dataList.add(m)
        notifyItemInserted(idx)
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun reset(data: MutableList<M>) {
        dataList = data
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = dataList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(itemLayoutId, parent, false)
        val viewHolder = CommonViewHolder(itemView = itemView)
        itemView.setOnClickListener { mOnItemClickListener?.invoke(it, viewHolder.adapterPosition) }
        itemView.setOnLongClickListener {
            return@setOnLongClickListener mOnItemLongClickListener.invoke(
                it,
                viewHolder.adapterPosition
            )
        }
        return viewHolder
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        if (onBindViewHolder != null) {
            onBindViewHolder!!.invoke(holder, position)
        } else {
            bindData(holder, position)
        }
    }

    open fun bindData(holder: CommonViewHolder, position: Int) {}

    class CommonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View = itemView
    }
}