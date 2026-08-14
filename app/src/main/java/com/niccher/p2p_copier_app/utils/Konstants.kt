package com.niccher.p2p_copier_app.utils

import android.content.Context

class Konstants {

    @JvmField
    var str_base_url: String = active_base_url

    @JvmField
    var str_device_action: String = "${active_base_url.trimEnd('/')}/device/"

    @JvmField
    var str_auth_action: String = "${active_base_url.trimEnd('/')}/auth/"

    @JvmField
    var str_file_list_uploaded: String = active_base_url

    @JvmField
    var str_file_upload_action: String = active_base_url

    @JvmField
    var TAGGED: String = "P2P_Copier"

    @JvmField
    var Splash_Time: Int = 1500

    @JvmField
    var shared_pref_auth: String = "s_p_auth"

    @JvmField
    var shared_pref_device: String = "s_p_device"

    companion object {
        const val PREFS_NAME = "p2p_copier_prefs"
        const val KEY_BACKEND_URL = "backend_url"
        const val KEY_BACKEND_PORT = "backend_port"
        const val KEY_BACKEND_CONFIGURED = "backend_configured"
        const val DEFAULT_BACKEND_URL = "https://p2p.chegecache.co.ke"
        const val DEFAULT_BACKEND_PORT = "443"

        @JvmStatic
        var active_base_url: String = DEFAULT_BACKEND_URL
            private set

        @JvmStatic
        fun loadBackendConfig(context: Context) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedUrl = prefs.getString(KEY_BACKEND_URL, DEFAULT_BACKEND_URL) ?: DEFAULT_BACKEND_URL
            val savedPort = prefs.getString(KEY_BACKEND_PORT, DEFAULT_BACKEND_PORT) ?: DEFAULT_BACKEND_PORT

            active_base_url = buildBackendUrl(savedUrl, savedPort)
        }

        @JvmStatic
        fun saveBackendConfig(context: Context, url: String, port: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            var cleanHost = url.trim()
            if (cleanHost.startsWith("http://")) cleanHost = cleanHost.substring(7)
            if (cleanHost.startsWith("https://")) cleanHost = cleanHost.substring(8)
            if (cleanHost.contains(":")) cleanHost = cleanHost.substringBefore(":")
            if (cleanHost.contains("/")) cleanHost = cleanHost.substringBefore("/")

            prefs.edit()
                .putString(KEY_BACKEND_URL, cleanHost)
                .putString(KEY_BACKEND_PORT, port.trim())
                .putBoolean(KEY_BACKEND_CONFIGURED, true)
                .apply()
            loadBackendConfig(context)
        }

        @JvmStatic
        fun isBackendConfigured(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_BACKEND_CONFIGURED, false)
        }

        @JvmStatic
        fun getBackendUrl(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_BACKEND_URL, DEFAULT_BACKEND_URL) ?: DEFAULT_BACKEND_URL
        }

        @JvmStatic
        fun getBackendPort(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_BACKEND_PORT, DEFAULT_BACKEND_PORT) ?: DEFAULT_BACKEND_PORT
        }

        @JvmStatic
        fun getBaseUrl(context: Context): String {
            loadBackendConfig(context)
            return active_base_url
        }

        @JvmStatic
        fun getBackendStatus(context: Context): String {
            return prefsGetString(context, "backend_status", "unknown")
        }

        @JvmStatic
        fun saveBackendStatus(context: Context, status: String) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString("backend_status", status)
                .apply()
        }

        @JvmStatic
        private fun prefsGetString(context: Context, key: String, default: String): String {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(key, default) ?: default
        }

        @JvmStatic
        private fun buildBackendUrl(url: String, port: String): String {
            var rawHost = url.trim()
            var scheme = "http"
            if (rawHost.startsWith("https://")) {
                scheme = "https"
                rawHost = rawHost.substring(8)
            } else if (rawHost.startsWith("http://")) {
                scheme = "http"
                rawHost = rawHost.substring(7)
            }

            if (rawHost.contains("/")) {
                rawHost = rawHost.substringBefore("/")
            }

            var hostOnly = rawHost
            var parsedPort = port.trim().toIntOrNull()

            if (rawHost.contains(":")) {
                hostOnly = rawHost.substringBefore(":")
                val portInHost = rawHost.substringAfter(":").toIntOrNull()
                if (portInHost != null) {
                    parsedPort = portInHost
                }
            }

            if (hostOnly.isEmpty()) {
                hostOnly = "p2p.chegecache.co.ke"
            }

            val portInt = parsedPort ?: (if (scheme == "https") 443 else 80)

            val base = if ((scheme == "https" && portInt == 443) || (scheme == "http" && portInt == 80)) {
                "$scheme://$hostOnly"
            } else {
                "$scheme://$hostOnly:$portInt"
            }

            return if (base.endsWith("/")) base else "$base/"
        }
    }
}
