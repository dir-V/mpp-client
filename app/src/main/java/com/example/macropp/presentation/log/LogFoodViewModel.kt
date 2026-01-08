package com.example.macropp.presentation.log

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class LogFoodViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val foodLogRepository: FoodLogRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val selectedDate: String = savedStateHandle.get<String>("date") ?: ""

    private val _uiState = MutableStateFlow(LogFoodUiState())
    val uiState: StateFlow<LogFoodUiState> = _uiState.asStateFlow()

    // --- EXISTING SEARCH LOGIC ---

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

    // --- STANDARD LOGGING (BY FOOD ID) ---

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

            val loggedAt = if (selectedDate.isNotBlank()) {
                val date = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_LOCAL_DATE)
                val currentTime = LocalTime.now()
                LocalDateTime.of(date, currentTime)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } else {
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }

            // Standard log: We send the ID, backend calculates macros
            foodLogRepository.createFoodLog(
                userId = currentUser.id,
                foodId = selectedFood.id,
                quantityGrams = quantity,
                loggedAt = loggedAt
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
                        it.copy(isLoading = false, error = e.message ?: "Failed to log food")
                    }
                }
        }
    }

    // --- NEW: CAMERA / AI LOGIC ---

    fun analyzePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Call the repository method that uses Gemini
            val result = foodLogRepository.analyzeFoodPhoto(bitmap)

            result.onSuccess { foodLog ->
                // Store the result in state so ScanResultScreen can show it
                _uiState.update { it.copy(isLoading = false, scannedFoodLog = foodLog) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "AI Analysis Failed") }
            }
        }
    }

    fun confirmScannedFood() {
        val currentUser = userRepository.currentUser.value
        val scanned = _uiState.value.scannedFoodLog

        if (currentUser == null || scanned == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val loggedAt = if (selectedDate.isNotBlank()) {
                val date = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_LOCAL_DATE)
                val currentTime = LocalTime.now()
                LocalDateTime.of(date, currentTime)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } else {
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }

            // AI Log: We send the macros directly (Quick Add)
            // We use createQuickLog because we don't have a foodId, and we have explicit macros from Gemini
            val result = foodLogRepository.createQuickLog(
                userId = currentUser.id,
                name = scanned.name,
                calories = scanned.calories,
                protein = scanned.proteinGrams ?: BigDecimal.ZERO,
                carbs = scanned.carbsGrams ?: BigDecimal.ZERO,
                fats = scanned.fatsGrams ?: BigDecimal.ZERO,
                loggedAt = loggedAt
            )

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        logSuccess = true,
                        scannedFoodLog = null
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to save log") }
            }
        }
    }

    fun clearScannedData() {
        _uiState.update { it.copy(scannedFoodLog = null, error = null) }
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
