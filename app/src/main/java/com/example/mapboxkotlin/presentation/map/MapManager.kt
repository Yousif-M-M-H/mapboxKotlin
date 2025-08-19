package com.example.mapboxkotlin.presentation.map

import android.content.Context
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.example.mapboxkotlin.data.model.SdsmObject

class MapManager(
    private val mapView: MapView,
    private val context: Context
) {
    private lateinit var markerManager: MarkerManager

    fun initialize(onMapReady: () -> Unit) {
        mapView.mapboxMap.apply {
            markerManager = MarkerManager(mapView)

            // Load style without custom icons
            loadStyleUri(
                Style.MAPBOX_STREETS,
                { style ->
                    // Set initial camera position (Chattanooga area)
                    setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(-85.3083, 35.0457))
                            .zoom(17.0)
                            .build()
                    )

                    onMapReady()
                }
            )
        }
    }

    fun updateObjects(objects: Map<Int, SdsmObject>) {
        // Pass ALL objects to markerManager - no filtering
        markerManager.updateMarkers(objects)
    }

    fun onDestroy() {
        markerManager.clear()
    }
}