package cn.jinelei.rainbow.bluetooth

import android.bluetooth.*
import android.content.Context
import cn.jinelei.rainbow.constant.DEFAULT_MTU_SIZE
import java.util.*

class JinBluetoothHelper(val context: Context) {
	private var mBluetoothManager: BluetoothManager =
		context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
	private var mBluetoothAdapter: BluetoothAdapter = mBluetoothManager.adapter
	private var mBluetoothDevice: BluetoothDevice? = null
	private var mConnectAddress: String? = null
	private var mGattCallback: BluetoothGattCallback? = null
	private var state: BtConnectState = BtConnectState.NONE
	
	private var RxChar: BluetoothGattCharacteristic? = null     // 特征值
	private var mtuSize = DEFAULT_MTU_SIZE    //  默认Mtu大小
	private var RxService: BluetoothGattService? = null            //GattService
	private var serviceUuid: UUID? = null                          //蓝牙UUID
	
	
	private fun connectDevice(address: String, listener: List<JinConnectCallback>) {
		if (mBluetoothManager == null || mBluetoothAdapter == null || address == null)
			return
	}
	
}

enum class BtConnectState(val state: String) {
	NONE("NONE"),
	CONNECTED("CONNECTED"),
	CONNECTING("CONNECTING"),
	DISCONNECTED("DISCONNECTED"),
}

interface JinConnectCallback {
	fun onConnected()
	fun onDisconnected()
}