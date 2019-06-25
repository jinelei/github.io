package cn.jinelei.rainbow.ui.fragment


import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseFragment
import kotlin.random.Random

val sleepDataList = mutableListOf<SleepData>(
//	SleepData(80, 0),
//	SleepData(80, 1),
//	SleepData(80, 2),
//	SleepData(80, 3),
//	SleepData(40, 1),
//	SleepData(40, 3),
//	SleepData(30, 0),
//	SleepData(30, 3)

)

class TestFragment : BaseFragment() {
	lateinit var dvTest: DrawView
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_test, container, false)
		dvTest = view.findViewById(R.id.dv_test)
		view.findViewById<FloatingActionButton>(R.id.fab_refresh).apply {
			setOnClickListener {
				Log.v(TestFragment::class.java.simpleName, "refresh")
				randomSleepData()
				dvTest.invalidate()
			}
		}
		return view
	}
	
	private fun randomSleepData() {
		var count = 10
		var lastType = 0
		var tmpType = 0
		var lastWidth = 0
		var tmpWidth = 0
		sleepDataList.clear()
		while (sleepDataList.size < count) {
			while (tmpType == lastType || tmpType == 0)
				tmpType = Random.nextInt(0, 4)
			while (tmpWidth == lastWidth || tmpWidth == 0)
				tmpWidth = 10 * Random.nextInt(0, 10)
			sleepDataList.add(SleepData(tmpWidth, tmpType))
			lastType = tmpType
			lastWidth = tmpWidth
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		randomSleepData()
	}
	
}

class SleepData(val width: Int, val type: Int) {
	override fun toString(): String {
		return "SleepData(width=$width, type=$type)"
	}
}

