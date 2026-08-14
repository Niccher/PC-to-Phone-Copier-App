package com.niccher.p2p_copier_app.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.niccher.p2p_copier_app.datastore.AuthPreferences;
import com.niccher.p2p_copier_app.datastore.DevicePreferences;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Helpers {

    public static String get_prefs_dev(String ty, Context cntt){
        DevicePreferences devicePrefs = new DevicePreferences(cntt);
        String value = devicePrefs.getStringSync(ty, "");
        if (value == null || value.isEmpty() || "undefined".equals(value)) {
            Konstants kon = new Konstants();
            android.content.SharedPreferences pref_dev = cntt.getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
            value = pref_dev.getString(ty, "");
            if (value != null && !value.isEmpty()) {
                devicePrefs.saveStringSync(ty, value);
            }
        }
        return value != null ? value : "";
    }

    public static void set_prefs_dev(String ty, String value, Context cntt){
        DevicePreferences devicePrefs = new DevicePreferences(cntt);
        devicePrefs.saveStringSync(ty, value);

        Konstants kon = new Konstants();
        android.content.SharedPreferences pref_dev = cntt.getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
        pref_dev.edit().putString(ty, value).apply();
    }

    public static String get_prefs_sess(String ty, Context cntt){
        AuthPreferences authPrefs = new AuthPreferences(cntt);
        String value = authPrefs.getStringSync(ty, "");
        if (value == null || value.isEmpty() || "undefined".equals(value)) {
            Konstants kon = new Konstants();
            android.content.SharedPreferences pref_auth = cntt.getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE);
            value = pref_auth.getString(ty, "");
            if (value != null && !value.isEmpty()) {
                authPrefs.saveStringSync(ty, value);
            }
        }
        return value != null ? value : "";
    }

    public static void set_prefs_sess(String ty, String value, Context cntt){
        AuthPreferences authPrefs = new AuthPreferences(cntt);
        authPrefs.saveStringSync(ty, value);

        Konstants kon = new Konstants();
        android.content.SharedPreferences pref_auth = cntt.getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE);
        pref_auth.edit().putString(ty, value).apply();
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
