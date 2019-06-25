package cn.jinelei.rainbow.ui.fragment


import android.content.Context
import android.content.res.TypedArray
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
		dvTest = view.findViewById<DrawView>(R.id.dv_test)
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
		sleepDataList.clear()
		while (--count > 0) {
			var tmp = Random.nextInt(0, 4)
			if (lastType != tmp) {
				sleepDataList.add(SleepData(10 * Random.nextInt(3, 10), tmp))
				lastType = tmp
			}
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
	private val DEF_LINE_HEIGHT = 80F     // 默认Y轴高度
	private val DEF_CHANGE_INTERVAL = 60F  // 贝塞尔曲线变化区间
	private val DEF_CHANGE_HEIGHT = 60F  // 贝塞尔曲线变化高度，和贝塞尔曲线变化区间共同构成圆角弧度
	private val DEF_START_X = 100F
	private val DEF_START_Y = 100F
	private val DEF_LINK_OFFSET_X = 5F      // 默认X偏移量
	private val DEF_LINK_OFFSET_Y = 100F     // 默认Y偏移量
	
	val FIRST_TO_DOWN = 0  // 第一个向下一层
	val DOWN_TO_UP = 1
	val UP_TO_UP = 2
	val UP_TO_DOWN = 3
	val DOWN_TO_DOWN = 4
	private var lastPos: MPoint? = null
	
	init {
		//初始化的时候获取自定义的属性，获取的是主布局中自己赋给自定义属性的值，第二个参数是如果你没有赋值，就是用默认值。
		context?.obtainStyledAttributes(attrs, R.styleable.DrawView)?.let {
			it?.let {
				defaultRadius = it.getFloat(R.styleable.DrawView_defaultRadius, defaultRadius)
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
	
	fun mMoveTo(path: Path, pos: MPoint) {
		Log.v(TestFragment::class.java.simpleName, "mMoveTo: $pos")
		path.moveTo(pos.x, pos.y)
		lastPos = pos
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
			var lastPos =
				MPoint(DEF_START_X, DEF_START_Y + (DEF_LINE_HEIGHT + DEF_LINK_OFFSET_Y) * sleepDataList[0].type)
			var firstPos =
				MPoint(DEF_START_X, DEF_START_Y + (DEF_LINE_HEIGHT + DEF_LINK_OFFSET_Y) * sleepDataList[0].type)
			var lastSleepData: SleepData? = null
			for (index in 0 until sleepDataList.size) {
				var sleepData = sleepDataList[index]
				Log.v(TestFragment::class.java.simpleName, "index: $index, $sleepData")
				var tmpPos = MPoint(0F, 0F)
				if (index == 0) {
					// 绘制左上角半圆角
					tmpPos = MPoint(lastPos.x + calcRadius(sleepData.width), lastPos.y - calcRadius(sleepData.width))
					firstPos = tmpPos
					moveTo(firstPos.x, firstPos.y)
					lastPos = mArcTo(this, tmpPos, lastPos, 4)
					// 绘制左侧数据区高度
					lastPos = MPoint(lastPos.x, lastPos.y + DEF_LINE_HEIGHT)
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
						lastPos.x + DEF_LINK_OFFSET_X,
						lastPos.y + (DEF_LINK_OFFSET_Y + DEF_LINE_HEIGHT) * changedLine
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
						lastPos.x + DEF_LINK_OFFSET_X,
						lastPos.y + (DEF_LINK_OFFSET_Y + DEF_LINE_HEIGHT) * changedLine
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
					lastPos = MPoint(lastPos.x, lastPos.y - DEF_LINE_HEIGHT)
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
				var tmpPos: MPoint = MPoint(DEF_START_X, DEF_START_Y)
				if (changedLine > 0) { // 逆着X轴，上升
					// 绘制连接线右侧半圆
					tmpPos =
						MPoint(lastPos.x - calcRadius(lastSleepData.width), lastPos.y - calcRadius(lastSleepData.width))
					lastPos = mArcTo(this, lastPos, tmpPos, 3)
					// 绘制连接线，根据changedLine
					tmpPos = MPoint(
						lastPos.x - DEF_LINK_OFFSET_X,
						lastPos.y - (DEF_LINK_OFFSET_Y + DEF_LINE_HEIGHT) * changedLine
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
						lastPos.x - DEF_LINK_OFFSET_X,
						lastPos.y - (DEF_LINK_OFFSET_Y + DEF_LINE_HEIGHT) * changedLine
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

//	fun mArcTo(path: Path, pos: MPoint) {
//		path.addArc()
//	}

//	fun mCubicTo(path: Path, controlPos1: MPoint, controlPos2: MPoint, endPos: MPoint): MPoint {
//		Log.v(
//			TestFragment::class.java.simpleName,
//			"mCubicTo: controlPos1: $controlPos1 controlPos2: $controlPos2 endPos: $endPos"
//		)
//		path.cubicTo(controlPos1.x, controlPos1.y, controlPos2.x, controlPos2.y, endPos.x, endPos.y)
//		return endPos
//	}
	
	/**
	 * drawCubic1(this, lastPos, width, FIRST_TO_DOWN)
	
	lastPos.x = lastPos.x + width
	lastPos.y = lastPos.y + DEF_LINE_HEIGHT * 2 + DEF_CHANGE_HEIGHT * 2 + DEF_LINK_HEIGHT
	moveTo(lastPos.x, lastPos.y - DEF_LINE_HEIGHT)
	lineTo(lastPos.x, lastPos.y - DEF_LINK_HEIGHT - DEF_LINK_HEIGHT - DEF_CHANGE_HEIGHT * 2)
	width = Random.nextInt(10, 100)
	drawCubic1(this, lastPos, width, DOWN_TO_UP)
	
	lastPos.x = lastPos.x + width
	lastPos.y = lastPos.y - DEF_LINE_HEIGHT * 2 - DEF_CHANGE_HEIGHT * 2 - DEF_LINK_HEIGHT
	moveTo(lastPos.x, lastPos.y + DEF_LINE_HEIGHT)
	lineTo(lastPos.x, lastPos.y + DEF_LINE_HEIGHT + DEF_LINK_HEIGHT + DEF_CHANGE_HEIGHT * 2)
	width = Random.nextInt(10, 100)
	drawCubic1(this, lastPos, width, UP_TO_UP)
	
	lastPos.x = lastPos.x + width
	lastPos.y = lastPos.y - DEF_LINE_HEIGHT * 2 - DEF_CHANGE_HEIGHT * 2 - DEF_LINK_HEIGHT
	moveTo(lastPos.x, lastPos.y + DEF_LINE_HEIGHT)
	lineTo(lastPos.x, lastPos.y + DEF_LINE_HEIGHT + DEF_LINK_HEIGHT + DEF_CHANGE_HEIGHT * 2)
	width = Random.nextInt(10, 100)
	drawCubic1(this, lastPos, width, UP_TO_DOWN)
	
	lastPos.x = lastPos.x + width
	lastPos.y = lastPos.y + DEF_LINE_HEIGHT * 2 + DEF_CHANGE_HEIGHT * 2 + DEF_LINK_HEIGHT
	moveTo(lastPos.x, lastPos.y - DEF_LINE_HEIGHT)
	lineTo(lastPos.x, lastPos.y - DEF_LINE_HEIGHT - DEF_LINK_HEIGHT - DEF_CHANGE_HEIGHT * 2)
	width = Random.nextInt(10, 100)
	drawCubic1(this, lastPos, width, DOWN_TO_DOWN)
	
	lastPos.x = lastPos.x + width
	lastPos.y = lastPos.y + DEF_LINE_HEIGHT * 2 + DEF_CHANGE_HEIGHT * 2 + DEF_LINK_HEIGHT
	moveTo(lastPos.x, lastPos.y - DEF_LINE_HEIGHT)
	lineTo(lastPos.x, lastPos.y - DEF_LINK_HEIGHT - DEF_LINK_HEIGHT - DEF_CHANGE_HEIGHT * 2)
	width = Random.nextInt(10, 100)
	drawCubic1(this, lastPos, width, DOWN_TO_UP)
	
	lastPos.x = lastPos.x + width
	lastPos.y =
	lastPos.y - (DEF_LINE_HEIGHT * 2 + DEF_CHANGE_HEIGHT * 2 + DEF_LINK_HEIGHT) * 2
	moveTo(lastPos.x, lastPos.y + DEF_LINE_HEIGHT)
	lineTo(
	lastPos.x,
	lastPos.y + (DEF_LINE_HEIGHT + DEF_LINK_HEIGHT + DEF_CHANGE_HEIGHT * 2) * 2
	)
	width = Random.nextInt(10, 100)
	drawCubic1(this, lastPos, width, UP_TO_DOWN)
	 */
	
	/**
	 * startX: 开始X坐标，取中点
	 * startY：开始Y坐标，取中点
	 */
	fun drawCubic1(path: Path?, pos: MPoint, width: Int, type: Int) {
		path?.let {
			var stopY: Float?
			var controlPointOneX: Float?
			var controlPointOneY: Float?
			var controlPointTwoX: Float?
			var controlPointTwoY: Float?
			when (type) {
				FIRST_TO_DOWN -> {
					// 左侧竖线
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					// 上部曲线
					controlPointOneX = pos.x
					controlPointOneY = pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_INTERVAL
					controlPointTwoX = pos.x + width
					controlPointTwoY = pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_INTERVAL
					stopY = pos.y - DEF_LINE_HEIGHT
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						stopY
					)
					// 下部曲线
					controlPointOneY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_INTERVAL
					controlPointTwoY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT - DEF_CHANGE_INTERVAL
					stopY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT
					it.moveTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						stopY
					)
					// 右侧竖线 + 连接线
					it.moveTo(pos.x + width, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x + width, pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT)
				}
				DOWN_TO_UP -> {
					// 左侧竖线
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					// 上部曲线
					controlPointOneX = pos.x
					controlPointOneY = pos.y - DEF_LINE_HEIGHT + DEF_CHANGE_INTERVAL
					controlPointTwoX = pos.x + width
					controlPointTwoY = pos.y - DEF_LINE_HEIGHT + DEF_CHANGE_INTERVAL
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y - DEF_LINE_HEIGHT
					)
					// 下部曲线
					controlPointOneY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_INTERVAL
					controlPointTwoY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_INTERVAL
					it.moveTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y + DEF_LINE_HEIGHT
					)
					// 右侧竖线
					it.moveTo(pos.x + width, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x + width, pos.y + DEF_LINE_HEIGHT)
				}
				UP_TO_UP -> {
					// 左侧竖线
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x, pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT)
					// 上部曲线
					controlPointOneX = pos.x
					controlPointOneY = pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_INTERVAL
					controlPointTwoX = pos.x + width
					controlPointTwoY = pos.y - DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT - DEF_CHANGE_INTERVAL
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_HEIGHT
					)
					// 下部曲线
					controlPointOneY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT - DEF_CHANGE_INTERVAL
					controlPointTwoY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT
					it.moveTo(pos.x, pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y + DEF_LINE_HEIGHT
					)
					// 右侧竖线 + 连接线
					it.moveTo(pos.x + width, pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_HEIGHT)
					it.lineTo(pos.x + width, pos.y + DEF_LINE_HEIGHT)
				}
				UP_TO_DOWN -> {
					// 左侧竖线
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					// 上部曲线
					controlPointOneX = pos.x
					controlPointOneY = pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_INTERVAL
					controlPointTwoX = pos.x + width
					controlPointTwoY = pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_INTERVAL
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y - DEF_LINE_HEIGHT
					)
					// 下部曲线
					controlPointOneY = pos.y + DEF_LINE_HEIGHT - DEF_CHANGE_INTERVAL
					controlPointTwoY = pos.y + DEF_LINE_HEIGHT - DEF_CHANGE_INTERVAL
					it.moveTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y + DEF_LINE_HEIGHT
					)
					// 右侧竖线
					it.moveTo(pos.x + width, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x + width, pos.y + DEF_LINE_HEIGHT)
				}
				DOWN_TO_DOWN -> {
					// 左侧竖线
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_HEIGHT)
					it.lineTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					// 上部曲线
					controlPointOneX = pos.x
					controlPointOneY = pos.y - DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT - DEF_CHANGE_INTERVAL
					controlPointTwoX = pos.x + width
					controlPointTwoY = pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_HEIGHT
					it.moveTo(pos.x, pos.y - DEF_LINE_HEIGHT - DEF_CHANGE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y - DEF_LINE_HEIGHT
					)
					// 下部曲线
					controlPointOneY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT
					controlPointTwoY = pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT - DEF_CHANGE_INTERVAL
					it.moveTo(pos.x, pos.y + DEF_LINE_HEIGHT)
					it.cubicTo(
						controlPointOneX,
						controlPointOneY,
						controlPointTwoX,
						controlPointTwoY,
						pos.x + width,
						pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT
					)
					// 右侧竖线 + 连接线
					it.moveTo(pos.x + width, pos.y - DEF_LINE_HEIGHT)
					it.lineTo(pos.x + width, pos.y + DEF_LINE_HEIGHT + DEF_CHANGE_HEIGHT)
				}
			}
		}
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
	}
}


