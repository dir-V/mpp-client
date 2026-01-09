package com.example.macropp.presentation.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateFoodRequest
import com.example.macropp.data.session.SessionManager
import com.example.macropp.domain.repository.FoodRepository
import com.example.macropp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateFoodUiState())
    val uiState: StateFlow<CreateFoodUiState> = _uiState.asStateFlow()

    // LOGIC TO FIND CURRENT USER USING SESSION MANAGER
    private fun withUser(action: suspend (UUID) -> Unit) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != null) {
                action(userId)
            } else {
                // Handle logged out state
            }
        }
    }

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
        val formData = _uiState.value.formData
        withUser { userId ->
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val request = CreateFoodRequest(
                    userId = userId,
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
    }

       private fun String.toBigDecimalOrNull(): BigDecimal? {
            return if (isBlank()) null else try {
                BigDecimal(this)
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
