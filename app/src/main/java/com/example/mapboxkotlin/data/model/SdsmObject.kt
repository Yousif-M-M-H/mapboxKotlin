package com.example.mapboxkotlin.data.model

import com.google.gson.annotations.SerializedName

data class SdsmObject(
    @SerializedName("objectID") val objectId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("location") val location: Location,
    @SerializedName("heading") val heading: Int,
    @SerializedName("speed") val speed: Int,
    @SerializedName("size") val size: Size?
) {
    val isVehicle: Boolean get() = type == "vehicle"
    val isVru: Boolean get() = type == "vru"
}

data class Size(
    @SerializedName("width") val width: Int?,
    @SerializedName("length") val length: Int?
)
