package com.munir.harbingerstudio.firebasepoweredlogin;

import android.app.Application;
import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

/**
 * Created by munirul.hoque on 9/14/2017.
 */

public class ModelInfo extends Application {
    public String getSystemProperty(String key) {
        String value = null;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public String getDeviceImei(TelephonyManager mTelephonyManager) {


        String deviceid = mTelephonyManager.getDeviceId();
        return deviceid;
    }

    public  boolean isSimSupport(TelephonyManager mTelephonyManager, Context context)
    {
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT);

    }

    public String getPhoneNumber(Context context) {
        String number="";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
            List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

            Log.d("Test", "Current list = " + subsInfoList);

            for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                number = subscriptionInfo.getNumber();

                Log.d("Test", " Number is  " + number);
            }
        }
        else{
            number="";
        }
        return number;
    }

}

