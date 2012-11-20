package com.idiandian.padterminal;

import java.util.ArrayList;
import java.util.List;

public class HotspotList {
    private List<String> mList = new ArrayList<String>();

    private boolean mCompleted = false;

    private static HotspotList sInstance;

    public static HotspotList getInstance() {
        if (sInstance == null) {
            sInstance = new HotspotList();
        }

        return sInstance;
    }

    private HotspotList() {
        // TODO
    }

    public void addHotspot(String hotspot) {
        mList.add(hotspot);
    }

    public void complete() {
        mCompleted = true;
    }

    public List<String> getHotspotList() {
        return mList;
    }

    public int getHotspotCount() {
        return mList.size();
    }

    public String getHotspot(int pos) {
        return mList.get(pos);
    }

    public boolean isComplete() {
        return mCompleted;
    }

    public void clear() {
        mList.clear();
    }
}
