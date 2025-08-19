package com.example.mapboxkotlin.data.network

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val BASE_URL = "http://roadaware.cuip.research.utc.edu/"
    private const val TAG = "NetworkModule"

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val apiService: SdsmApiService by lazy {
        Log.d(TAG, "Creating API service with base URL: $BASE_URL")
        Log.d(TAG, "Full endpoint will be: ${BASE_URL}cv2x/latest/sdsm_events")

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SdsmApiService::class.java)
    }
}