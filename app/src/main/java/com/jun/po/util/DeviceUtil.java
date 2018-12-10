package com.jun.po.util;

public class DeviceUtil {
    public static void getDeviceInfo() {
        String BOARD = android.os.Build.BOARD;//获取设备基板名称
        String BRAND = android.os.Build.BRAND;//获取设备品牌
        String CPU_ABI = android.os.Build.CPU_ABI;//获取设备指令集名称（CPU的类型）
        String HOST = android.os.Build.HOST;//设备主机地址
        String ID = android.os.Build.ID;//设备版本号。
        String MODEL = android.os.Build.MODEL;//获取手机的型号 设备名称。
        String MANUFACTURER = android.os.Build.MANUFACTURER;//获取设备制造商
        String TAGS = android.os.Build.TAGS;//设备标签。如release - keys 或测试的 test -keys
        String RELEASE = android.os.Build.VERSION.RELEASE;//获取系统版本字符串。如4 .1 .2 或2 .2 或2 .3 等
    }
}
