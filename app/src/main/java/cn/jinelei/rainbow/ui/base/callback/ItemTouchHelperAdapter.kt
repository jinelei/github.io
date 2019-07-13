package cn.jinelei.rainbow.ui.base.callback

interface ItemTouchHelperAdapter {
	fun onMove(fromPosition: Int, toPosition: Int)
	fun onSwipe(position: Int)
}