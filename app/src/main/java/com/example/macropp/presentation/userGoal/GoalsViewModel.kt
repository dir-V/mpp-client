package com.example.macropp.presentation.userGoal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateUserGoalRequest
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.remote.dto.GoalType
import com.example.macropp.data.session.SessionManager
import com.example.macropp.domain.repository.UserGoalRepository
import com.example.macropp.domain.repository.UserRepository
import com.example.macropp.presentation.userGoal.GoalsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userGoalRepository: UserGoalRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

// TODO: WE NEED SOME WAY OF GETTING THE USER ID!
    init {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId == null) {
                // If no user ID is found, update state to block UI or trigger navigation
                _goalState.update {
                    it.copy(error = "User session expired. Please log in again.")
                }
            }
        }
    }

    // Internal mutable state
    private val _goalState = MutableStateFlow(GoalsUiState())
    // Public read-only state
    val goalState = _goalState.asStateFlow()

    fun onGoalTypeChange(newGoalType: String) {
        _goalState.update { it.copy(goalType = newGoalType) }
    }
    fun onTargetCaloriesChange(newTargetCalories: String) {
        _goalState.update { it.copy(targetCalories = newTargetCalories) }
    }

    fun onTargetProteinGramsChange(newTargetProteinGrams: String) {
        _goalState.update { it.copy(targetProteinGrams = newTargetProteinGrams) }
    }

    fun onTargetCarbsGramsChange(newTargetCarbsGrams: String) {
        _goalState.update { it.copy(targetCarbsGrams = newTargetCarbsGrams) }
    }

    fun onTargetFatsGramsChange(newTargetFatsGrams: String) {
        _goalState.update { it.copy(targetFatsGrams = newTargetFatsGrams) }
    }

    fun onSubmit() {

        val currentState = _goalState.value

        // Basic validation
//        if (currentState.email.isBlank() || currentState.height.isBlank()) {
//            _goalState.update { it.copy(error = "Please fill in all fields") }
//            return
//        }

        viewModelScope.launch {
            _goalState.update { it.copy(isLoading = true, error = null) }


            val userId = sessionManager.getUserId()
            if (userId == null) {
                _goalState.update { it.copy(isLoading = false, error = "User not logged in.") }
                return@launch
            }

            // Create the request object
            val request = CreateUserGoalRequest(
                userId = userId.toString(),
                goalType = GoalType.valueOf(currentState.goalType.uppercase()),
                targetCalories = currentState.targetCalories.toIntOrNull() ?: 0,
                targetProteinGrams = currentState.targetProteinGrams.toBigDecimal(),
                targetCarbsGrams = currentState.targetCarbsGrams.toBigDecimal(),
                targetFatsGrams = currentState.targetFatsGrams.toBigDecimal()
            )


            val result = userGoalRepository.createUserGoal(request)

            result.onSuccess {
                _goalState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _goalState.update { it.copy(isLoading = false, error = exception.message) }
            }

        }
    }
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()

        }
    }
}
