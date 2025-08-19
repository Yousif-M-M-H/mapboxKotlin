package com.example.mapboxkotlin.presentation.map

import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.example.mapboxkotlin.data.model.SdsmObject

class MarkerManager(private val mapView: MapView) {
    private val circleAnnotationManager: CircleAnnotationManager =
        mapView.annotations.createCircleAnnotationManager()

    // Cache for existing annotations with their last known state
    private val annotations = mutableMapOf<Int, AnnotationState>()

    private data class AnnotationState(
        var annotation: CircleAnnotation,
        var lastLat: Double,
        var lastLng: Double,
        var lastType: String
    )

    init {
        // Disable overlap to improve performance
        circleAnnotationManager.circleTranslate = listOf(0.0, 0.0)
    }

    /**
     * Efficiently updates only the markers that have changed.
     * Minimizes Mapbox API calls for better performance.
     */
    fun updateMarkers(objects: Map<Int, SdsmObject>) {
        val currentIds = objects.keys
        val existingIds = annotations.keys

        // Find changes
        val toRemove = existingIds - currentIds
        val toAdd = currentIds - existingIds
        val toUpdate = currentIds.intersect(existingIds)

        // Batch operations for efficiency
        val annotationsToDelete = mutableListOf<CircleAnnotation>()
        val annotationsToCreate = mutableListOf<CircleAnnotationOptions>()

        // Remove old markers
        toRemove.forEach { id ->
            annotations[id]?.let { state ->
                annotationsToDelete.add(state.annotation)
                annotations.remove(id)
            }
        }

        // Update existing markers only if position or type changed
        toUpdate.forEach { id ->
            val sdsmObject = objects[id]!!
            val state = annotations[id]!!
            val point = sdsmObject.location.toMapboxPoint()

            // Check if position or type changed
            if (point.latitude() != state.lastLat ||
                point.longitude() != state.lastLng ||
                sdsmObject.type != state.lastType) {

                // Delete old annotation and create new one if type changed
                if (sdsmObject.type != state.lastType) {
                    annotationsToDelete.add(state.annotation)
                    annotationsToCreate.add(createAnnotationOptions(sdsmObject))
                    state.lastType = sdsmObject.type
                } else {
                    // Just update position
                    state.annotation.point = point
                    circleAnnotationManager.update(state.annotation)
                }

                state.lastLat = point.latitude()
                state.lastLng = point.longitude()
            }
        }

        // Add new markers
        toAdd.forEach { id ->
            val sdsmObject = objects[id]!!
            annotationsToCreate.add(createAnnotationOptions(sdsmObject))
        }

        // Perform batch operations
        if (annotationsToDelete.isNotEmpty()) {
            circleAnnotationManager.delete(annotationsToDelete)
        }

        if (annotationsToCreate.isNotEmpty()) {
            val newAnnotations = circleAnnotationManager.create(annotationsToCreate)

            // Update cache for type-changed markers
            var createIndex = 0
            toUpdate.forEach { id ->
                val sdsmObject = objects[id]!!
                val state = annotations[id]
                if (state != null && annotationsToDelete.contains(state.annotation)) {
                    // This was recreated due to type change
                    val point = sdsmObject.location.toMapboxPoint()
                    state.annotation = newAnnotations[createIndex++]
                }
            }

            // Store new annotations
            toAdd.forEach { id ->
                val sdsmObject = objects[id]!!
                val point = sdsmObject.location.toMapboxPoint()
                annotations[id] = AnnotationState(
                    annotation = newAnnotations[createIndex++],
                    lastLat = point.latitude(),
                    lastLng = point.longitude(),
                    lastType = sdsmObject.type
                )
            }
        }
    }

    private fun createAnnotationOptions(sdsmObject: SdsmObject): CircleAnnotationOptions {
        val point = sdsmObject.location.toMapboxPoint()

        return CircleAnnotationOptions()
            .withPoint(point)
            .withCircleRadius(getRadiusForType(sdsmObject.type))
            .withCircleColor(getColorForType(sdsmObject.type))
            .withCircleOpacity(0.85)
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#FFFFFF")
            .withCircleStrokeOpacity(1.0)
    }

    private fun getColorForType(type: String): String {
        return when (type) {
            "vehicle" -> "#0066CC"  // Blue for vehicles
            "vru" -> "#CC0000"      // Red for VRUs
            else -> "#666666"        // Gray for unknown
        }
    }

    private fun getRadiusForType(type: String): Double {
        return when (type) {
            "vehicle" -> 8.0
            "vru" -> 6.0
            else -> 5.0
        }
    }

    fun clear() {
        circleAnnotationManager.deleteAll()
        annotations.clear()
    }
}