package com.idiandian.padterminal;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class LocalApp extends Activity {

    private GridView mAppsView;

    private List<ResolveInfo> mApps;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_app);

        init();
    }

    private void init() {
        mAppsView = (GridView) findViewById(R.id.localGridView);

        setupAppsView();
    }

    private void setupAppsView() {
        loadApps();
        mAppsView.setAdapter(new AppsAdapter());
    }

    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    // ---------------------------------------------------------------------------
    public class AppsAdapter extends BaseAdapter {
        public AppsAdapter() {
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i;

            if (convertView == null) {
                i = new ImageView(LocalApp.this);
                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
                i.setLayoutParams(new GridView.LayoutParams(50, 50));
            } else {
                i = (ImageView) convertView;
            }

            ResolveInfo info = mApps.get(position);
            i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));

            return i;
        }

        @Override
        public final int getCount() {
            return mApps.size();
        }

        @Override
        public final Object getItem(int position) {
            return mApps.get(position);
        }

        @Override
        public final long getItemId(int position) {
            return position;
        }
    }
    // ----------------------------------------------------------------------------------

}
