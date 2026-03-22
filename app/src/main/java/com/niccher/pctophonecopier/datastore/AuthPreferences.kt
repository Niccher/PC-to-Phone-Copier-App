package com.niccher.pctophonecopier.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    companion object {
        private val AUTH_CODE_ID = stringPreferencesKey("auth_code_id")
        private val AUTH_CODE = stringPreferencesKey("auth_code")
        private val AUTH_TYPE = stringPreferencesKey("auth_type")
    }

    val authCodeId: Flow<String> = context.authDataStore.data
        .map { preferences ->
            preferences[AUTH_CODE_ID] ?: "undefined"
        }

    val authCode: Flow<String> = context.authDataStore.data
        .map { preferences ->
            preferences[AUTH_CODE] ?: "undefined"
        }

    val authType: Flow<String> = context.authDataStore.data
        .map { preferences ->
            preferences[AUTH_TYPE] ?: "undefined"
        }

    suspend fun saveAuthCodeId(codeId: String) {
        context.authDataStore.edit { preferences ->
            preferences[AUTH_CODE_ID] = codeId
        }
    }

    suspend fun saveAuthCode(code: String) {
        context.authDataStore.edit { preferences ->
            preferences[AUTH_CODE] = code
        }
    }

    suspend fun saveAuthType(type: String) {
        context.authDataStore.edit { preferences ->
            preferences[AUTH_TYPE] = type
        }
    }

    // Synchronous versions for compatibility with existing code
    fun getAuthCodeIdSync(): String {
        return runBlocking { authCodeId.first() }
    }

    fun getAuthCodeSync(): String {
        return runBlocking { authCode.first() }
    }

    fun getAuthTypeSync(): String {
        return runBlocking { authType.first() }
    }

    fun saveAuthCodeIdSync(codeId: String) {
        runBlocking { saveAuthCodeId(codeId) }
    }

    fun saveAuthCodeSync(code: String) {
        runBlocking { saveAuthCode(code) }
    }

    fun saveAuthTypeSync(type: String) {
        runBlocking { saveAuthType(type) }
    }

    // Keep the original Flow properties for Kotlin code
    fun getAuthCodeIdFlow(): kotlinx.coroutines.flow.Flow<String> = authCodeId
    fun getAuthCodeFlow(): kotlinx.coroutines.flow.Flow<String> = authCode
    fun getAuthTypeFlow(): kotlinx.coroutines.flow.Flow<String> = authType
}
