package cn.jinelei.rainbow.ui.base.components

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import cn.jinelei.rainbow.R

class LoadingDialog(context: Context) : AlertDialog(context) {

    init {
        this.setCancelable(false)
    }

    constructor(context: Context, message: String) : this(context) {
        this.setCancelable(false)
        this.setMessage(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.loading_layout)
    }

}