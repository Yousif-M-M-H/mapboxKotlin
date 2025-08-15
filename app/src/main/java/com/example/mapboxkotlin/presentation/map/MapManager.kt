package com.example.mapboxkotlin.presentation.map

import android.content.Context
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.example.mapboxkotlin.R
import com.example.mapboxkotlin.data.model.SdsmObject

class MapManager(
    private val mapView: MapView,
    private val context: Context
) {
    private lateinit var markerManager: MarkerManager

    fun initialize(onMapReady: () -> Unit) {
        mapView.mapboxMap.apply {  // ← Changed from getMapboxMap()
            markerManager = MarkerManager(mapView)  // ← Pass mapView instead

            // Load style with custom icons
            loadStyleUri(
                Style.MAPBOX_STREETS,
                { style ->
                    // Add custom icons to style (optional - can comment out for now)
                    // style.addImage("car-icon",
                    //     context.getDrawable(R.drawable.ic_car)!!)
                    // style.addImage("pedestrian-icon",
                    //     context.getDrawable(R.drawable.ic_pedestrian)!!)

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
        markerManager.updateMarkers(objects)
    }

    fun onDestroy() {
        markerManager.clear()
    }
}