package com.example.macropp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.domain.repository.FoodLogRepository
import com.example.macropp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodLogRepository: FoodLogRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFoodLogs()
    }

    fun loadFoodLogs() {
        val currentUser = userRepository.currentUser.value ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            foodLogRepository.getUserFoodLogs(currentUser.id)
                .onSuccess { logs ->
                    val totalCalories = logs.sumOf { it.calories }
                    val totalProtein = logs.mapNotNull { it.proteinGrams }.fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    val totalCarbs = logs.mapNotNull { it.carbsGrams }.fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    val totalFats = logs.mapNotNull { it.fatsGrams }.fold(BigDecimal.ZERO) { acc, v -> acc + v }

                    _uiState.update {
                        it.copy(
                            foodLogs = logs,
                            totalCalories = totalCalories,
                            totalProtein = totalProtein,
                            totalCarbs = totalCarbs,
                            totalFats = totalFats,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load food logs"
                        )
                    }
                }
        }
    }

    fun onFoodLogTapped(foodLog: com.example.macropp.domain.model.FoodLog) {
        _uiState.update { it.copy(selectedFoodLogForEdit = foodLog) }
    }

    fun dismissEditSheet() {
        _uiState.update { it.copy(selectedFoodLogForEdit = null) }
    }

    fun updateTimestamp(hour: Int, minute: Int) {
        val foodLog = _uiState.value.selectedFoodLogForEdit ?: return
        val currentUser = userRepository.currentUser.value ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingTimestamp = true) }

            val existingDateTime = foodLog.loggedAt?.let {
                try {
                    LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                } catch (e: Exception) {
                    LocalDateTime.now()
                }
            } ?: LocalDateTime.now()

            val newDateTime = existingDateTime
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)

            val formattedDateTime = newDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            foodLogRepository.updateFoodLogTimestamp(foodLog.id, formattedDateTime)
                .onSuccess {
                    loadFoodLogs()
                    _uiState.update {
                        it.copy(
                            selectedFoodLogForEdit = null,
                            isUpdatingTimestamp = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isUpdatingTimestamp = false,
                            error = e.message ?: "Failed to update timestamp"
                        )
                    }
                }
        }
    }
}
