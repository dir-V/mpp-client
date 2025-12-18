package com.example.macropp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateUserGoalRequest
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.domain.repository.UserGoalRepository
import com.example.macropp.domain.repository.UserRepository
import com.example.macropp.presentation.userGoal.GoalsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userGoalRepository: UserGoalRepository
) : ViewModel() {

// TODO: WE NEED SOME WAY OF GETTING THE USER ID!

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

            // Create the request object
            val request = CreateUserGoalRequest(
                userId = currentState.userId,
                goalType = currentState.goalType,
                targetCalories = currentState.targetCalories.toIntOrNull() ?: 0,
                targetProteinGrams = currentState.targetProteinGrams.toBigDecimal(),
                targetCarbsGrams = currentState.targetCarbsGrams.toBigDecimal(),
                targetFatsGrams = currentState.targetFatsGrams.toBigDecimal()
            )

            // Call the Repository (The code you provided!)
            val result = userGoalRepository.createUserGoal(request)

            result.onSuccess { user ->
                _goalState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { exception ->
                _goalState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}
