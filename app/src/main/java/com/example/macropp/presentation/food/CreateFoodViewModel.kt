package com.example.macropp.presentation.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateFoodRequest
import com.example.macropp.domain.repository.FoodRepository
import com.example.macropp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
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
        val currentUser = userRepository.currentUser.value
        if (currentUser == null) {
            _uiState.update { it.copy(error = "No user logged in") }
            return
        }

        val formData = _uiState.value.formData
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val request = CreateFoodRequest(
                userId = currentUser.id,
                name = formData.name,
                caloriesPer100Grams = formData.caloriesPer100Grams.toIntOrNull() ?: 0,
                proteinPer100Grams = formData.proteinPer100Grams.toBigDecimalOrNull(),
                carbsPer100Grams = formData.carbsPer100Grams.toBigDecimalOrNull(),
                fatsPer100Grams = formData.fatsPer100Grams.toBigDecimalOrNull(),
                servingSizeGrams = formData.servingSizeGrams.toBigDecimalOrNull(),
                barcode = formData.barcode.toLongOrNull()
            )

            foodRepository.createFood(request)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to save food"
                        )
                    }
                }
        }
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? {
        return if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
    }
}