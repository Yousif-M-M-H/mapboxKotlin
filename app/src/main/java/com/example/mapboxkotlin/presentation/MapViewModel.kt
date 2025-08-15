package com.example.mapboxkotlin.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapboxkotlin.data.model.SdsmObject
import com.example.mapboxkotlin.data.domain.usecase.GetSdsmUpdatesUseCase  // ← Fixed import path
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val getSdsmUpdatesUseCase = GetSdsmUpdatesUseCase()

    private val _sdsmObjects = MutableStateFlow<Map<Int, SdsmObject>>(emptyMap())
    val sdsmObjects: StateFlow<Map<Int, SdsmObject>> = _sdsmObjects.asStateFlow()

    fun startUpdates() {
        viewModelScope.launch {
            getSdsmUpdatesUseCase.execute()
                .collect { objects: Map<Int, SdsmObject> ->  // ← Explicitly specify type
                    _sdsmObjects.value = objects
                }
        }
    }
}