package com.idiandian.padterminal;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PadActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pad);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_pad, menu);
        return true;
    }
}
