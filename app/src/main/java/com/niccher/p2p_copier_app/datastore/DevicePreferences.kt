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
        private val DEVICE_UUID = stringPreferencesKey("dev_uuid")
        private val DEVICE_STATUS = stringPreferencesKey("dev_status")
        private val DEVICE_MESSAGE = stringPreferencesKey("dev_message")
        private val DEVICE_TIME = stringPreferencesKey("dev_time")
    }

    val deviceUuid: Flow<String> = context.dataStore.data.map { it[DEVICE_UUID] ?: "" }
    val deviceStatus: Flow<String> = context.dataStore.data.map { it[DEVICE_STATUS] ?: "" }
    val deviceMessage: Flow<String> = context.dataStore.data.map { it[DEVICE_MESSAGE] ?: "" }
    val deviceTime: Flow<String> = context.dataStore.data.map { it[DEVICE_TIME] ?: "" }

    suspend fun saveDeviceUuid(uuid: String) {
        context.dataStore.edit { it[DEVICE_UUID] = uuid }
    }

    suspend fun saveDeviceStatus(status: String) {
        context.dataStore.edit { it[DEVICE_STATUS] = status }
    }

    suspend fun saveDeviceMessage(message: String) {
        context.dataStore.edit { it[DEVICE_MESSAGE] = message }
    }

    suspend fun saveDeviceTime(time: String) {
        context.dataStore.edit { it[DEVICE_TIME] = time }
    }

    // Synchronous versions for compatibility
    fun getDeviceUuidSync(): String = runBlocking { deviceUuid.first() }
    fun getDeviceStatusSync(): String = runBlocking { deviceStatus.first() }
    fun getDeviceMessageSync(): String = runBlocking { deviceMessage.first() }
    fun getDeviceTimeSync(): String = runBlocking { deviceTime.first() }

    fun saveDeviceUuidSync(uuid: String) = runBlocking { saveDeviceUuid(uuid) }
    fun saveDeviceStatusSync(status: String) = runBlocking { saveDeviceStatus(status) }
    fun saveDeviceMessageSync(message: String) = runBlocking { saveDeviceMessage(message) }
    fun saveDeviceTimeSync(time: String) = runBlocking { saveDeviceTime(time) }

    fun getStringSync(key: String, defaultValue: String = ""): String {
        return when (key) {
            "dev_uuid" -> getDeviceUuidSync().ifEmpty { defaultValue }
            "dev_status" -> getDeviceStatusSync().ifEmpty { defaultValue }
            "dev_message" -> getDeviceMessageSync().ifEmpty { defaultValue }
            "dev_time" -> getDeviceTimeSync().ifEmpty { defaultValue }
            else -> defaultValue
        }
    }

    fun saveStringSync(key: String, value: String) {
        when (key) {
            "dev_uuid" -> saveDeviceUuidSync(value)
            "dev_status" -> saveDeviceStatusSync(value)
            "dev_message" -> saveDeviceMessageSync(value)
            "dev_time" -> saveDeviceTimeSync(value)
        }
    }
}
