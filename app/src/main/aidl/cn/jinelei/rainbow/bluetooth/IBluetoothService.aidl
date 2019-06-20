package cn.jinelei.rainbow.bluetooth;

import cn.jinelei.rainbow.bluetooth.IConnectionCallback;
import android.bluetooth.BluetoothDevice;

interface IBluetoothService {

    long sendBytes(in BluetoothDevice device, in byte[] data, int priority, boolean isAck);

    /**
     * Return connection state.
     *
     * @see STATE_NONE#0
     * @see STATE_LISTEN#1
     * @see STATE_CONNECT_FAIL#2
     * @see STATE_CONNECT_LOST#3
     * @see STATE_CONNECTING#4
     * @see STATE_CONNECTED#5
     * @see STATE_DISCONNECTING#6
     */
    int getConnectionState(in BluetoothDevice device);

    void disconnect(in BluetoothDevice device);

    void connect(in BluetoothDevice device);

    void registerConnectionCallback(in BluetoothDevice device, in IConnectionCallback callback);

    void unregisterConnectionCallback(in BluetoothDevice device, in IConnectionCallback callback);

}
