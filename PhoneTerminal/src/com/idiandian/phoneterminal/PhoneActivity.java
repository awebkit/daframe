package com.idiandian.phoneterminal;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Method;

public class PhoneActivity extends Activity {
    private WifiManager wifiManager;
    private Button open;
    private boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        // 获取wifi管理服务
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        open = (Button) findViewById(R.id.open_hotspot);
        // 通过按钮事件设置热点
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果是打开状态就关闭，如果是关闭就打开
                flag = !flag;
                setWifiApEnabled(flag);
            }
        });
    }

    // wifi热点开关
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            // wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifiManager.setWifiEnabled(false);
        }
        try {
            // 热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            // 配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "ANDROIDTEST12345";
            // 配置热点的密码
            apConfig.preSharedKey = "88888888";
            apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            // 通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            // 返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }

}
