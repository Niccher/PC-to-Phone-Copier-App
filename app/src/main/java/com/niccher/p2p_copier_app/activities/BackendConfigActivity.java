package com.niccher.p2p_copier_app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.utils.Konstants;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BackendConfigActivity extends AppCompatActivity {

    private TextInputEditText et_backend_url;
    private TextInputEditText et_backend_port;
    private Button btn_test_connection;
    private Button btn_save_config;
    private Button btn_test_and_save;
    private ProgressBar progressBar;
    private TextView tv_status;
    private TextView tv_current_url;

    private volatile boolean testResult = false;
    private volatile String testMessage = "Not tested yet";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backend_config);

        et_backend_url = findViewById(R.id.et_backend_url);
        et_backend_port = findViewById(R.id.et_backend_port);
        btn_test_connection = findViewById(R.id.btn_test_connection);
        btn_save_config = findViewById(R.id.btn_save_config);
        btn_test_and_save = findViewById(R.id.btn_test_and_save);
        progressBar = findViewById(R.id.progressBar);
        tv_status = findViewById(R.id.tv_status);
        tv_current_url = findViewById(R.id.tv_current_url);

        loadCurrentConfig();

        btn_test_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnection();
            }
        });

        btn_save_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfig();
            }
        });

        btn_test_and_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testConnectionAndSave();
            }
        });
    }

    private void loadCurrentConfig() {
        String savedHost = Konstants.getBackendUrl(this);
        String savedPort = Konstants.getBackendPort(this);

        if (savedHost.contains("://")) {
            savedHost = savedHost.substring(savedHost.indexOf("://") + 3);
        }
        if (savedHost.contains(":")) {
            savedHost = savedHost.substring(0, savedHost.indexOf(":"));
        }
        if (savedHost.contains("/")) {
            savedHost = savedHost.substring(0, savedHost.indexOf("/"));
        }

        et_backend_url.setText(savedHost);
        et_backend_port.setText(TextUtils.isEmpty(savedPort) ? "80" : savedPort);

        String activeFullUrl = Konstants.getBaseUrl(this);
        tv_current_url.setText(String.format("Current URL: %s", activeFullUrl));
        updateStatusDisplay(false, "Not tested yet");
    }

    private String buildUrlFromInputs() {
        String urlText = et_backend_url.getText().toString().trim();
        String portText = et_backend_port.getText().toString().trim();

        if (TextUtils.isEmpty(urlText)) {
            return Konstants.DEFAULT_BACKEND_URL;
        }

        String scheme = "http://";
        if (urlText.startsWith("https://")) {
            scheme = "https://";
            urlText = urlText.substring(8);
        } else if (urlText.startsWith("http://")) {
            scheme = "http://";
            urlText = urlText.substring(7);
        } else if (!TextUtils.isEmpty(portText) && "443".equals(portText.trim())) {
            scheme = "https://";
        }

        if (urlText.endsWith("/")) {
            urlText = urlText.substring(0, urlText.length() - 1);
        }

        if (!urlText.contains(":") && !TextUtils.isEmpty(portText)) {
            int portInt = 80;
            try {
                portInt = Integer.parseInt(portText.trim());
            } catch (NumberFormatException ignored) {}

            if (("https://".equals(scheme) && portInt != 443) || ("http://".equals(scheme) && portInt != 80)) {
                urlText = urlText + ":" + portInt;
            }
        }

        return scheme + urlText;
    }

    private OkHttpClient getTestClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[] {};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
        } catch (Exception e) {
            return new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
    }

    private void testConnection() {
        final String testUrl = buildUrlFromInputs();
        final String pingUrl = testUrl.endsWith("/") ? testUrl + "device/ping" : testUrl + "/device/ping";
        testResult = false;
        testMessage = "Testing connection...";
        progressBar.setVisibility(View.VISIBLE);
        tv_status.setText("Testing connection (" + pingUrl + ")...");
        tv_status.setTextColor(getResources().getColor(R.color.dull_warning));
        btn_test_connection.setEnabled(false);
        btn_test_and_save.setEnabled(false);
        btn_save_config.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = getTestClient();
                Request request = new Request.Builder()
                        .url(pingUrl)
                        .get()
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    final int code = response.code();
                    final String bodyStr = response.body() != null ? response.body().string() : "";
                    final boolean isValidP2pServer = response.isSuccessful() && bodyStr.contains("P2P Copier WebApp");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            btn_test_connection.setEnabled(true);
                            if (isValidP2pServer) {
                                testResult = true;
                                testMessage = "P2P Copier WebApp Verified (HTTP " + code + ")";
                                updateStatusDisplay(true, testMessage);
                            } else if (response.isSuccessful()) {
                                testResult = false;
                                testMessage = "Server reached (HTTP " + code + "), but not a P2P Copier instance";
                                updateStatusDisplay(false, testMessage);
                            } else {
                                testResult = false;
                                testMessage = "Server responded with HTTP " + code;
                                updateStatusDisplay(false, testMessage);
                            }
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            btn_test_connection.setEnabled(true);
                            testResult = false;
                            String errorMsg = e.getClass().getSimpleName();
                            if (e.getMessage() != null && e.getMessage().contains("timeout")) {
                                errorMsg = "Connection timed out";
                            } else if (e.getMessage() != null && e.getMessage().contains("refused")) {
                                errorMsg = "Connection refused";
                            } else if (e.getMessage() != null && e.getMessage().contains("unreachable")) {
                                errorMsg = "Host unreachable";
                            } else if (e.getMessage() != null && (e.getMessage().contains("unable to resolve")
                                    || e.getMessage().contains("No address"))) {
                                errorMsg = "Unable to resolve host";
                            } else if (e.getMessage() != null) {
                                errorMsg = e.getMessage();
                            }
                            testMessage = "Failed: " + errorMsg;
                            updateStatusDisplay(false, testMessage);
                        }
                    });
                }
            }
        }).start();
    }

    private void saveConfig() {
        String urlText = et_backend_url.getText().toString().trim();
        String portText = et_backend_port.getText().toString().trim();

        if (TextUtils.isEmpty(urlText)) {
            Toast.makeText(this, "Please enter a backend URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!testResult) {
            new AlertDialog.Builder(this)
                    .setTitle("Save Without Testing?")
                    .setMessage("You have not tested the connection yet. The URL may be incorrect. Do you want to save anyway?")
                    .setPositiveButton("Save Anyway", (dialog, which) -> performSave())
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false)
                    .show();
            return;
        }

        performSave();
    }

    private void testConnectionAndSave() {
        testConnection();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(12000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (testResult) {
                            performSave();
                        } else {
                            Toast.makeText(BackendConfigActivity.this,
                                    "Please fix the connection issue before saving",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void performSave() {
        String fullUrl = buildUrlFromInputs();
        String port = et_backend_port.getText().toString().trim();

        if (TextUtils.isEmpty(port)) {
            port = fullUrl.startsWith("https://") ? "443" : "80";
        }

        Konstants.saveBackendConfig(this, fullUrl, port);
        ServiceGenerator.rebuildService(this);
        com.niccher.p2p_copier_app.utils.DeviceMetricsHelper.logAndSendMetrics(this);

        Konstants.saveBackendStatus(this, "configured");

        Toast.makeText(this, "Backend configuration saved successfully", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateStatusDisplay(boolean success, String message) {
        tv_status.setText(message);
        if (success) {
            tv_status.setTextColor(getResources().getColor(R.color.dull_success));
            btn_test_and_save.setEnabled(true);
            btn_save_config.setEnabled(true);
        } else {
            tv_status.setTextColor(getResources().getColor(R.color.dull_error));
            btn_test_and_save.setEnabled(true);
            btn_save_config.setEnabled(false);
        }
    }
}
