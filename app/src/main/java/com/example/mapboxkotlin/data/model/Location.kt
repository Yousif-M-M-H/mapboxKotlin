package com.example.mapboxkotlin.data.model

import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Point

data class Location(
    @SerializedName("type") val type: String,
    @SerializedName("coordinates") val coordinates: List<Double>
) {
    // Convert to Mapbox Point (longitude, latitude order)
    fun toMapboxPoint(): Point {
        // Input is [latitude, longitude], Mapbox needs [longitude, latitude]
        return Point.fromLngLat(coordinates[1], coordinates[0])
    }
}