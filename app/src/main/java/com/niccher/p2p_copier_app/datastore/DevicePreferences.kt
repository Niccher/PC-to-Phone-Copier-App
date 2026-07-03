package com.niccher.p2p_copier_app.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "device_prefs")

class DevicePreferences(private val context: Context) {

    companion object {
        private val DEVICE_UUID = stringPreferencesKey("device_uuid")
        private val DEVICE_STATUS = stringPreferencesKey("device_status")
        private val DEVICE_MESSAGE = stringPreferencesKey("device_message")
    }

    val deviceUuid: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DEVICE_UUID] ?: "undefined"
        }

    val deviceStatus: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DEVICE_STATUS] ?: "undefined"
        }

    val deviceMessage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DEVICE_MESSAGE] ?: "undefined"
        }

    suspend fun saveDeviceUuid(uuid: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_UUID] = uuid
        }
    }

    suspend fun saveDeviceStatus(status: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_STATUS] = status
        }
    }

    suspend fun saveDeviceMessage(message: String) {
        context.dataStore.edit { preferences ->
            preferences[DEVICE_MESSAGE] = message
        }
    }

    // Synchronous versions for compatibility with existing code
    fun getDeviceUuidSync(): String {
        return runBlocking { deviceUuid.first() }
    }

    fun getDeviceStatusSync(): String {
        return runBlocking { deviceStatus.first() }
    }

    fun getDeviceMessageSync(): String {
        return runBlocking { deviceMessage.first() }
    }

    fun saveDeviceUuidSync(uuid: String) {
        runBlocking { saveDeviceUuid(uuid) }
    }

    fun saveDeviceStatusSync(status: String) {
        runBlocking { saveDeviceStatus(status) }
    }

    fun saveDeviceMessageSync(message: String) {
        runBlocking { saveDeviceMessage(message) }
    }

    // Keep the original Flow properties for Kotlin code
    fun getDeviceUuidFlow(): kotlinx.coroutines.flow.Flow<String> = deviceUuid
    fun getDeviceStatusFlow(): kotlinx.coroutines.flow.Flow<String> = deviceStatus
    fun getDeviceMessageFlow(): kotlinx.coroutines.flow.Flow<String> = deviceMessage
}
