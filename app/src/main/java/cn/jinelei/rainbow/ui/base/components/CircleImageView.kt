package cn.jinelei.rainbow.ui.base.components

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import cn.jinelei.rainbow.R


class CircleImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {
	private val defaultScaleType = ScaleType.CENTER_CROP
	
	private val mDrawableRect = RectF()//图像矩形
	private val mBorderRect = RectF()  //边框矩形
	
	private val mShaderMatrix = Matrix()  //矩阵
	private val mBitmapPaint = Paint()   //图像画笔
	private val mBorderPaint = Paint()//边框画笔
	
	private var mBorderColor = Color.BLACK  //边框颜色
	private var mBorderWidth = 10  //边框宽度
	
	private var mBitmap: Bitmap? = null  //图片
	private var mBitmapShader: BitmapShader? = null  //渲染为圆
	private var mBitmapWidth: Int = 0   //位图宽度
	private var mBitmapHeight: Int = 0  //位图高度
	
	private var mDrawableRadius: Float = 0.toFloat()   //图片半径
	private var mBorderRadius: Float = 0.toFloat()  //边框半径
	
	private var mReady: Boolean = false
	private var mSetupPending: Boolean = false
	
	init {
		context?.obtainStyledAttributes(attrs, R.styleable.CircleImageView)?.let {
			mBorderWidth = it.getInteger(R.styleable.CircleImageView_defaultBorderWidth, mBorderWidth)
			mBorderColor = it.getColor(R.styleable.CircleImageView_defaultBorderColor, mBorderColor)
			it.recycle()
			init()
		}
	}
	
	private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
		if (drawable == null) {
			return null
		}
		
		if (drawable is BitmapDrawable) {
			return drawable.bitmap
		}
		
		try {
			val bitmap: Bitmap = when (drawable) {
				is ColorDrawable -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
				else -> Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
			}
			
			val canvas = Canvas(bitmap)
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
			drawable.draw(canvas)
			return bitmap
		} catch (e: OutOfMemoryError) {
			return null
		}
	}
	
	private fun init() {
		super.setScaleType(defaultScaleType)
		mReady = true
		if (mSetupPending) {
			setup()
			mSetupPending = false
		}
	}
	
	override fun getScaleType(): ScaleType {
		return defaultScaleType
	}
	
	override fun setScaleType(scaleType: ScaleType) {
		if (scaleType != defaultScaleType) {
			throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
		}
	}
	
	override fun onDraw(canvas: Canvas) {
		if (drawable == null) {
			return
		}
		canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mDrawableRadius, mBitmapPaint)
		if (mBorderWidth != 0) {
			canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mBorderRadius, mBorderPaint)
		}
	}
	
	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		setup()
	}
	
	override fun setImageBitmap(bm: Bitmap) {
		super.setImageBitmap(bm)
		mBitmap = bm
		setup()
	}
	
	override fun setImageDrawable(drawable: Drawable) {
		super.setImageDrawable(drawable)
		mBitmap = getBitmapFromDrawable(drawable)
		setup()
	}
	
	override fun setImageResource(resId: Int) {
		super.setImageResource(resId)
		mBitmap = getBitmapFromDrawable(drawable)
		setup()
	}
	
	override fun setImageURI(uri: Uri) {
		super.setImageURI(uri)
		mBitmap = getBitmapFromDrawable(drawable)
		setup()
	}
	
	private fun setup() {
		if (!mReady) {
			mSetupPending = true
			return
		}
		
		if (mBitmap == null) {
			return
		}
		
		mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
		
		mBitmapPaint.isAntiAlias = true
		mBitmapPaint.shader = mBitmapShader
		
		mBorderPaint.style = Paint.Style.STROKE
		mBorderPaint.isAntiAlias = true
		mBorderPaint.color = mBorderColor
		mBorderPaint.strokeWidth = mBorderWidth.toFloat()
		
		mBitmapHeight = mBitmap!!.height
		mBitmapWidth = mBitmap!!.width
		
		mBorderRect.set(0f, 0f, width.toFloat(), height.toFloat())
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2)
		
		mDrawableRect.set(
			mBorderWidth.toFloat(),
			mBorderWidth.toFloat(),
			mBorderRect.width() - mBorderWidth,
			mBorderRect.height() - mBorderWidth
		)
		mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2)
		
		updateShaderMatrix()
		invalidate()
	}
	
	private fun updateShaderMatrix() {
		val scale: Float
		var dx = 0f
		var dy = 0f
		
		mShaderMatrix.set(null)
		
		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
			scale = mDrawableRect.height() / mBitmapHeight.toFloat()
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
		} else {
			scale = mDrawableRect.width() / mBitmapWidth.toFloat()
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
		}
		
		mShaderMatrix.setScale(scale, scale)
		mShaderMatrix.postTranslate((dx + 0.5f) + mBorderWidth, (dy + 0.5f) + mBorderWidth)
		
		mBitmapShader!!.setLocalMatrix(mShaderMatrix)
	}
	
}