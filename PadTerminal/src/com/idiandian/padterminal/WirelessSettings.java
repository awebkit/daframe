package com.idiandian.padterminal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

public class WirelessSettings extends Activity implements AdapterView.OnItemClickListener {

    private Activity mActivity;

    private ProgressBar mProgressBar;
    private ListView mHotspotsView;

    private WifiManager mWifiManager;
    private WifiReceiver mWifiReceiver;

    private boolean mConnected;

    private final static int STOP_SPLASH = 1001;
    private static final int START_PROGRESS = 110;
    private static final int STOP_PROGRESS = 111;
    private static final int CONNECT_STATUS = 113;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_settings);

        mActivity = this;

        mHotspotsView = (ListView) findViewById(R.id.wirelessListView);
        mProgressBar = (ProgressBar) findViewById(R.id.wirelessProgressBar);

        mProgressBar.setVisibility(View.VISIBLE);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // start thread to search hotspot
        threadForSearchHotspot();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiReceiver);
    }

    private void setupHotspotList() {

        if (HotspotList.getInstance().isComplete() &&
                HotspotList.getInstance().getHotspotCount() >= 1) {
            List<String> hotspots = HotspotList.getInstance().getHotspotList();

            if (hotspots.size() > 1) {
                mHotspotsView.setVisibility(View.VISIBLE);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, hotspots);
                mHotspotsView.setAdapter(adapter);
                mHotspotsView.setOnItemClickListener(this);
            } else if (hotspots.size() == 1) {
                // TODO
                String hotspotName = hotspots.get(0);
                Toast.makeText(this, "开始连接" + hotspotName, Toast.LENGTH_LONG).show();
                threadForConnectHotspot(hotspotName);
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("错误")
                    .setMessage("查找热点失败，请点击退出并检查网络，然后重试")
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            // android.os.Process.killProcess(android.os.Process.myPid());
                            finish();
                        }
                    })
                    .setPositiveButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        String hotspot = HotspotList.getInstance().getHotspot(position);
        Toast.makeText(this, "开始连接" + hotspot, Toast.LENGTH_LONG).show();

        threadForConnectHotspot(hotspot);
    }

    private void threadForSearchHotspot() {
        startProgressBar();
        new Thread() {
            @Override
            public void run() {
                searchHotspot();
            }

        }.start();
    }

    private void threadForConnectHotspot(final String name) {
        startProgressBar();
        new Thread() {
            @Override
            public void run() {
                connectHotspot(name);
            }
        }.start();
    }

    private void searchHotspot() {
        mWifiManager.startScan();
        // stop splash when search done
        int count = 0;
        while (true) {
            SystemClock.sleep(1000);
            count++;
            if (HotspotList.getInstance().isComplete() || count >= 4) {
                stopSplash();
            }
            break;
        }

    }

    private void connectHotspot(String name) {
        // TODO Auto-generated method stub
        if (name == null) {
            return;
        }
        WifiConfiguration wifiConfig = setWifiParams(name);
        int wcgID = mWifiManager.addNetwork(wifiConfig);
        mConnected = mWifiManager.enableNetwork(wcgID, true);

        promptUserConnectStatus(name);
    }

    private WifiConfiguration setWifiParams(String ssid) {
        // TODO Auto-generated method stub
        WifiConfiguration apConfig = new WifiConfiguration();
        apConfig.SSID = "\"" + ssid + "\"";
        apConfig.preSharedKey = "\"88888888\"";
        apConfig.hiddenSSID = true;
        apConfig.status = WifiConfiguration.Status.ENABLED;
        apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        return apConfig;
    }

    private void promptUserConnectStatus(String name) {
        Message msg = new Message();
        msg.what = CONNECT_STATUS;
        msg.obj = name;
        mHandler.sendMessageDelayed(msg, 500);
    }

    private Handler mHandler = new Handler() {
        //
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_SPLASH:
                    mProgressBar.setVisibility(View.GONE);
                    // setupHotspotView();
                    setupHotspotList();
                    break;

                case STOP_PROGRESS:
                    mProgressBar.setVisibility(View.GONE);
                    break;

                case START_PROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;

                case CONNECT_STATUS:
                    mProgressBar.setVisibility(View.GONE);
                    String name = (String) msg.obj;
                    String message = null;
                    if (mConnected) {
                        message = "成功连接" + name + "。请按后退键返回主界面";
                    } else {
                        message = "连接" + name + "失败。请按后退键返回主界面";
                    }
                    Toast.makeText(mActivity, message, Toast.LENGTH_LONG)
                            .show();
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private void stopSplash() {
        Message msg = new Message();
        msg.what = STOP_SPLASH;
        mHandler.sendMessageDelayed(msg, 0);
    }

    private void startProgressBar() {
        Message msg = new Message();
        msg.what = START_PROGRESS;
        mHandler.sendMessageDelayed(msg, 0);
    }

    @SuppressLint("HandlerLeak")
    public void postMessage(int what, int arg1, int arg2, Object obj,
            long delayMillis) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(what, arg1, arg2,
                obj), delayMillis);
    }

    // ---------------------------------------------------------------
    // Listen to hotspot change
    private final class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> wifiList = mWifiManager.getScanResults();
            if (wifiList == null || wifiList.size() == 0) {
                return;
            }
            onReceiveNewNetworks(wifiList);
        }

        // Search special hotspots
        private void onReceiveNewNetworks(List<ScanResult> wifiList) {
            HotspotList.getInstance().clear();
            for (ScanResult result : wifiList) {
                if ((result.SSID).startsWith("ANDROIDTEST")) {
                    HotspotList.getInstance().addHotspot(result.SSID);
                }
            }
            HotspotList.getInstance().complete();
        }

    }

    // ------------------------------------------------------------------
}
