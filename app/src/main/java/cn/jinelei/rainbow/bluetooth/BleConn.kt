package cn.jinelei.rainbow.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

class BleConn(val gatt: BluetoothGatt) {
	var device: BluetoothDevice = gatt.device
	var name: String = gatt.device.name
	var address: String = gatt.device.address
	
}