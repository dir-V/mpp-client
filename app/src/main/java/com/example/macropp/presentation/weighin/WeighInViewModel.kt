package com.example.macropp.presentation.weighin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.remote.dto.CreateWeighInRequest
import com.example.macropp.domain.repository.WeighInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeighInViewModel @Inject constructor(
    private val weighInRepository: WeighInRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeighInUiState())
    val uiState = _uiState.asStateFlow()

    fun onWeightChange(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun onSubmit() {
        var weightValue = uiState.value.weight
        if (weightValue != null) {
            weightValue = weightValue.toDouble()
            return
        }
        else { _uiState.update { it.copy(error = "Invalid weight") }


        viewModelScope.launch {

            val request = CreateWeighInRequest(
                weight_kg = uiState.value.weight,
                weight_date = ""
            )

            _uiState.update { it.copy(isLoading = true) }
            try {
                weighInRepository.createWeighIn(request)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
