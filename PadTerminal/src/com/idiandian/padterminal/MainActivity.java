package com.idiandian.padterminal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private LinearLayout splash;
    private LinearLayout mMainLayout;

    private Handler mHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        splash = (LinearLayout) findViewById(R.id.splashscreen);
        ImageView iview = (ImageView) findViewById(R.id.iv);
        iview.setImageResource(R.drawable.splash);

        mMainLayout = (LinearLayout) findViewById(R.id.mainUi);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new TextViewAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                switch (position) {
                    case 0:
                        startWirelessSettings(view);
                        break;
                    case 1:
                        startLocalApp(view);
                        break;
                    case 2:
                        startWebApp(view);
                        break;
                    case 3:
                        startDAApp(view);
                        break;
                    default:
                        break;
                }

            }
        });

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splash.setVisibility(View.GONE);
                mMainLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);

    }

    public void startWirelessSettings(View v) {
        Intent i = new Intent(this, WirelessSettings.class);
        startActivity(i);
    }

    public void startLocalApp(View v) {
        Intent i = new Intent(this, LocalApp.class);
        startActivity(i);
    }

    public void startWebApp(View v) {
        // TODO
        Toast.makeText(this, "start web app", Toast.LENGTH_SHORT).show();
    }

    public void startDAApp(View v) {
        // TODO
        Toast.makeText(this, "start DA app", Toast.LENGTH_SHORT).show();
    }

}
