package com.niccher.p2p_copier_app.datastore

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
        private val AUTH_CODE_ID = stringPreferencesKey("auth_auth_code_id")
        private val AUTH_CODE = stringPreferencesKey("auth_auth_code")
        private val AUTH_TYPE = stringPreferencesKey("auth_type")
        private val AUTH_STATUS = stringPreferencesKey("auth_status")
        private val AUTH_MESSAGE = stringPreferencesKey("auth_message")
        private val AUTH_TIME = stringPreferencesKey("auth_time")
    }

    val authCodeId: Flow<String> = context.authDataStore.data.map { it[AUTH_CODE_ID] ?: "" }
    val authCode: Flow<String> = context.authDataStore.data.map { it[AUTH_CODE] ?: "" }
    val authType: Flow<String> = context.authDataStore.data.map { it[AUTH_TYPE] ?: "" }
    val authStatus: Flow<String> = context.authDataStore.data.map { it[AUTH_STATUS] ?: "" }
    val authMessage: Flow<String> = context.authDataStore.data.map { it[AUTH_MESSAGE] ?: "" }
    val authTime: Flow<String> = context.authDataStore.data.map { it[AUTH_TIME] ?: "" }

    suspend fun saveAuthCodeId(codeId: String) {
        context.authDataStore.edit { it[AUTH_CODE_ID] = codeId }
    }

    suspend fun saveAuthCode(code: String) {
        context.authDataStore.edit { it[AUTH_CODE] = code }
    }

    suspend fun saveAuthType(type: String) {
        context.authDataStore.edit { it[AUTH_TYPE] = type }
    }

    suspend fun saveAuthStatus(status: String) {
        context.authDataStore.edit { it[AUTH_STATUS] = status }
    }

    suspend fun saveAuthMessage(message: String) {
        context.authDataStore.edit { it[AUTH_MESSAGE] = message }
    }

    suspend fun saveAuthTime(time: String) {
        context.authDataStore.edit { it[AUTH_TIME] = time }
    }

    // Synchronous methods for Java compatibility
    fun getAuthCodeIdSync(): String = runBlocking { authCodeId.first() }
    fun getAuthCodeSync(): String = runBlocking { authCode.first() }
    fun getAuthTypeSync(): String = runBlocking { authType.first() }
    fun getAuthStatusSync(): String = runBlocking { authStatus.first() }
    fun getAuthMessageSync(): String = runBlocking { authMessage.first() }
    fun getAuthTimeSync(): String = runBlocking { authTime.first() }

    fun saveAuthCodeIdSync(codeId: String) = runBlocking { saveAuthCodeId(codeId) }
    fun saveAuthCodeSync(code: String) = runBlocking { saveAuthCode(code) }
    fun saveAuthTypeSync(type: String) = runBlocking { saveAuthType(type) }
    fun saveAuthStatusSync(status: String) = runBlocking { saveAuthStatus(status) }
    fun saveAuthMessageSync(message: String) = runBlocking { saveAuthMessage(message) }
    fun saveAuthTimeSync(time: String) = runBlocking { saveAuthTime(time) }

    fun getStringSync(key: String, defaultValue: String = ""): String {
        return when (key) {
            "auth_auth_code_id" -> getAuthCodeIdSync().ifEmpty { defaultValue }
            "auth_auth_code" -> getAuthCodeSync().ifEmpty { defaultValue }
            "auth_type" -> getAuthTypeSync().ifEmpty { defaultValue }
            "auth_status" -> getAuthStatusSync().ifEmpty { defaultValue }
            "auth_message" -> getAuthMessageSync().ifEmpty { defaultValue }
            "auth_time" -> getAuthTimeSync().ifEmpty { defaultValue }
            else -> defaultValue
        }
    }

    fun saveStringSync(key: String, value: String) {
        when (key) {
            "auth_auth_code_id" -> saveAuthCodeIdSync(value)
            "auth_auth_code" -> saveAuthCodeSync(value)
            "auth_type" -> saveAuthTypeSync(value)
            "auth_status" -> saveAuthStatusSync(value)
            "auth_message" -> saveAuthMessageSync(value)
            "auth_time" -> saveAuthTimeSync(value)
        }
    }
}
