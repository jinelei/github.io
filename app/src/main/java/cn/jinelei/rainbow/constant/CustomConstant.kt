package cn.jinelei.rainbow.constant

import java.util.*

//  Binder请求码
const val BINDER_REQUEST_CODE_TEST = 0x01
const val BINDER_REQUEST_CODE_BLUETOOTH = 0x02

//  默认隐藏加载中弹窗的超时时间
const val DEFAULT_HIDE_LOADING_TIMEOUT = 10000L
const val DEFAULT_BLUETOOTH_SCAN_TIMEOUT = 20000L

//  权限请求码
const val REQUEST_CODE_OPEN_BT = 0x01

//  首选项
const val PRE_NAME_MINE = "PRE_NAME_MINE"
const val PRE_KEY_DEBUG = "PRE_KEY_DEBUG"
const val PRE_KEY_LANGUAGE = "PRE_KEY_LANGUAGE"
const val PRE_KEY_BT_DEVICE_MAC = "PRE_KEY_BT_DEVICE_MAC"
const val PRE_KEY_GRID_ITEM_SEQUENCE = "PRE_KEY_GRID_ITEM_SEQUENCE"

//  默认值
const val DEFAULT_MTU_SIZE = 20


const val WITH_ACK_TIMEOUT = 5 * 1000
const val WITHOUT_ACK_TIMEOUT = 2 * 1000
const val DEFAULT_SLEEP_TIME = 1000L
const val EXCEEDED_RETRIES = "exceeded retries"
/**
 * 返回给用户数据的回调接口
 */
const val PUBLISH_D2A_DATA = 1;
/**
 * 向蓝牙发送数据
 */
const val SEND_DATA = 2;
/**
 * 清除缓存
 */
const val CLEAR_RECEIVE_DATA = 3;
/**
 * 取消发送数据超时定时器
 */
const val CANCEL_SEND_TIMEOUT_TIMER = 4;
/**
 * 开启发送数据超时定时器
 */
const val START_SEND_TIMEOUT_TIMER = 5;

// 蓝牙连接状态
const val STATE_CONNECTING = 0
const val STATE_CONNECT_FAIL = 1

const val CONN_STATE_IDLE = 0
const val CONN_STATE_CONNECTING = 1
const val CONN_STATE_CONNECTED = 2
const val CONN_STATE_DISCONNECTING = 3
const val CONN_STATE_CLOSED = 4

/**
 * 蓝牙UUID
 */
//alertchar
val IMMIDATE_ALERT_CHAR_UUID = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb")
//计步char:读取该UUID下的value数组 第0 个数据就是 步数
val STEP_CHAR_UUID = UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb")
//电量信息
val BATTERY_CHAR_UUID = UUID.fromString("0000ff0c-0000-1000-8000-00805f9b34fb")
//用户信息char
val USER_INFO_CHAR_UUID = UUID.fromString("0000ff04-0000-1000-8000-00805f9b34fb")
//控制点char
val CONTROL_POINT_CHAR_UUID = UUID.fromString("0000ff05-0000-1000-8000-00805f9b34fb")

