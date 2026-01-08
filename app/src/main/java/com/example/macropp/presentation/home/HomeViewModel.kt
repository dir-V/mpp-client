package com.example.macropp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.session.SessionManager
import com.example.macropp.domain.repository.FoodLogRepository
import com.example.macropp.domain.repository.UserGoalRepository
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
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodLogRepository: FoodLogRepository,
    private val userRepository: UserRepository,
    private val userGoalRepository: UserGoalRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFoodLogs()
        loadUserGoals()
    }
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

    // Private implementation that takes the ID
    fun loadFoodLogs() {
        val selectedDate = _uiState.value.selectedDate
        val dateString = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

        _uiState.update { it.copy(isLoading = true, error = null) }
        withUser { userId ->
            foodLogRepository.getUserFoodLogs(userId, dateString)
                .onSuccess { logs ->
                    val totalCalories = logs.sumOf { it.calories }
                    val totalProtein = logs.mapNotNull { it.proteinGrams }
                        .fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    val totalCarbs = logs.mapNotNull { it.carbsGrams }
                        .fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    val totalFats =
                        logs.mapNotNull { it.fatsGrams }.fold(BigDecimal.ZERO) { acc, v -> acc + v }

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
    fun loadUserGoals() {
        withUser { userId ->
            userGoalRepository.getActiveGoal(userId)
                .onSuccess { goal ->
                    _uiState.update {
                        it.copy(
                            goalCalories = goal.targetCalories,
                            goalProtein = goal.targetProteinGrams,
                            goalCarbs = goal.targetCarbsGrams,
                            goalFats = goal.targetFatsGrams,
                        )
                    }
                }
                .onFailure {
                    println("Failed to load goals: ${it.message}")
                }
        }
    }
    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadFoodLogs()
    }

    fun goToPreviousDay() {
        val newDate = _uiState.value.selectedDate.minusDays(1)
        selectDate(newDate)
    }

    fun goToNextDay() {
        val newDate = _uiState.value.selectedDate.plusDays(1)
        selectDate(newDate)
    }

    fun goToToday() {
        selectDate(LocalDate.now())
    }

    fun onFoodLogTapped(foodLog: com.example.macropp.domain.model.FoodLog) {
        _uiState.update { it.copy(selectedFoodLogForEdit = foodLog) }
    }

    fun dismissEditSheet() {
        _uiState.update { it.copy(selectedFoodLogForEdit = null) }
    }

    fun updateTimestamp(hour: Int, minute: Int) {
        val foodLog = _uiState.value.selectedFoodLogForEdit ?: return

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

    fun deleteFoodLog() {
        val foodLog = _uiState.value.selectedFoodLogForEdit ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingFoodLog = true) }

            foodLogRepository.deleteFoodLog(foodLog.id)
                .onSuccess {
                    // This can be refactored later with loadFoodLogs() call like in updateTimestamp()
                    val updatedLogs = _uiState.value.foodLogs.filter { it.id != foodLog.id }
                    val totalCalories = updatedLogs.sumOf { it.calories }
                    val totalProtein = updatedLogs.mapNotNull { it.proteinGrams }.fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    val totalCarbs = updatedLogs.mapNotNull { it.carbsGrams }.fold(BigDecimal.ZERO) { acc, v -> acc + v }
                    val totalFats = updatedLogs.mapNotNull { it.fatsGrams }.fold(BigDecimal.ZERO) { acc, v -> acc + v }

                    _uiState.update {
                        it.copy(
                            foodLogs = updatedLogs,
                            totalCalories = totalCalories,
                            totalProtein = totalProtein,
                            totalCarbs = totalCarbs,
                            totalFats = totalFats,
                            selectedFoodLogForEdit = null,
                            isDeletingFoodLog = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isDeletingFoodLog = false,
                            error = e.message ?: "Failed to delete food log"
                        )
                    }
                }
        }
    }
}
