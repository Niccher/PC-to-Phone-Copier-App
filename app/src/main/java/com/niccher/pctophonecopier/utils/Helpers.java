package com.niccher.pctophonecopier.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.niccher.pctophonecopier.datastore.AuthPreferences;
import com.niccher.pctophonecopier.datastore.DevicePreferences;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Helpers {

    public static String get_prefs_dev(String ty, Context cntt){
        DevicePreferences devicePrefs = new DevicePreferences(cntt);
        String id = "";
        if ("dev_uuid".equals(ty)){
            id = devicePrefs.getDeviceUuidSync();
        }else if ("dev_status".equals(ty)){
            id = devicePrefs.getDeviceStatusSync();
        }else if ("dev_message".equals(ty)){
            id = devicePrefs.getDeviceMessageSync();
        }
        return id;
    }

    public static void set_prefs_dev(String ty, String value, Context cntt){
        // For now, keep using SharedPreferences for setters until DataStore is fully integrated
        Konstants kon = new Konstants();
        android.content.SharedPreferences pref_dev = cntt.getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref_dev.edit();

        if ("dev_uuid".equals(ty)){
            editor.putString("dev_uuid", value);
        }else if ("dev_status".equals(ty)){
            editor.putString("dev_status", value);
        }else if ("dev_message".equals(ty)){
            editor.putString("dev_message", value);
        }
        editor.apply();
    }

    public static String get_prefs_sess(String ty, Context cntt){
        AuthPreferences authPrefs = new AuthPreferences(cntt);
        String id = "";
        if ("auth_auth_code_id".equals(ty)){
            id = authPrefs.getAuthCodeIdSync();
        }else if ("auth_auth_code".equals(ty)){
            id = authPrefs.getAuthCodeSync();
        }else if ("auth_type".equals(ty)){
            id = authPrefs.getAuthTypeSync();
        }
        return id;
    }

    public static void set_prefs_sess(String ty, String value, Context cntt){
        // For now, keep using SharedPreferences for setters until DataStore is fully integrated
        Konstants kon = new Konstants();
        android.content.SharedPreferences pref_dev = cntt.getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref_dev.edit();

        if ("auth_auth_code_id".equals(ty)){
            editor.putString("auth_auth_code_id", value);
        }else if ("auth_auth_code".equals(ty)){
            editor.putString("auth_auth_code", value);
        }else if ("auth_type".equals(ty)){
            editor.putString("auth_type", value);
        }
        editor.apply();
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public static String[] getFileName(Uri uri, Context cnt) {
        Cursor returnCursor = cnt.getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        //String[] f_info_data = {f_name, f_size, f_mime};
        Long f_size = Long.valueOf(returnCursor.getString(sizeIndex));
        String[] f_info_data = {returnCursor.getString(nameIndex), cnt.getContentResolver().getType(uri), humanReadableByteCountBin(f_size)};
        returnCursor.close();

        return f_info_data;
    }
}
