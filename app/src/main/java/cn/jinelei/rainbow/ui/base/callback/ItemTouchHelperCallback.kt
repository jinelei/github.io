package cn.jinelei.rainbow.ui.base.callback

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import java.util.function.Consumer


open class ItemTouchHelperCallback(
	val mAdapter: ItemTouchHelperAdapter,
	val mContext: Context,
	var isCanDrag: Boolean = false,
	var isCanSwap: Boolean = false,
	var moveHook: Consumer<List<Int>>? = null,
	var swapHook: Consumer<Int>? = null
) : ItemTouchHelper.Callback() {
	private val mVibrator: Vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
	
	override fun isLongPressDragEnabled(): Boolean {
		return isCanDrag
	}
	
	override fun isItemViewSwipeEnabled(): Boolean {
		return isCanSwap
	}
	
	override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
		when (recyclerView.layoutManager) {
			is GridLayoutManager -> {
				val dragFlag =
					ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
				val swipeFlag = 0
				return makeMovementFlags(dragFlag, swipeFlag)
			}
			is LinearLayoutManager -> {
				val orientation = (recyclerView.layoutManager as LinearLayoutManager).orientation
				var dragFlag = 0
				var swipeFlag = 0
				if (orientation == LinearLayoutManager.HORIZONTAL) { // 如果是横向的布局
					swipeFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
					dragFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
				} else if (orientation == LinearLayoutManager.VERTICAL) { // 如果是竖向的布局，相当于ListView
					dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
					swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
				}
				return makeMovementFlags(dragFlag, swipeFlag)
			}
		}
		return 0
	}
	
	override fun onMove(
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder,
		target: RecyclerView.ViewHolder
	): Boolean {
		mAdapter.onMove(viewHolder.adapterPosition, target.adapterPosition)
		moveHook?.accept(listOf(viewHolder.adapterPosition, target.adapterPosition))
		return true
	}
	
	override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
		if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
			mVibrator.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
		}
		super.onSelectedChanged(viewHolder, actionState)
	}
	
	override fun onSwiped(viewHolder: RecyclerView.ViewHolder, p1: Int) {
		mAdapter.onSwipe(viewHolder.adapterPosition)
		swapHook?.accept(viewHolder.adapterPosition)
	}
}