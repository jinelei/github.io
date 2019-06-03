package cn.jinelei.rainbow.message

class MessageEvent(val message: String) {
    class WifiScanMessageEvent(val finish: Boolean) {}
}