class DrawView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
	private var defaultRadius: Float = 20F           // 默认圆角半径
	private var defaultLineHeight = 80F     // 默认数据显示段高度
	private var defaultLinkOffsetX = 5F      // 默认X偏移量
	private var defaultLinkOffsetY = 100F     // 默认Y偏移量
	private var defaultStartX = 100F
	private var defaultStartY = 100F
	
	init {
		//初始化的时候获取自定义的属性，获取的是主布局中自己赋给自定义属性的值，第二个参数是如果你没有赋值，就是用默认值。
		context?.obtainStyledAttributes(attrs, R.styleable.DrawView)?.let {
			it?.let {
				defaultRadius = it.getFloat(R.styleable.DrawView_defaultRadius, defaultRadius)
				defaultLineHeight = it.getFloat(R.styleable.DrawView_defaultLineHeight, defaultLineHeight)
				defaultLinkOffsetX = it.getFloat(R.styleable.DrawView_defaultLinkOffsetX, defaultLinkOffsetX)
				defaultLinkOffsetY = it.getFloat(R.styleable.DrawView_defaultLinkOffsetY, defaultLinkOffsetY)
				defaultStartX = it.getFloat(R.styleable.DrawView_defaultStartX, defaultStartX)
				defaultStartY = it.getFloat(R.styleable.DrawView_defaultStartY, defaultStartY)
				it.recycle();//释放资源
			}
		}
	}
	
	fun mArcTo(path: Path, p1: MPoint, p2: MPoint, type: Int): MPoint {
		Log.v(TestFragment::class.java.simpleName, "mArcTo: $p1 $p2 type: $type")
		val radius = Math.max(Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y))
		var minX = Math.min(p1.x, p2.x)
		var minY = Math.min(p1.y, p2.y)
		when (type) {
			1 -> {
				if (p1.x < p2.x) {
					path.arcTo(minX - radius, minY, minX + radius, minY + 2 * radius, 270F, 90F, false)
				} else {
					path.arcTo(minX - radius, minY, minX + radius, minY + 2 * radius, 0F, -90F, false)
				}
			}
			2 -> {
				if (p1.x > p2.x) {
					path.arcTo(minX - radius, minY - radius, minX + radius, minY + radius, 0F, 90F, false)
				} else {
					path.arcTo(minX - radius, minY - radius, minX + radius, minY + radius, 90F, -90F, false)
				}
			}
			3 -> {
				if (p1.x > p2.x) {
					path.arcTo(minX, minY - radius, minX + radius * 2, minY + radius, 90F, 90F, false)
				} else {
					path.arcTo(minX, minY - radius, minX + radius * 2, minY + radius, 180F, -90F, false)
				}
			}
			4 -> {
				if (p1.x < p2.x) {
					path.arcTo(minX, minY, minX + radius * 2, minY + radius * 2, 180F, 90F, false)
				} else {
					path.arcTo(minX, minY, minX + radius * 2, minY + radius * 2, 270F, -90F, false)
				}
			}
		}
		path.setLastPoint(p2.x, p2.y)
		return p2
	}
	
	fun mLineTo(path: Path, pos: MPoint): MPoint {
		Log.v(TestFragment::class.java.simpleName, "mLineTo: $pos")
		path.lineTo(pos.x, pos.y)
		return pos
	}
	
	override fun onDraw(canvas: Canvas?) {
		if (canvas == null)
			return
		for (i in 1..20) {
			canvas.drawLine(0F, 100F * i, 1000F, 100F * i, Paint().apply {
				color = Color.RED
				style = Paint.Style.FILL
			})
			canvas.drawLine(100F * i, 0F, 100F * i, 2000F, Paint().apply {
				color = Color.RED
				style = Paint.Style.FILL
			})
		}
		canvas.drawPath(Path().apply {
			var lastPos = MPoint(0F, 0F)
			var firstPos = MPoint(0F, 0F)
			var lastSleepData: SleepData? = null
			for (index in 0 until sleepDataList.size) {
				var sleepData = sleepDataList[index]
				Log.v(TestFragment::class.java.simpleName, "index: $index, $sleepData")
				var tmpPos = MPoint(0F, 0F)
				if (index == 0) {
					// 绘制左上角半圆角
					firstPos =
						MPoint(
							defaultStartX + calcRadius(sleepData.width),
							defaultStartY + (defaultRadius * 2 + defaultLinkOffsetY + defaultLineHeight) * sleepData.type
						)
					Log.v(TestFragment::class.java.simpleName, "first: $firstPos")
					moveTo(firstPos.x, firstPos.y)
					lastPos = MPoint(firstPos.x - calcRadius(sleepData.width), firstPos.y + calcRadius(sleepData.width))
					lastPos = mArcTo(this, firstPos, lastPos, 4)
					// 绘制左侧数据区高度
					lastPos = MPoint(lastPos.x, lastPos.y + defaultLineHeight)
					lastPos = mLineTo(this, lastPos)
					// 绘制左下角半圆角
					tmpPos = MPoint(lastPos.x + calcRadius(sleepData.width), lastPos.y + calcRadius(sleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 3)
					// 绘制横线
					lastPos = MPoint(lastPos.x + sleepData.width - calcRadius(sleepData.width), lastPos.y)
					lastPos = mLineTo(this, lastPos)
				}
				
				// 绘制中部
				if (lastSleepData == null) {
					lastSleepData = sleepData
					continue
				}
				val changedLine = sleepData.type - lastSleepData.type
				if (changedLine > 0) { // 顺X轴，下降
					// 左侧
					tmpPos = MPoint(
						lastPos.x + calcRadius(lastSleepData.width),
						lastPos.y + calcRadius(lastSleepData.width)
					)
					lastPos = mArcTo(this, lastPos, tmpPos, 1)
					// 绘制连接线，根据changedLine
					tmpPos = MPoint(
						lastPos.x + defaultLinkOffsetX,
						lastPos.y + (defaultLinkOffsetY + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPos.y = tmpPos.y + (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPos = mLineTo(this, tmpPos)
					// 右侧
					tmpPos =
						MPoint(lastPos.x + calcRadius(sleepData.width), lastPos.y + calcRadius(sleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 3)
				} else { // 顺X轴，上升
					// 左侧
					tmpPos = MPoint(
						lastPos.x + calcRadius(lastSleepData.width),
						lastPos.y - calcRadius(lastSleepData.width)
					)
					lastPos = mArcTo(this, lastPos, tmpPos, 2)
					// 绘制连接线，根据changedLine
					tmpPos = MPoint(
						lastPos.x + defaultLinkOffsetX,
						lastPos.y + (defaultLinkOffsetY + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPos.y = tmpPos.y - (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPos = mLineTo(this, tmpPos)
					// 右侧
					tmpPos =
						MPoint(lastPos.x + calcRadius(sleepData.width), lastPos.y - calcRadius(sleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 4)
				}
				// 绘制底部横线
				lastPos = mLineTo(this, MPoint(lastPos.x + calcRadius(sleepData.width), lastPos.y))
				
				if (index == sleepDataList.size - 1) { // 最后一个
					// 绘制右下角半圆角
					tmpPos = MPoint(lastPos.x + calcRadius(sleepData.width), lastPos.y - calcRadius(sleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 2)
					// 绘制左侧数据区高度
					lastPos = MPoint(lastPos.x, lastPos.y - defaultLineHeight)
					lastPos = mLineTo(this, lastPos)
					// 绘制右上角半圆角
					tmpPos = MPoint(lastPos.x - calcRadius(sleepData.width), lastPos.y - calcRadius(sleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 1)
					// 绘制横线
					lastPos = MPoint(lastPos.x - calcRadius(sleepData.width), lastPos.y)
					lastPos = mLineTo(this, lastPos)
				}
				lastSleepData = sleepData
			}
			
			Log.v(TestFragment::class.java.simpleName, "reverse")
			var tmpIdx = 0
			var sleepData = SleepData(0, 0)
			for (index in 1 until sleepDataList.size) {
				tmpIdx = sleepDataList.size - 1 - index
				sleepData = sleepDataList[tmpIdx]
				Log.v(TestFragment::class.java.simpleName, "reverse index: $tmpIdx, $sleepData")
				if (lastSleepData == null) return
				val changedLine = lastSleepData.type - sleepData.type
				var tmpPos: MPoint = MPoint(defaultStartX, defaultStartY)
				if (changedLine > 0) { // 逆着X轴，上升
					// 绘制连接线右侧半圆
					tmpPos =
						MPoint(lastPos.x - calcRadius(lastSleepData.width), lastPos.y - calcRadius(lastSleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 3)
					// 绘制连接线，根据changedLine
					tmpPos = MPoint(
						lastPos.x - defaultLinkOffsetX,
						lastPos.y - (defaultLinkOffsetY + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPos.y = tmpPos.y - (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPos = mLineTo(this, tmpPos)
					// 绘制连接线左侧半圆
					tmpPos = MPoint(lastPos.x - calcRadius(sleepData.width), lastPos.y - calcRadius(sleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 1)
					// 绘制横线
					lastPos = mLineTo(this, MPoint(lastPos.x - calcRadius(sleepData.width), lastPos.y))
				} else {// 逆着X轴， 下降
					// 绘制连接线右侧半圆
					tmpPos =
						MPoint(lastPos.x - calcRadius(lastSleepData.width), lastPos.y + calcRadius(lastSleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 4)
					// 绘制连接线，根据changedLine
					tmpPos = MPoint(
						lastPos.x - defaultLinkOffsetX,
						lastPos.y - (defaultLinkOffsetY + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPos.y = tmpPos.y + (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPos = mLineTo(this, tmpPos)
					// 绘制连接线左侧半圆
					tmpPos = MPoint(lastPos.x - calcRadius(sleepData.width), lastPos.y + calcRadius(sleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 2)
					// 绘制横线
					lastPos = mLineTo(this, MPoint(lastPos.x - calcRadius(sleepData.width), lastPos.y))
				}
				lastSleepData = sleepData
			}
			mLineTo(this, firstPos)
			close()
		}, Paint().apply {
			var left = 100F
			var top = 100F
			var bottom = 400F
			color = Color.RED
			shader = LinearGradient(
				left,
				top,
				left,
				bottom,
				Color.parseColor("#FFFF00FF"),
				Color.parseColor("#FFFFFF00"),
				Shader.TileMode.CLAMP
			)
			xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
			style = Paint.Style.FILL
		})
	}
	
	class MPoint(var x: Float, var y: Float) {
		override fun toString(): String {
			return "MPoint(x=$x, y=$y)"
		}
	}
	
	fun calcRadius(width: Int): Float {
		if (width < defaultRadius) {
			return width.toFloat()
		} else {
			return defaultRadius
		}
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	}
}
