package com.niccher.p2p_copier_app.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

class BiometricHelper(private val activity: FragmentActivity) {

    private val executor: Executor = ContextCompat.getMainExecutor(activity)

    interface BiometricAuthListener {
        fun onAuthSuccess()
        fun onAuthError(errorCode: Int, errString: CharSequence)
        fun onAuthFailed()
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    fun showBiometricPrompt(
        title: String = "Biometric Authentication",
        subtitle: String = "Log in using your biometric credential",
        negativeButtonText: String = "Use Account Password",
        listener: BiometricAuthListener
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            // .setNegativeButtonText(negativeButtonText) // Cannot be used if DEVICE_CREDENTIAL is allowed
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                listener.onAuthError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                listener.onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                listener.onAuthFailed()
            }
        })

        biometricPrompt.authenticate(promptInfo)
    }
}
