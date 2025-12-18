package com.example.macropp.presentation.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.domain.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateFoodUiState())
    val uiState: StateFlow<CreateFoodUiState> = _uiState.asStateFlow()

    fun updateFoodName(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                formData = currentState.formData.copy(name = name)
            )
        }
    }

    fun updateCalories(calories: String) {
        _uiState.update { currentState ->
            currentState.copy(
                formData = currentState.formData.copy(caloriesPer100Grams = calories)
            )
        }
    }

    fun updateProtein(protein: String) {
        _uiState.update { currentState ->
            currentState.copy(
                formData = currentState.formData.copy(proteinPer100Grams = protein)
            )
        }
    }

    fun updateCarbs(carbs: String) {
        _uiState.update { currentState ->
            currentState.copy(
                formData = currentState.formData.copy(carbsPer100Grams = carbs)
            )
        }
    }

    fun updateFats(fats: String) {
        _uiState.update { currentState ->
            currentState.copy(
                formData = currentState.formData.copy(fatsPer100Grams = fats)
            )
        }
    }

    fun updateServingSize(servingSize: String) {
        _uiState.update { currentState ->
            currentState.copy(
                formData = currentState.formData.copy(servingSizeGrams = servingSize)
            )
        }
    }

    fun updateBarcode(barcode: String) {
        _uiState.update { currentState ->
            currentState.copy(
                formData = currentState.formData.copy(barcode = barcode)
            )
        }
    }

    fun saveFood() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Call your repository to save the food
                // repository.createFood(_uiState.value.formData)

                // Handle success (maybe navigate back or show success message)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to save food"
                    )
                }
            }
        }
    }
}