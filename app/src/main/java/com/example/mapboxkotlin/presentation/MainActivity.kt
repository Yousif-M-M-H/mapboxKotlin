package com.example.mapboxkotlin.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mapbox.maps.MapView
import com.example.mapboxkotlin.R
import com.example.mapboxkotlin.presentation.map.MapManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var mapManager: MapManager
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapManager = MapManager(mapView, this)

        mapManager.initialize {
            observeUpdates()
        }
    }

    private fun observeUpdates() {
        lifecycleScope.launch {
            // Only collect updates when the activity is at least STARTED
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Start fetching updates when visible
                viewModel.startUpdates()

                // Observe and update map with changes only
                viewModel.sdsmObjects.collect { objects ->
                    mapManager.updateObjects(objects)
                }
            }
            // Updates automatically stop when lifecycle goes below STARTED
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapManager.onDestroy()
        mapView.onDestroy()
    }
}