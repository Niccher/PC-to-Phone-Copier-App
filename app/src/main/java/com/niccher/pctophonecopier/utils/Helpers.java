package com.niccher.pctophonecopier.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Helpers {

    public static String get_prefs_dev(String ty, Context cntt){
        Konstants kon;
        kon = new Konstants();
        SharedPreferences pref_dev = cntt.getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
        String id = "";
        if (ty=="dev_uuid"){
            id = pref_dev.getString("dev_uuid", "undefined");
        }else if (ty=="dev_status"){
            id = pref_dev.getString("dev_status", "undefined");
        }else if (ty=="dev_message"){
            id = pref_dev.getString("dev_message", "undefined");
        }
        return id;
    }
}
