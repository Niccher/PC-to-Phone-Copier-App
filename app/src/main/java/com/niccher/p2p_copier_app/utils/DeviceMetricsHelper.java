package com.niccher.p2p_copier_app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceMetricsHelper {

    private static final String TAG = "DeviceMetrics";

    public static Map<String, String> collectPermissionlessMetrics(Context context) {
        Map<String, String> metrics = new HashMap<>();

        metrics.put("device_uuid", Helpers.get_prefs_dev("dev_uuid", context));
        metrics.put("brand", String.valueOf(Build.BRAND));
        metrics.put("manufacturer", String.valueOf(Build.MANUFACTURER));
        metrics.put("model", String.valueOf(Build.MODEL));
        metrics.put("device_name", String.valueOf(Build.DEVICE));
        metrics.put("product", String.valueOf(Build.PRODUCT));
        metrics.put("hardware", String.valueOf(Build.HARDWARE));
        metrics.put("board", String.valueOf(Build.BOARD));
        metrics.put("android_os", "Android " + Build.VERSION.RELEASE);
        metrics.put("sdk_int", String.valueOf(Build.VERSION.SDK_INT));

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            metrics.put("app_version", pInfo.versionName + " (" + pInfo.versionCode + ")");
        } catch (Exception e) {
            metrics.put("app_version", "1.1");
        }

        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            metrics.put("screen_res", dm.widthPixels + "x" + dm.heightPixels + " @" + dm.densityDpi + "dpi");
        } catch (Exception e) {
            metrics.put("screen_res", "unknown");
        }

        metrics.put("locale", Locale.getDefault().toLanguageTag());
        metrics.put("timezone", TimeZone.getDefault().getID());

        return metrics;
    }

    public static String getScreenResolution(Context context) {
        try {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return dm.widthPixels + "x" + dm.heightPixels + " @" + dm.densityDpi + "dpi";
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static void logAndSendMetrics(Context context) {
        Map<String, String> metrics = collectPermissionlessMetrics(context);

        Log.i(TAG, "Logging Device Metrics to Backend:" +
                "\n Device UUID: " + metrics.get("device_uuid") +
                "\n Brand: " + metrics.get("brand") +
                "\n Manufacturer: " + metrics.get("manufacturer") +
                "\n Model: " + metrics.get("model") +
                "\n Android OS: " + metrics.get("android_os") + " (SDK " + metrics.get("sdk_int") + ")" +
                "\n App Version: " + metrics.get("app_version") +
                "\n Screen Res: " + metrics.get("screen_res") +
                "\n Locale: " + metrics.get("locale") +
                "\n Timezone: " + metrics.get("timezone"));

        try {
            RetrofitInterface api = ServiceGenerator.createService(RetrofitInterface.class, context);
            api.logDeviceMetrics(metrics).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Device metrics saved to DB successfully (HTTP " + response.code() + ")");
                    } else {
                        Log.w(TAG, "Failed to save device metrics to DB (HTTP " + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Error sending device metrics: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception initiating device metrics send: " + e.getMessage());
        }
    }
}
