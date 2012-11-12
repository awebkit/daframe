package com.idiandian.phoneterminal;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PhoneActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_phone, menu);
        return true;
    }
}
