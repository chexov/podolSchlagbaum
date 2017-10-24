package com.linevich.schlagbaum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private ScrollingActivity scrollingActivity;

    public MyBroadcastReceiver(ScrollingActivity scrollingActivity) {
        this.scrollingActivity = scrollingActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        scrollingActivity.log("state changed to " + stateStr + "; incoming number " + incomingNumber + "; needToCallback=" + scrollingActivity.needToCallback);

        if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            scrollingActivity.log("number called" + incomingNumber);
            scrollingActivity.callBaum();
        } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            scrollingActivity.needToCallback = true;
            killCall(context);
        }
    }

    public boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            scrollingActivity.log("MyBroadcastReceiver **" + ex.toString());
            return false;
        }
        return true;
    }
}
