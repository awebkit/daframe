
package com.idiandian.padterminal;

import android.util.Log;

public class Utils {

    public static boolean TEST = true;

    private static boolean LOG_ENABLE = true;
    private final static String LOG_TAG = "daapp";

    public static void log(String s) {
        if (LOG_ENABLE) {
            Log.i(LOG_TAG, s);
        }
    }
}
