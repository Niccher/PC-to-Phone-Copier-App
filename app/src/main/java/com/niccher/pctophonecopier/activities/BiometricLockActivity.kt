package com.niccher.pctophonecopier.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.niccher.pctophonecopier.R
import com.niccher.pctophonecopier.utils.BiometricHelper

class BiometricLockActivity : AppCompatActivity() {

    private lateinit var biometricHelper: BiometricHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometric_lock)

        biometricHelper = BiometricHelper(this)

        findViewById<Button>(R.id.btn_unlock).setOnClickListener {
            startAuthentication()
        }

        // Prevent back button from bypassing lock
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Move task to back instead of allowing back navigation
                moveTaskToBack(true)
            }
        })

        // Auto-trigger on start
        startAuthentication()
    }

    private fun startAuthentication() {
        val sharedPrefs = com.niccher.pctophonecopier.utils.SharedPrefs(this)
        val isBiometricEnabled = sharedPrefs.getBoolean("biometric_enabled", true)

        if (isBiometricEnabled && biometricHelper.isBiometricAvailable()) {
            biometricHelper.showBiometricPrompt(
                listener = object : BiometricHelper.BiometricAuthListener {
                    override fun onAuthSuccess() {
                        navigateToMain()
                    }

                    override fun onAuthError(errorCode: Int, errString: CharSequence) {
                        // Keep on this screen if error or canceled
                    }

                    override fun onAuthFailed() {
                        // System UI handles this usually
                    }
                }
            )
        } else {
            // If biometrics/PIN missing or disabled, proceed
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, Auth_New_Or_Continue::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
        finish()
    }
}
