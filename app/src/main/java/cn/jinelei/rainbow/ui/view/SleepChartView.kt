package cn.jinelei.rainbow.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.ui.fragment.SleepData
import cn.jinelei.rainbow.util.Point

class SleepChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
	private var defaultRadius: Float = 20F           // 默认圆角半径
	private var defaultLineHeight = 80F     // 默认数据显示段高度
	private var defaultLinkWidth = 5F      // 默认X偏移量
	private var defaultLinkHeight = 100F     // 默认Y偏移量
	private var defaultPaddingLeft = 10F        //默认左内边距
	private var defaultPaddingRight = 10F        //默认右内边距
	private var defaultPaddingTop = 10F        //默认上内边距
	private var defaultPaddingBottom = 10F        //默认下内边距
	private var defaultScaleX = 0F                // 自适应下，宽度缩放系数
	private var defaultTypeCount = 4            // 默认类型数量
	private var sleepDataList: MutableList<SleepData> = mutableListOf()
	
	init {
		context?.obtainStyledAttributes(attrs, R.styleable.SleepChartView).let {
			it?.let {
				defaultRadius = it.getFloat(R.styleable.SleepChartView_defaultRadius, defaultRadius)
				defaultLineHeight = it.getFloat(R.styleable.SleepChartView_defaultLineHeight, defaultLineHeight)
				defaultLinkWidth = it.getFloat(R.styleable.SleepChartView_defaultLinkWidth, defaultLinkWidth)
				defaultLinkHeight = it.getFloat(R.styleable.SleepChartView_defaultLinkHeight, defaultLinkHeight)
				defaultTypeCount = it.getInt(R.styleable.SleepChartView_defaultTypeCount, defaultTypeCount)
				defaultPaddingTop = it.getFloat(R.styleable.SleepChartView_padding, defaultPaddingTop)
				defaultPaddingTop = it.getFloat(R.styleable.SleepChartView_paddingVertical, defaultPaddingTop)
				defaultPaddingTop = it.getFloat(R.styleable.SleepChartView_paddingTop, defaultPaddingTop)
				defaultPaddingBottom = it.getFloat(R.styleable.SleepChartView_padding, defaultPaddingBottom)
				defaultPaddingBottom = it.getFloat(R.styleable.SleepChartView_paddingVertical, defaultPaddingBottom)
				defaultPaddingBottom = it.getFloat(R.styleable.SleepChartView_paddingBottom, defaultPaddingBottom)
				defaultPaddingLeft = it.getFloat(R.styleable.SleepChartView_padding, defaultPaddingLeft)
				defaultPaddingLeft = it.getFloat(R.styleable.SleepChartView_paddingHorizontal, defaultPaddingLeft)
				defaultPaddingLeft = it.getFloat(R.styleable.SleepChartView_paddingLeft, defaultPaddingLeft)
				defaultPaddingRight = it.getFloat(R.styleable.SleepChartView_padding, defaultPaddingRight)
				defaultPaddingRight = it.getFloat(R.styleable.SleepChartView_paddingHorizontal, defaultPaddingRight)
				defaultPaddingRight = it.getFloat(R.styleable.SleepChartView_paddingRight, defaultPaddingRight)
				it.recycle();//释放资源
			}
		}
	}
	
	fun updateDataAndRefresh(newSleepData: List<SleepData>) {
		sleepDataList.clear()
		sleepDataList.addAll(newSleepData)
		this.invalidate()
	}
	
	fun mArcTo(path: Path, p1: Point, p2: Point, type: Int): Point {
		Log.v(SleepChartView::class.java.simpleName, "mArcTo: $p1 $p2 type: $type")
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
	
	fun mLineTo(path: Path, pos: Point): Point {
		Log.v(SleepChartView::class.java.simpleName, "mLineTo: $pos")
		path.lineTo(pos.x, pos.y)
		return pos
	}
	
	fun drawGrid(canvas: Canvas) {
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
	}
	
	private fun initDefaultParameters() {
		// 容器总宽度
		val containerTotalWidth = width
		// 容器总高度
		val containerTotalHeight = height
		if (containerTotalHeight != 0 && containerTotalWidth != 0) {
			// 内容总宽度
			val contentTotalWidth = sleepDataList.map { it.width.toFloat() }.reduce { acc, fl -> acc + fl }
			// 内容总高度
			val contentTotalHeight = defaultTypeCount * (defaultLinkHeight + defaultRadius)
			defaultScaleX =
				(containerTotalWidth - defaultPaddingLeft - defaultPaddingRight - defaultLinkWidth * (sleepDataList.size - 1) - (defaultRadius * 2) * sleepDataList.size) / contentTotalWidth
			defaultLineHeight =
				(containerTotalHeight - defaultPaddingTop - defaultPaddingBottom - contentTotalHeight) / defaultTypeCount + defaultRadius
			Log.v(
				SleepChartView::class.java.simpleName,
				"defaultRadius $defaultRadius defaultLineHeight $defaultLineHeight defaultLinkWidth $defaultLinkWidth defaultLinkHeight $defaultLinkHeight defaultPaddingLeft $defaultPaddingLeft defaultPaddingRight $defaultPaddingRight defaultPaddingTop $defaultPaddingTop defaultPaddingBottom $defaultPaddingBottom defaultScaleX $defaultScaleX"
			)
		}
	}
	
	private fun drawSleepData(canvas: Canvas){
		canvas.drawPath(Path().apply {
			var lastPoint = Point(0F, 0F)
			var firstPoint = Point(0F, 0F)
			var lastSleepData: SleepData? = null
			var currentSleepData: SleepData? = null
			var tmpPoint: Point = Point(0F, 0F)
			// 绘制下半部曲线
			for (index in 0 until sleepDataList.size) {
				currentSleepData = sleepDataList[index]
				Log.v(SleepChartView::class.java.simpleName, "index: $index, $currentSleepData")
				// 绘制开始左半部
				if (index == 0) {
					// 绘制左上角半圆角
					firstPoint =
						Point(
							defaultPaddingLeft + defaultRadius,
							defaultPaddingTop + (defaultRadius * 2 + defaultLinkHeight + defaultLineHeight) * currentSleepData.type
						)
					Log.v(SleepChartView::class.java.simpleName, "first: $firstPoint")
					moveTo(firstPoint.x, firstPoint.y)
					lastPoint =
						Point(firstPoint.x - defaultRadius, firstPoint.y + defaultRadius)
					lastPoint = mArcTo(this, firstPoint, lastPoint, 4)
					// 绘制左侧数据区高度
					lastPoint = Point(lastPoint.x, lastPoint.y + defaultLineHeight)
					lastPoint = mLineTo(this, lastPoint)
					// 绘制左下角半圆角
					tmpPoint = Point(lastPoint.x + defaultRadius, lastPoint.y + defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 3)
				}
				
				// 绘制中部
				if (lastSleepData == null) {
					lastSleepData = currentSleepData
					continue
				}
				val changedLine = currentSleepData.type - lastSleepData.type
				if (changedLine > 0) { // 顺X轴，下降
					// 绘制横线，缩短一个defaultLinkWidth
					lastPoint =
						Point(
							lastPoint.x + lastSleepData.width * defaultScaleX - defaultLinkWidth,
							lastPoint.y
						)
					lastPoint = mLineTo(this, lastPoint)
					// 左侧
					tmpPoint = Point(
						lastPoint.x + defaultRadius,
						lastPoint.y + defaultRadius
					)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 1)
					// 绘制连接线，根据changedLine
					tmpPoint = Point(
						lastPoint.x,
						lastPoint.y + (defaultLinkHeight + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPoint.y = tmpPoint.y + (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPoint = mLineTo(this, tmpPoint)
					// 右侧
					tmpPoint =
						Point(lastPoint.x + defaultRadius, lastPoint.y + defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 3)
				} else { // 顺X轴，上升
					// 绘制横线，扩大一个defaultLinkWidth
					lastPoint =
						Point(
							lastPoint.x + lastSleepData.width * defaultScaleX + defaultLinkWidth,
							lastPoint.y
						)
					lastPoint = mLineTo(this, lastPoint)
					// 左侧
					tmpPoint = Point(
						lastPoint.x + defaultRadius,
						lastPoint.y - defaultRadius
					)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 2)
					// 绘制连接线，根据changedLine
					tmpPoint = Point(
						lastPoint.x,
						lastPoint.y + (defaultLinkHeight + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPoint.y = tmpPoint.y - (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPoint = mLineTo(this, tmpPoint)
					// 右侧
					tmpPoint =
						Point(lastPoint.x + defaultRadius, lastPoint.y - defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 4)
				}

				// 绘制结束右半部
				if (index == sleepDataList.size - 1) { // 最后一个
					// 绘制底部横线
					lastPoint = mLineTo(
						this,
						Point(lastPoint.x + currentSleepData.width * defaultScaleX, lastPoint.y)
					)
					// 绘制右下角半圆角
					tmpPoint = Point(lastPoint.x + defaultRadius, lastPoint.y - defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 2)
					// 绘制左侧数据区高度
					lastPoint = Point(lastPoint.x, lastPoint.y - defaultLineHeight)
					lastPoint = mLineTo(this, lastPoint)
					// 绘制右上角半圆角
					tmpPoint = Point(lastPoint.x - defaultRadius, lastPoint.y - defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 1)
				}
				lastSleepData = currentSleepData
			}
			
			// 绘制上半部曲线
			for (index in 1 until sleepDataList.size) {
				var tmpIdx = sleepDataList.size - 1 - index
				currentSleepData = sleepDataList[tmpIdx]
				Log.v(SleepChartView::class.java.simpleName, "reverse index: $tmpIdx, $currentSleepData")
				if (lastSleepData == null) return
				val changedLine = lastSleepData.type - currentSleepData.type
				if (changedLine > 0) { // 逆着X轴，上升
					// 绘制横线
					lastPoint =
						Point(
							lastPoint.x - lastSleepData.width * defaultScaleX + defaultLinkWidth,
							lastPoint.y
						)
					lastPoint = mLineTo(this, lastPoint)
					// 绘制连接线右侧半圆
					tmpPoint =
						Point(lastPoint.x - defaultRadius, lastPoint.y - defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 3)
					// 绘制连接线，根据changedLine
					tmpPoint = Point(
						lastPoint.x,
						lastPoint.y - (defaultLinkHeight + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPoint.y = tmpPoint.y - (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPoint = mLineTo(this, tmpPoint)
					// 绘制连接线左侧半圆
					tmpPoint = Point(lastPoint.x - defaultRadius, lastPoint.y - defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 1)
				} else {// 逆着X轴， 下降
					// 绘制横线
					lastPoint =
						Point(
							lastPoint.x - lastSleepData.width * defaultScaleX - defaultLinkWidth,
							lastPoint.y
						)
					lastPoint = mLineTo(this, lastPoint)
					// 绘制连接线右侧半圆
					tmpPoint =
						Point(lastPoint.x - defaultRadius, lastPoint.y + defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 4)
					// 绘制连接线，根据changedLine
					tmpPoint = Point(
						lastPoint.x,
						lastPoint.y - (defaultLinkHeight + defaultLineHeight) * changedLine
					)
					if (Math.abs(changedLine) > 1) {
						tmpPoint.y = tmpPoint.y + (defaultRadius * 2) * (Math.abs(changedLine) - 1)
					}
					lastPoint = mLineTo(this, tmpPoint)
					// 绘制连接线左侧半圆
					tmpPoint = Point(lastPoint.x - defaultRadius, lastPoint.y + defaultRadius)
					lastPoint = mArcTo(this, lastPoint, tmpPoint, 2)
				}
				lastSleepData = currentSleepData
			}
			// 连回起点
			mLineTo(this, firstPoint)
			close()
		}, Paint().apply {
			color = Color.RED
			shader = LinearGradient(
				0f,
				0f,
				0f,
				height.toFloat(),
				Color.parseColor("#febf22"),
				Color.parseColor("#9127ed"),
				Shader.TileMode.CLAMP
			)
			xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
			style = Paint.Style.FILL
		})
	}
	
	override fun onDraw(canvas: Canvas?) {
		if (canvas == null)
			return
		initDefaultParameters()
		drawSleepData(canvas)
	}
	
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}
	
	private fun measureWidth(measureSpec: Int): Int {
		var specMode = MeasureSpec.getMode(measureSpec);
		var specSize = MeasureSpec.getSize(measureSpec);
		var result = 500;
		if (specMode == MeasureSpec.AT_MOST) {//相当于我们设置为wrap_content
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {//相当于我们设置为match_parent或者为一个具体的值
			result = specSize;
		}
		return result;
	}
	
	private fun measureHeight(measureSpec: Int): Int {
		var specMode = MeasureSpec.getMode(measureSpec);
		var specSize = MeasureSpec.getSize(measureSpec);
		var result = 500;
		if (specMode == MeasureSpec.AT_MOST) {
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		return result;
	}
	
}

