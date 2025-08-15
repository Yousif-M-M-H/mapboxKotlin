package com.example.mapboxkotlin.data.repository

import com.example.mapboxkotlin.data.model.SdsmData
import com.example.mapboxkotlin.data.network.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class SdsmRepository {
    private val apiService = NetworkModule.apiService

    fun getSdsmUpdates(intervalMs: Long = 1000): Flow<SdsmData> = flow {
        while (true) {
            try {
                val data = apiService.getLatestSdsmEvents()
                emit(data)
            } catch (e: Exception) {
                // Log error but continue polling
                e.printStackTrace()
            }
            delay(intervalMs)
        }
    }
}