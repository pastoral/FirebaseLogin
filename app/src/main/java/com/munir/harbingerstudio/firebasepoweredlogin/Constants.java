package com.munir.harbingerstudio.firebasepoweredlogin;

/**
 * Created by munirul.hoque on 9/6/2017.
 */

public class Constants {
    public static final int REQ = 1;
    public static final int REQUEST_CHECK_SETTINGS = 19;
    public final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    public static final int permsRequestCode = 20;
    public static String[] permisionList = { "android.permission.ACCESS_FINE_LOCATION" , "android.permission.ACCESS_COARSE_LOCATION", "android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"};
    public static boolean isFirebaseReady = false;
    public static final String CONFIG_IS_ADMOB_ON = "is_admob_on";
    public static final String CONFIG_DISABLE_ADMOB_FOR = "disable_admob_for";
    // public static final String CONFIG_COLOR_PRIMARY_ORANGE = "disable_admob_for"
    public static final String APPTAG = "IslamicLife Application";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int DISPLACEMENT = 10; // 10 meters
}
