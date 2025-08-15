package com.example.mapboxkotlin.data.model

import com.google.gson.annotations.SerializedName

data class SdsmData(
    @SerializedName("intersectionID") val intersectionId: String,
    @SerializedName("intersection") val intersection: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("objects") val objects: List<SdsmObject>
)