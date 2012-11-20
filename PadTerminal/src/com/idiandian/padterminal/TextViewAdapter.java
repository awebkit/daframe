package com.idiandian.padterminal;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class TextViewAdapter extends BaseAdapter {

    private Context mContext;

    public TextViewAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mTextIds.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv;
        if (convertView == null) {
            tv = new TextView(mContext);
            tv.setLayoutParams(new GridView.LayoutParams(105, 85));
            tv.setPadding(8, 8, 8, 8);
            // tv.setTextAppearance(mContext, R.style.TextViewTest);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundResource(R.drawable.bg);
        } else {
            tv = (TextView) convertView;
        }
        tv.setText(mTextIds[position]);

        return tv;
    }

    private String[] mTextIds = {// 显示的文字
            "无线设置及初始化", "本地应用", "网络应用", "DA应用",
    };

}
