package com.example.mapboxkotlin.data.repository

import com.example.mapboxkotlin.data.model.SdsmData
import com.example.mapboxkotlin.data.model.SdsmObject
import com.example.mapboxkotlin.data.network.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class SdsmRepository {
    private val apiService = NetworkModule.apiService
    private var lastDataHash: Int = 0
    private var lastTimestamp: String = ""

    /**
     * Monitors for changes in SDSM data and emits only when changes are detected.
     * Uses exponential backoff when no changes are detected to reduce network usage.
     */
    fun getSdsmUpdates(): Flow<SdsmData> = flow {
        var backoffDelay = 100L  // Start with 100ms
        val maxBackoff = 2000L    // Max 2 seconds between checks
        val minBackoff = 100L     // Min 100ms for responsive updates

        while (true) {
            try {
                val data = apiService.getLatestSdsmEvents()

                // Create a hash of the current data to detect changes
                val currentHash = computeDataHash(data)
                val hasChanged = currentHash != lastDataHash || data.timestamp != lastTimestamp

                if (hasChanged) {
                    // Data changed - emit update and reset backoff
                    lastDataHash = currentHash
                    lastTimestamp = data.timestamp
                    emit(data)
                    backoffDelay = minBackoff  // Reset to minimum delay
                } else {
                    // No change - increase backoff
                    backoffDelay = (backoffDelay * 1.5).toLong().coerceAtMost(maxBackoff)
                }

            } catch (e: Exception) {
                // On error, use moderate backoff
                backoffDelay = 1000L
            }

            delay(backoffDelay)
        }
    }

    /**
     * Computes a hash of the SDSM data to detect changes efficiently.
     * Considers object positions, headings, and speeds.
     */
    private fun computeDataHash(data: SdsmData): Int {
        var hash = data.intersectionId.hashCode()
        hash = 31 * hash + data.objects.size

        data.objects.forEach { obj ->
            hash = 31 * hash + obj.objectId
            hash = 31 * hash + obj.type.hashCode()
            hash = 31 * hash + obj.location.coordinates.hashCode()
            hash = 31 * hash + obj.heading
            hash = 31 * hash + obj.speed
        }

        return hash
    }
}