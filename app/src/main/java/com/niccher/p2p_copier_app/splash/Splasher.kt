package com.niccher.p2p_copier_app.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.niccher.p2p_copier_app.R
import com.niccher.p2p_copier_app.activities.Auth_New_Or_Continue
import com.niccher.p2p_copier_app.activities.BiometricLockActivity
import com.niccher.p2p_copier_app.activities.Regista
import com.niccher.p2p_copier_app.utils.BiometricHelper
import com.niccher.p2p_copier_app.utils.Konstants
import com.niccher.p2p_copier_app.utils.SharedPrefs

class Splasher : AppCompatActivity() {

    private val SPLASH_DURATION_MS = 2000L
    private lateinit var progressBar: LinearProgressIndicator
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var kon: Konstants

    private val navigationRunnable = Runnable { navigateNext() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splasher)

        progressBar = findViewById(R.id.progress_bar)
        kon = Konstants()

        startSplash()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(navigationRunnable)
    }

    private fun startSplash() {
        handler.postDelayed(navigationRunnable, SPLASH_DURATION_MS)
    }

    private fun navigateNext() {
        if (isAuthenticated()) {
            val biometricHelper = BiometricHelper(this)
            val sharedPrefs = SharedPrefs(this)
            val isBiometricEnabled = sharedPrefs.getBoolean("biometric_enabled", true)

            if (isBiometricEnabled && biometricHelper.isBiometricAvailable()) {
                proceedToIntent(Intent(this, BiometricLockActivity::class.java))
            } else {
                // Biometrics not available or disabled, proceed anyway
                proceedToIntent(Intent(this, Auth_New_Or_Continue::class.java))
            }
        } else {
            proceedToIntent(Intent(this, Regista::class.java))
        }
    }

    private fun proceedToIntent(intent: Intent) {
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
        finish()
    }

    /**
     * Authentication check with backward-compatible migration.
     * Supports legacy String values ("True"/"False") and migrates to boolean.
     */
    private fun isAuthenticated(): Boolean {
        val prefs = getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE)
        val allPrefs = prefs.all
        val rawValue = allPrefs["auth_status"]

        if (rawValue is Boolean) {
            return rawValue
        }

        if (rawValue is String) {
            val migratedValue = "true".equals(rawValue, ignoreCase = true)

            // Migrate to proper boolean
            prefs.edit()
                .putBoolean("auth_status", migratedValue)
                .apply()

            return migratedValue
        }

        // Default: not authenticated
        return false
    }
}
