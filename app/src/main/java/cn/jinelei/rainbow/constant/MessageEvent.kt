package cn.jinelei.rainbow.constant

sealed class MessageEvent {}

data class WifiScanMessageEvent(val finish: Boolean) : MessageEvent()