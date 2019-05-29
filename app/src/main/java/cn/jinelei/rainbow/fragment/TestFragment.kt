package cn.jinelei.rainbow.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.jinelei.rainbow.R
import cn.jinelei.rainbow.custom.ColorPickerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TestFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        var colorPickerView: ColorPickerView = ColorPickerView(activity?.applicationContext!!)
//        colorPickerView.listener = object :ColorPickerView.OnColorChangedListener{
//            override fun onColorChanged(r: Int, g: Int, b: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onMoveColor(r: Int, g: Int, b: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//        }
        return inflater.inflate(R.layout.fragment_test, container, false)
    }


}
