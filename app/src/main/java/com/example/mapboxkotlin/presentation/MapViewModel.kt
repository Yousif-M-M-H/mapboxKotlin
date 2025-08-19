package com.example.mapboxkotlin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapboxkotlin.data.model.SdsmObject
import com.example.mapboxkotlin.data.domain.usecase.GetSdsmUpdatesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val getSdsmUpdatesUseCase = GetSdsmUpdatesUseCase()

    private val _sdsmObjects = MutableStateFlow<Map<Int, SdsmObject>>(emptyMap())
    val sdsmObjects: StateFlow<Map<Int, SdsmObject>> = _sdsmObjects.asStateFlow()

    private val _updateCount = MutableStateFlow(0L)
    val updateCount: StateFlow<Long> = _updateCount.asStateFlow()

    private var updateJob: Job? = null

    fun startUpdates() {
        // Cancel any existing job before starting a new one
        updateJob?.cancel()

        updateJob = viewModelScope.launch {
            getSdsmUpdatesUseCase.execute()
                .distinctUntilChanged() // Additional deduplication at ViewModel level
                .collect { objects ->
                    // Only emit if the data is actually different
                    if (_sdsmObjects.value != objects) {
                        _sdsmObjects.value = objects
                        _updateCount.value++
                    }
                }
        }
    }

    fun stopUpdates() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdates()
    }
}