package cn.jinelei.rainbow.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import java.time.format.DecimalStyle

class ColorPickerView(context: Context, attrs: AttributeSet?, defStyle: Int) : ImageView(context, attrs, defStyle) {
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    open var listener: OnColorChangedListener? = null

    interface OnColorChangedListener {
        fun onColorChanged(r: Int, g: Int, b: Int)
        fun onMoveColor(r: Int, g: Int, b: Int)
    }
}