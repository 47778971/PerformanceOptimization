package com.jun.po.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DeviceUtil {
    public static void getDeviceInfo(Context context) {
        String BOARD = android.os.Build.BOARD;//获取设备基板名称
        String BRAND = android.os.Build.BRAND;//获取设备品牌
        String CPU_ABI = android.os.Build.CPU_ABI;//获取设备指令集名称（CPU的类型）
        String HOST = android.os.Build.HOST;//设备主机地址
        String ID = android.os.Build.ID;//设备版本号。
        String MODEL = android.os.Build.MODEL;//获取手机的型号 设备名称。
        String MANUFACTURER = android.os.Build.MANUFACTURER;//获取设备制造商
        String TAGS = android.os.Build.TAGS;//设备标签。如release - keys 或测试的 test -keys
        String RELEASE = android.os.Build.VERSION.RELEASE;//获取系统版本字符串。如4 .1 .2 或2 .2 或2 .3 等
        String IP = getIPAddress(context);
        record(context, "","","","","","","","","","","","","","");
    }

    /**
     * 性能埋点
     *
     * @param userId      用户id
     * @param userType    用户类型
     * @param packageName 应用包名
     * @param appVersion  app的版本号
     * @param permission  权限列表(有权限的项目，逗号分隔)
     * @param dsType      数据来源类型(crash、exception、ANR、consumeTime、pageLoad、networkRequest、fileDownload)
     * @param type        类型
     * @param page        事件发生的页面
     * @param errorPoint  出错点
     * @param describe    描述
     * @param reason      报错的原因
     * @param consumeTime 耗时时长(单位毫秒)
     * @param upload      上传流量（压缩后的）
     * @param download    下载流量（压缩后的）
     */
    public static void record(Context context, String userId, String userType, String packageName, String appVersion, String permission, String dsType, String type,
                              String page, String errorPoint, String describe, String reason, String consumeTime, String upload, String download) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", userId);
            jsonObject.put("usertype", userType);
            jsonObject.put("ip", getIPAddress(context));
            jsonObject.put("appname", packageName);
            jsonObject.put("appversion", appVersion);
            jsonObject.put("manufacturer", android.os.Build.MANUFACTURER);
            jsonObject.put("os", "Android");
            jsonObject.put("osversion", android.os.Build.VERSION.RELEASE);
            jsonObject.put("model", android.os.Build.MODEL);
            jsonObject.put("serial", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            jsonObject.put("carrier", getOperatorName(context));
            jsonObject.put("permission", permission);
            jsonObject.put("createtime", System.currentTimeMillis());
            jsonObject.put("dstype", dsType);
            jsonObject.put("type", type);
            jsonObject.put("page", page);
            jsonObject.put("errorpoint", errorPoint);
            jsonObject.put("describe", describe);
            jsonObject.put("reason", reason);
            jsonObject.put("consumetime", consumeTime);
            jsonObject.put("upload", upload);
            jsonObject.put("download", download);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                return ipAddress;
            }
        }
        return "";
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    /**
     * 获取当前的运营商
     */
    public static String getOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimOperatorName();
    }

}
