package cn.jinelei.rainbow.ui.fragment


import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.base.BaseFragment
import cn.jinelei.rainbow.ui.view.SleepChartView
import kotlin.random.Random

class TestFragment : BaseFragment() {
	lateinit var dvTest: SleepChartView
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_test, container, false)
		dvTest = view.findViewById(R.id.dv_test)
		view.findViewById<FloatingActionButton>(R.id.fab_refresh).apply {
			setOnClickListener {
				Log.v(TestFragment::class.java.simpleName, "refresh")
				dvTest.updateDataAndRefresh(randomSleepData(Random.nextInt(10, 20)))
			}
		}
		dvTest.updateDataAndRefresh(randomSleepData(Random.nextInt(10, 20)))
		return view
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

