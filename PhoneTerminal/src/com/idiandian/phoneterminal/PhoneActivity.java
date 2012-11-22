
package com.idiandian.phoneterminal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@SuppressLint({
        "NewApi", "NewApi"
})
public class PhoneActivity extends Activity {

    private static final int WIFI_AP_STATE_UNKNOWN = -1;
    private static final int WIFI_AP_STATE_DISABLING = 0;
    private static final int WIFI_AP_STATE_DISABLED = 1;
    private static final int WIFI_AP_STATE_ENABLING = 2;
    private static final int WIFI_AP_STATE_ENABLED = 3;
    private static final int WIFI_AP_STATE_FAILED = 4;

    private final String[] WIFI_STATE_TEXTSTATE = new String[] {
            "DISABLING",
            "DISABLED", "ENABLING", "ENABLED", "FAILED"
    };

    private static int UDP_PORT = 8899;

    private WifiManager wifi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatusDisplay();
    }

    public void toggleWifi(View v) {
        boolean wifiApIsOn = getWifiAPState() == WIFI_AP_STATE_ENABLED
                || getWifiAPState() == WIFI_AP_STATE_ENABLING;
        new SetWifiAPTask(!wifiApIsOn, false).execute();
    }

    public void close(View v) {
        boolean wifiApIsOn = getWifiAPState() == WIFI_AP_STATE_ENABLED
                || getWifiAPState() == WIFI_AP_STATE_ENABLING;
        if (wifiApIsOn) {
            new SetWifiAPTask(false, true).execute();
        } else {
            finish();
        }
    }

    /**
     * Endable/disable wifi
     *
     * @param enabled
     * @return WifiAP state
     */
    private int setWifiApEnabled(boolean enabled) {
        Log.d("WifiAP", "*** setWifiApEnabled CALLED **** " + enabled);
        if (enabled && wifi.getConnectionInfo() != null) {
            wifi.setWifiEnabled(false);
            try {
                Thread.sleep(1500);
            } catch (Exception e) {
            }
        }

        // int duration = Toast.LENGTH_LONG;
        // String toastText = "MobileAP status: ";
        int state = WIFI_AP_STATE_UNKNOWN;
        try {
            wifi.setWifiEnabled(false);
            // 热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            // 配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "ANDROIDTEST12345";
            // 配置热点的密码
            apConfig.preSharedKey = "88888888";
            apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            Method method1 = wifi.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method1.invoke(wifi, apConfig, enabled); // true

            Method method2 = wifi.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(wifi);
        } catch (Exception e) {
            Log.e(WIFI_SERVICE, e.getMessage());
            // toastText += "ERROR " + e.getMessage();
        }

        if (!enabled) {
            int loopMax = 10;
            while (loopMax > 0
                    && (getWifiAPState() == WIFI_AP_STATE_DISABLING
                            || getWifiAPState() == WIFI_AP_STATE_ENABLED || getWifiAPState() == WIFI_AP_STATE_FAILED)) {
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {
                }
            }
            wifi.setWifiEnabled(true);
        } else if (enabled) {
            int loopMax = 10;
            while (loopMax > 0
                    && (getWifiAPState() == WIFI_AP_STATE_ENABLING
                            || getWifiAPState() == WIFI_AP_STATE_DISABLED || getWifiAPState() == WIFI_AP_STATE_FAILED)) {
                try {
                    Thread.sleep(500);
                    loopMax--;
                } catch (Exception e) {
                }
            }
        }

        return state;
    }

    private int getWifiAPState() {
        int state = WIFI_AP_STATE_UNKNOWN;
        try {
            Method method2 = wifi.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(wifi);
        } catch (Exception e) {
        }
        Log.d("WifiAP", "getWifiAPState.state "
                + (state == -1 ? "UNKNOWN" : WIFI_STATE_TEXTSTATE[state]));
        return state;
    }

    private void updateStatusDisplay() {

        if (getWifiAPState() == WIFI_AP_STATE_ENABLED
                || getWifiAPState() == WIFI_AP_STATE_ENABLING) {
            Toast.makeText(this, "热点已启动", Toast.LENGTH_SHORT).show();
            // start server thread
            new Thread() {
                @Override
                public void run() {
                    try {
                        threadForUdpServer();
                        new TcpSocketServer();
                    } catch (SocketException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            Toast.makeText(this, "热点已关闭", Toast.LENGTH_SHORT).show();
        }

    }

    private void threadForUdpServer() throws SocketException, UnknownHostException {
        byte serverBuf[] = new byte[1024];
        DatagramPacket pack = new DatagramPacket(serverBuf, 1024);
        DatagramSocket socket = new DatagramSocket(UDP_PORT, InetAddress.getByName("0.0.0.0"));
        socket.setBroadcast(false);
        while (true) {
            try {
                Log.i("test", "========== socket server running ....");

                socket.receive(pack);
                Log.i("test", "========== socket server receive packet ....");

                String message = new String(pack.getData()).trim();
                if (message.equals("DISCOVER_SERVER_REQUEST")) {
                    Log.i("test", "client ip : " + pack.getAddress().getHostAddress());

                    PhoneGlobals.setClientIp(pack.getAddress().getHostAddress());

                    serverBuf = "DISCOVER_SERVER_RESPONSE".getBytes();

                    DatagramPacket serverPacket = new DatagramPacket(serverBuf,
                            serverBuf.length, pack.getAddress(), pack.getPort());
                    socket.send(serverPacket);
                }
                break;
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                socket.close();
            } finally {
                socket.close();
            }
        }
    }

    private void threadForTcpServer() {

    }

    class SetWifiAPTask extends AsyncTask<Void, Void, Void> {

        boolean mMode;
        boolean mFinish;

        public SetWifiAPTask(boolean mode, boolean finish) {
            mMode = mode;
            mFinish = finish;
        }

        ProgressDialog d = new ProgressDialog(PhoneActivity.this);

        @SuppressLint("NewApi")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // d.setTitle("Turning WiFi AP " + (mMode ? "on" : "off") + "...");
            // d.setMessage("...please wait a moment.");
            d.setTitle((mMode ? "打开" : "关闭") + "热点...");
            d.setMessage("请稍候...");
            d.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                d.dismiss();
            } catch (IllegalArgumentException e) {
            }
            ;
            updateStatusDisplay();
            if (mFinish) {
                finish();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            setWifiApEnabled(mMode);
            return null;
        }
    }

}
