package com.example.macropp.presentation.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.domain.model.Food
import com.example.macropp.domain.repository.FoodLogRepository
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
class LogFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val foodLogRepository: FoodLogRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogFoodUiState())
    val uiState: StateFlow<LogFoodUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isNotBlank()) {
            searchFoods(query)
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    private fun searchFoods(query: String) {
        val currentUser = userRepository.currentUser.value ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }

            foodRepository.searchFoods(currentUser.id, query)
                .onSuccess { foods ->
                    _uiState.update { it.copy(searchResults = foods, isSearching = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            error = e.message ?: "Search failed"
                        )
                    }
                }
        }
    }

    fun selectFood(food: Food) {
        _uiState.update {
            it.copy(
                selectedFood = food,
                quantityGrams = food.servingSizeGrams?.toString() ?: "100"
            )
        }
    }

    fun clearSelectedFood() {
        _uiState.update { it.copy(selectedFood = null, quantityGrams = "") }
    }

    fun updateQuantity(quantity: String) {
        _uiState.update { it.copy(quantityGrams = quantity) }
    }

    fun logFood() {
        val currentUser = userRepository.currentUser.value
        val selectedFood = _uiState.value.selectedFood
        val quantity = _uiState.value.quantityGrams.toBigDecimalOrNull()

        if (currentUser == null) {
            _uiState.update { it.copy(error = "No user logged in") }
            return
        }
        if (selectedFood == null) {
            _uiState.update { it.copy(error = "No food selected") }
            return
        }
        if (quantity == null || quantity <= BigDecimal.ZERO) {
            _uiState.update { it.copy(error = "Invalid quantity") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            foodLogRepository.createFoodLog(
                userId = currentUser.id,
                foodId = selectedFood.id,
                quantityGrams = quantity,
                loggedAt = null
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            logSuccess = true,
                            selectedFood = null,
                            quantityGrams = ""
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to log food"
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? {
        return if (isBlank()) null else try {
            BigDecimal(this)
        } catch (e: NumberFormatException) {
            null
        }
    }
}
