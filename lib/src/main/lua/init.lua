--
-- Created by IntelliJ IDEA.
-- User: user
-- Date: 2019/5/31
-- Time: 14:03
-- To change this template use File | Settings | File Templates.
--

--all constants variable
localConfFileName = "conf.json"
keyConnectedWifi = "connected_wifi"
localConfFile = nil
connectedInfo = {}
socketServer = nil
socketConn = nil
apSetup = false

--1. check exist localConfFile

-- check conf file exist and set localConfFile variable
function checkConfigFileExist()
    allFiles = file.list()
    if allFiles and allFiles[localConfFileName] then
        localConfFile = allFiles[localConfFileName]
        return true
    end
    return false
end

-- main interaction logic
function onReceive(sck, data)
    print("onReceive data: ", data)
end

-- build socket server
function setupSocketServer()
    print("buildSocketServer")
    socketServer = net.createServer(net.TCP, 30)
    socketServer:listen(80, function(conn)
        socketConn = conn
        conn:on("receive", onReceive)
    end)
end

-- setup init wifi ap
function setupWifiAP()
    wifi.setmode(wifi.SOFTAP)
    --    wifi.setmode(wifi.STATIONAP)
    wifi.sta.autoconnect(0)
    wifi.sta.clearconfig()
    local initConf = {}
    initConf.ssid = "nodemcu"
    initConf.pwd = "12341234"
    initConf.save = true
    initConf.staconnected_cb = function(T)
        print("staconnected_cb")
    end
    initConf.stadisconnected_cb = function(T)
        print("stadisconnected_cb")
    end
    initConf.probereq_cb = function(T)
        if not apSetup then
            tmr.wdclr()
            apSetup = true
            --            setupSocketServer()
        end
    end
    wifi.ap.config(initConf)
    tmr.softwd(30)
end

-- get table length
function getTableLenth(t)
    local leng = 0
    for k, v in pairs(t) do
        leng = leng + 1
    end
    return leng;
end

-- scan wifi and connect
function scanWifiAndConnect()
    -- open config file
    if file.open(localConfFileName) then
        local content = file.read()
        file.close()
        local config = sjson.decode(content)
        if config[keyConnectedWifi] then
            wifis = config[keyConnectedWifi]
            wifiIndex = 1
            if getTableLenth(wifis) >= wifiIndex then
                wifi.setmode(wifi.STATION)
                local staConf = wifis[wifiIndex]
                wifiIndex = wifiIndex + 1
                staConf.auto = true
                staConf.save = false
                staConf.got_ip_cb = function(T)
                    print("ip: ", T.IP, " netmask: ", T.netmask, " gateway: ", T.gateway)
                    connectedInfo = T
                end
                staConf.connected_cb = function(T)
                    print("ssid: ", T.ssid, " bssid: ", T.bssid, " channel: ", T.channel)
                end
                staConf.disconnected_cb = function(T)
                    print("ssid: ", T.ssid, " bssid: ", T.bssid, " channel: ", T.channel)
                end
                staConf.dhcp_timeout_cb = function()
                    print("timeout")
                end
                wifi.sta.config(staConf)
                --                for k, v in pairs(wifis[1]) do
                --                    print("k ", k, " v: ", v)
                --                end
            end
        end
    end
end


-- main
if checkConfigFileExist() then
    print("config file exist")
    scanWifiAndConnect()
else
    print("config file not exist")
    setupWifiAP()
end
