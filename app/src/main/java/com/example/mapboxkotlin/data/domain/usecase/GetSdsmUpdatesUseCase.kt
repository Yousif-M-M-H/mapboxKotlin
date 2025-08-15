package com.example.mapboxkotlin.data.domain.usecase

import com.example.mapboxkotlin.data.model.SdsmData
import com.example.mapboxkotlin.data.model.SdsmObject
import com.example.mapboxkotlin.data.repository.SdsmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSdsmUpdatesUseCase(
    private val repository: SdsmRepository = SdsmRepository()
) {
    // Returns a flow of Map<objectId, SdsmObject> for efficient updates
    fun execute(): Flow<Map<Int, SdsmObject>> =
        repository.getSdsmUpdates()
            .map { sdsmData: SdsmData ->  // ← Explicitly specify type here
                sdsmData.objects.associateBy { it.objectId }
            }
}