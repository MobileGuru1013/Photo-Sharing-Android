package org.aynsoft.javafile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnection {

    public static boolean checkNetworkConnection(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean infoResult = false;
        boolean wifiResult = false;
        boolean mobileResult = false;

        try {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info == null) {
                return false;
            } else {
                infoResult = info.isConnectedOrConnecting();
                }

            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi == null) {
                return false;
            } else {
                wifiResult = wifi.isConnectedOrConnecting();
                }

            NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobile == null) {
                return false;
            } else {
                mobileResult = mobile.isConnectedOrConnecting();
                }

            // if(wifi.isConnectedOrConnecting()||mobile.isConnectedOrConnecting())

        } catch (NullPointerException nullPointException) {
            System.out.println( nullPointException.getMessage() );
        }

        return infoResult||wifiResult||mobileResult;
    }
}