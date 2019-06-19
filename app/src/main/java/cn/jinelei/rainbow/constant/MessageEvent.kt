package cn.jinelei.rainbow.constant

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult

sealed class MessageEvent {}

data class WifiScanMessageEvent(val finish: Boolean) : MessageEvent()

data class BtScanStatus(val status: Boolean = false) : MessageEvent()
data class BtScanResult(val device: BluetoothDevice) : MessageEvent()
data class BtBatchScanResults(val results: MutableList<ScanResult>? = null) : MessageEvent()