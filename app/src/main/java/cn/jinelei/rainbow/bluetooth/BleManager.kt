package cn.jinelei.rainbow.bluetooth

import android.bluetooth.BluetoothGatt

class BleManager {
	val allBleConn = mutableListOf<BleConn>()
	
	/**
	 * 添加连接
	 */
	fun addBleConn(gatt: BluetoothGatt) {
		val bleConn = BleConn(gatt)
		if (!allBleConn.contains(bleConn))
			allBleConn.add(bleConn)
	}
	
	/**
	 * 断开所有连接
	 */
	fun disconnectAll() {
		for (bleConn in allBleConn) {
			bleConn.gatt.disconnect()
		}
		allBleConn.clear()
	}
	
	companion object {
		val instant = BleManager()
	}
}