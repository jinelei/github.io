package cn.jinelei.rainbow.ui.common

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer


open class BaseRecyclerAdapter<M>(
    @LayoutRes val itemLayoutId: Int,
    val dataSet: MutableList<M> = mutableListOf(),
    bind: (BaseRecyclerAdapter<M>.() -> Unit)? = null
) : RecyclerView.Adapter<BaseRecyclerAdapter.CommonViewHolder>() {

    constructor(
        @LayoutRes itemLayoutId: Int,
        bind: (BaseRecyclerAdapter<M>.() -> Unit)? = null
    ) : this(itemLayoutId, mutableListOf<M>(), bind)

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

    //    添加item
    fun append(item: M) {
        val idx = dataSet.size
        dataSet.add(item)
        notifyItemInserted(idx)
    }

    //    清除所有的item
    fun clear() {
        dataSet.clear()
        notifyDataSetChanged()
    }

    //    重置所有的item
    fun reset(data: MutableList<M>) {
        dataSet.clear()
        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = dataSet[position]

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

    override fun getItemCount() = dataSet.size

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