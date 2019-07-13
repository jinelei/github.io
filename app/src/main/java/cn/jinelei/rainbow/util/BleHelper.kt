package cn.jinelei.rainbow.util

import android.bluetooth.BluetoothGatt
import android.util.Log

const val TAG = "BleHelper"

fun discoveryAllUUIDs(gatt: BluetoothGatt) {
	for (gattService in gatt.services) {
		Log.d(TAG, "服务UUID ${gattService.uuid}")
		for (char in gattService.characteristics) {
			Log.d(TAG, "> 特征UUID ${char.uuid} value: ${char.value}")
			for (desc in char.descriptors) {
				Log.d(TAG, ">> 描述UUID ${desc.uuid} value: ${desc.value}")
			}
		}
	}
}