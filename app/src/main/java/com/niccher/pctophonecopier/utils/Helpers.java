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

        if (id == null || id.isEmpty() || "undefined".equals(id)) {
            Konstants kon = new Konstants();
            android.content.SharedPreferences pref_dev = cntt.getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
            id = pref_dev.getString(ty, "");
        }
        return id;
    }

    public static void set_prefs_dev(String ty, String value, Context cntt){
        DevicePreferences devicePrefs = new DevicePreferences(cntt);
        if ("dev_uuid".equals(ty)){
            devicePrefs.saveDeviceUuidSync(value);
        }else if ("dev_status".equals(ty)){
            devicePrefs.saveDeviceStatusSync(value);
        }else if ("dev_message".equals(ty)){
            devicePrefs.saveDeviceMessageSync(value);
        }

        // Keep SharedPreferences for backward compatibility
        Konstants kon = new Konstants();
        android.content.SharedPreferences pref_dev = cntt.getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref_dev.edit();
        editor.putString(ty, value);
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

        if (id == null || id.isEmpty() || "undefined".equals(id)) {
            Konstants kon = new Konstants();
            android.content.SharedPreferences pref_auth = cntt.getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE);
            id = pref_auth.getString(ty, "");
        }
        return id;
    }

    public static void set_prefs_sess(String ty, String value, Context cntt){
        AuthPreferences authPrefs = new AuthPreferences(cntt);
        if ("auth_auth_code_id".equals(ty)){
            authPrefs.saveAuthCodeIdSync(value);
        }else if ("auth_auth_code".equals(ty)){
            authPrefs.saveAuthCodeSync(value);
        }else if ("auth_type".equals(ty)){
            authPrefs.saveAuthTypeSync(value);
        }

        // Keep SharedPreferences for backward compatibility
        Konstants kon = new Konstants();
        android.content.SharedPreferences pref_auth = cntt.getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref_auth.edit();
        editor.putString(ty, value);
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
        String[] f_info_data = {"Unknown", "application/octet-stream", "0 B"};
        try (Cursor returnCursor = cnt.getContentResolver().query(uri, null, null, null, null)) {
            if (returnCursor != null && returnCursor.moveToFirst()) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);

                String name = "Unknown";
                if (nameIndex != -1) {
                    name = returnCursor.getString(nameIndex);
                }

                long size = 0;
                if (sizeIndex != -1) {
                    size = returnCursor.getLong(sizeIndex);
                }

                String mime = cnt.getContentResolver().getType(uri);
                if (mime == null) {
                    mime = "application/octet-stream";
                }

                f_info_data[0] = name;
                f_info_data[1] = mime;
                f_info_data[2] = humanReadableByteCountBin(size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f_info_data;
    }
}
