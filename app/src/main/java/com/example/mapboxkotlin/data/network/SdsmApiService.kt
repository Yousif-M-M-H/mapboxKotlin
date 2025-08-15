package com.example.mapboxkotlin.data.network

import com.example.mapboxkotlin.data.model.SdsmData
import retrofit2.http.GET

interface SdsmApiService {
    @GET("cv2x/latest/sdsm_events")
    suspend fun getLatestSdsmEvents(): SdsmData
}