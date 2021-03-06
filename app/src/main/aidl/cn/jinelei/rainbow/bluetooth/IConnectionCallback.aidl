package cn.jinelei.rainbow.bluetooth;

interface IConnectionCallback {

    /**
     * @see STATE_NONE#0
     * @see STATE_LISTEN#1
     * @see STATE_CONNECT_FAIL#2
     * @see STATE_CONNECT_LOST#3
     * @see STATE_CONNECTING#4
     * @see STATE_CONNECTED#5
     * @see STATE_DISCONNECTING#6
     */
    void onConnectionStateChange(in int newStatus, in int oldState);

    void onBytesReceived(in byte[] dataBuffer);
}
