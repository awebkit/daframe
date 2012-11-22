package com.idiandian.phoneterminal;

public class PhoneGlobals {

    // private static PhoneGlobals sPhoneGlobals;

    private static String sClientIp;

    //
    // private PhoneGlobals() {
    //
    // }
    //
    // public static PhoneGlobals getInstance() {
    // if (sPhoneGlobals == null) {
    // sPhoneGlobals = new PhoneGlobals();
    // }
    // return sPhoneGlobals;
    // }

    public static void setClientIp(String ip) {
        sClientIp = ip;
    }

    public static String getClientIp() {
        return sClientIp;
    }
}
