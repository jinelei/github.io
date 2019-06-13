package cn.jinelei.rainbow.bluetooth;

import cn.jinelei.rainbow.bluetooth.IConnectionCallback;

interface IBluetoothService {

    /**
     * Init IPCController.
     * @param cmd_type Only support SDK Controller CMD_8 or CMD_9
     * @param tagName Controller Tag
     */
    int init(int cmd_type, in String tagName);

    /**
     * Send bytes to device.
     * @param tagName Controller Tag, use your IPCController init tag.
     * @param cmd command string, like "yahooweather yahooweather 1 0 0 "
     * @param dataBuffer byte type of data, like "".getBytes()
     * @param priority default PRIORITY_NORMAL#0, if set as PRIORITY_HIGH#1, this session
     *        will get top priority to send.
     */
    long sendBytes(in String tagName, String cmd, in byte[] data, int priority);

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
    int getConnectionState();

    /**
     * Destroy the IPCController.
     */
    void close(String tagName);

    /**
     * register IControllerCallback for the "tagName" IPCController.
     */
    void registerConnectionCallback(in String tagName, in IConnectionCallback callback);

    /**
     * unregister IControllerCallback for the "tagName" IPCController.
     */
    void unregisterConnectionCallback(in String tagName, in IConnectionCallback callback);

    /**
     * get SmartDevice APK remote Bluetooth device Name.
     */
    String getRemoteDeviceName();
}
