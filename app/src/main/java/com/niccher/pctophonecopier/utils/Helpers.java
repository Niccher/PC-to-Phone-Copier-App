package com.niccher.pctophonecopier.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

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

    public String[] getFileName(Uri uri, Context cnt) {
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
