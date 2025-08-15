package com.example.mapboxkotlin.presentation.map

import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.example.mapboxkotlin.data.model.SdsmObject

class MarkerManager(private val mapView: MapView) {  // ← Changed to MapView
    private val pointAnnotationManager: PointAnnotationManager =
        mapView.annotations.createPointAnnotationManager()  // ← Use mapView.annotations

    // Cache for existing annotations
    private val annotations = mutableMapOf<Int, PointAnnotation>()

    init {
        // Enable clustering for better performance with many markers
        pointAnnotationManager.textAllowOverlap = false
        pointAnnotationManager.iconAllowOverlap = false
    }

    fun updateMarkers(objects: Map<Int, SdsmObject>) {
        // Remove markers that no longer exist
        val currentIds = objects.keys
        val toRemove = annotations.keys - currentIds

        toRemove.forEach { id ->
            annotations[id]?.let { annotation ->
                pointAnnotationManager.delete(annotation)
                annotations.remove(id)
            }
        }

        // Update or add markers
        objects.forEach { (id, sdsmObject) ->
            val point = sdsmObject.location.toMapboxPoint()

            annotations[id]?.let { existingAnnotation ->
                // Update existing marker position
                existingAnnotation.point = point
                pointAnnotationManager.update(existingAnnotation)
            } ?: run {
                // Create new marker
                val options = PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(getIconForType(sdsmObject.type))
                    .withIconSize(if (sdsmObject.isVehicle) 1.2 else 0.8)
                    .withIconRotate(sdsmObject.heading.toDouble() / 100.0)

                val annotation = pointAnnotationManager.create(options)
                annotations[id] = annotation
            }
        }
    }

    private fun getIconForType(type: String): String {
        return when (type) {
            "vehicle" -> "car-icon"
            "vru" -> "pedestrian-icon"
            else -> "default-icon"
        }
    }

    fun clear() {
        pointAnnotationManager.deleteAll()
        annotations.clear()
    }
}