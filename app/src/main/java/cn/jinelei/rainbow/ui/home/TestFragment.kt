package cn.jinelei.rainbow.ui.home


import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseFragment
import cn.jinelei.rainbow.ui.base.components.SleepChartView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlin.random.Random

class TestFragment : BaseFragment() {
	private lateinit var dvTest: SleepChartView
	private lateinit var bcTest: BarChart
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_test, container, false)
		dvTest = view.findViewById(R.id.dv_test)
		bcTest = view.findViewById(R.id.bar_chart)
		initBarData()
		view.findViewById<FloatingActionButton>(R.id.fab_refresh).apply {
			setOnClickListener {
				Log.v(TestFragment::class.java.simpleName, "refresh")
				dvTest.updateDataAndRefresh(randomSleepData(Random.nextInt(10, 20)))
			}
		}
		dvTest.updateDataAndRefresh(randomSleepData(Random.nextInt(10, 20)))
		return view
	}
	
	fun initBarData() {
		var yvalue = ArrayList<BarEntry>()
		//敲黑板啦！！这里才是重点部分，可以添加一个float数组，让它变成StackBar
		yvalue.add(BarEntry(0F, floatArrayOf(10f, 20f)))
		yvalue.add(BarEntry(1F, floatArrayOf(20f, 30f)))
		yvalue.add(BarEntry(2F, floatArrayOf(30f, 40f)))
		var set = BarDataSet(yvalue, "")
		set.setColors(Color.RED, Color.GRAY)
		var xvalue = ArrayList<String>()
		xvalue.add("第一季度")
		xvalue.add("第二季度")
		xvalue.add("第三季度")
		xvalue.add("第四季度")
		bcTest.data = BarData(set)
	}
	
	private fun initSleepData(): List<SleepData> {
		var sleepDataList = mutableListOf<SleepData>()
		sleepDataList.add(SleepData(100, 0))
		sleepDataList.add(SleepData(100, 1))
		sleepDataList.add(SleepData(100, 2))
		sleepDataList.add(SleepData(100, 1))
		sleepDataList.add(SleepData(100, 0))
		sleepDataList.add(SleepData(100, 2))
		return sleepDataList.toList()
	}
	
	private fun randomSleepData(count: Int = 10): List<SleepData> {
		var lastType = Random.nextInt(0, 3)
		var tmpType = Random.nextInt(0, 3)
		var lastWidth = 10 * Random.nextInt(0, 10)
		var tmpWidth = 10 * Random.nextInt(0, 10)
		var sleepDataList = mutableListOf<SleepData>()
		while (sleepDataList.size < count) {
			while (tmpType == lastType)
				tmpType = Random.nextInt(0, 3)
			while (tmpWidth == lastWidth)
				tmpWidth = 10 * Random.nextInt(0, 10)
			sleepDataList.add(SleepData(tmpWidth, tmpType))
			lastType = tmpType
			lastWidth = tmpWidth
		}
		return sleepDataList.toList()
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}
	
}

class SleepData(val width: Int, val type: Int) {
	override fun toString(): String {
		return "SleepData(width=$width, type=$type)"
	}
}

