package com.example.macropp.presentation.weighin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.remote.dto.CreateWeighInRequest
import com.example.macropp.domain.repository.WeighInRepository
import com.example.macropp.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeighInViewModel @Inject constructor(
    private val weighInRepository: WeighInRepository,
    public val sessionManager: SessionManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId == null) {
                // If no user ID is found, update state to block UI or trigger navigation
                _uiState.update {
                    it.copy(error = "User session expired. Please log in again.")
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(WeighInUiState())
    val uiState = _uiState.asStateFlow()

    fun onWeightChange(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun onSubmit() {
        val weightValue = uiState.value
//        if (weightValue != null) {
//            weightValue = weightValue
//            return
//        }
//        else { _uiState.update { it.copy(error = "Invalid weight") }



        viewModelScope.launch {

            val userId = sessionManager.getUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not logged in.") }
                return@launch
            }

            val request = CreateWeighInRequest(
                userId = userId.toString(),
                weightKg = uiState.value.weight.toBigDecimal(),
                weightDate = LocalDate.now().toString()
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
