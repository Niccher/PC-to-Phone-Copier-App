package com.niccher.p2p_copier_app.utils;

import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static Retrofit retrofit = null;
    private static String lastBaseUrl = null;
    private static final Object lock = new Object();

    public static <S> S createService(Class<S> serviceClass, Context context) {
        String baseUrl = Konstants.Companion.getBaseUrl(context);
        synchronized (lock) {
            if (retrofit == null || !baseUrl.equals(lastBaseUrl)) {
                lastBaseUrl = baseUrl;
                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getUnsafeOkHttpClient(context))
                        .build();
            }
        }
        return retrofit.create(serviceClass);
    }

    public static void rebuildService(Context context) {
        Konstants.loadBackendConfig(context);
        synchronized (lock) {
            retrofit = null;
            lastBaseUrl = null;
        }
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        return getUnsafeOkHttpClient(null);
    }

    public static OkHttpClient getUnsafeOkHttpClient(final Context context) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[] {};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.addInterceptor(logging);

            if (context != null) {
                builder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        String uuid = Helpers.get_prefs_dev("dev_uuid", context);
                        if (uuid == null) uuid = "";

                        Request.Builder reqBuilder = original.newBuilder()
                                .header("X-Device-UUID", uuid)
                                .header("X-Device-Brand", String.valueOf(Build.BRAND))
                                .header("X-Device-Model", String.valueOf(Build.MODEL))
                                .header("X-Device-OS", "Android " + Build.VERSION.RELEASE)
                                .header("X-Device-Fingerprint", String.valueOf(Build.FINGERPRINT));

                        return chain.proceed(reqBuilder.build());
                    }
                });
            }

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
