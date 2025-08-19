package com.example.mapboxkotlin.data.model

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Point

data class Location(
    @SerializedName("type") val type: String,
    @SerializedName("coordinates") val coordinates: List<Double>
) {
    // Convert to Mapbox Point (longitude, latitude order)
    fun toMapboxPoint(): Point {
        // API sends [latitude, longitude], Mapbox needs [longitude, latitude]
        val lat = coordinates[0]
        val lng = coordinates[1]

        Log.d("Location", "Converting coordinates: API=[lat=$lat, lng=$lng] -> Mapbox=[lng=$lng, lat=$lat]")

        // Create Mapbox Point with longitude first, latitude second
        return Point.fromLngLat(lng, lat)
    }
}