//	fun drawLine(canvas: Canvas) {
//		var paint = Paint().apply {
//			color = Color.RED
//			style = Paint.Style.STROKE;
//			strokeWidth = 4F
//		}
//		var path = Path().apply {
//			var lastPos = MPoint(DEF_START_X, DEF_START_Y)
//			var path = Path()
//			var lastSleepData: SleepData? = null
//			for ((index, sleepData) in sleepDataList.withIndex()) {
//				Log.v(TestFragment::class.java.simpleName, "index: $index, $sleepData")
//				if (index == 0) {
//
//					lastPos = MPoint(
//						DEF_START_X,
//						DEF_START_Y + (DEF_LINE_HEIGHT + DEF_LINK_HEIGHT) * (sleepData.type + 1)
//					)
//					when (sleepData.type) {
//						0 -> {
//							lastPos = mLineTo(path, mMoveTo(path, lastPos).apply { y += DEF_LINE_HEIGHT })
//						}
//						1 -> {
//							lastPos = mLineTo(
//								path,
//								mMoveTo(
//									path,
//									lastPos.apply {
//										y = y + DEF_LINE_HEIGHT + DEF_LINE_HEIGHT
//									}).apply { y += (DEF_LINE_HEIGHT + DEF_LINE_HEIGHT) + DEF_LINE_HEIGHT })
//						}
//						2 -> {
//							lastPos = mLineTo(
//								path,
//								mMoveTo(
//									path,
//									lastPos.apply {
//										y = y + (DEF_LINE_HEIGHT + DEF_LINK_HEIGHT) * 2
//									}).apply { y += (DEF_LINE_HEIGHT + DEF_LINE_HEIGHT) * 2 + DEF_LINE_HEIGHT })
//						}
//						3 -> {
//							lastPos = mLineTo(
//								path,
//								mMoveTo(
//									path,
//									lastPos.apply {
//										y = y + (DEF_LINE_HEIGHT + DEF_LINK_HEIGHT) * 3
//									}).apply { y += (DEF_LINE_HEIGHT + DEF_LINE_HEIGHT) * 3 + DEF_LINE_HEIGHT })
//						}
//					}
//					lastSleepData = sleepData
//				} else {
//					val changedLine = sleepData.type - lastSleepData!!.type
//					// 默认向下
//					var controlPos1 = MPoint(lastPos.x, lastPos.y + DEF_CHANGE_INTERVAL)
//					var controlPos2 = MPoint(
//						lastPos.x + lastSleepData.width,
//						lastPos.y + (DEF_CHANGE_HEIGHT) - DEF_CHANGE_INTERVAL
//					)
//					var endPos = MPoint(lastPos.x + lastSleepData.width, lastPos.y + DEF_CHANGE_HEIGHT)
//					// 如果向上，更新坐标
//					if (changedLine < 0) {
//						controlPos2 = MPoint(lastPos.x + width, lastPos.y + DEF_CHANGE_INTERVAL)
//						endPos = MPoint(lastPos.x + lastSleepData.width, lastPos.y)
//					}
//					lastPos = mCubicTo(path, controlPos1, controlPos2, endPos)
//					lastPos = mLineTo(
//						path,
//						lastPos.apply { y += (DEF_LINK_HEIGHT + DEF_LINE_HEIGHT) * (changedLine + 1) })
//					lastSleepData = sleepData
//				}
//			}
//			path.close()
//		}
//		canvas.drawPath(path, paint)
//	}