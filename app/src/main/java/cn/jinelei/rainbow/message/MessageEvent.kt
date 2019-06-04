package cn.jinelei.rainbow.message

sealed class MessageEvent {}

data class WifiScanMessageEvent(val finish: Boolean) : MessageEvent()