package com.example.macropp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.session.SessionManager
import com.example.macropp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow(AuthUiState())
    // Public read-only state
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun onHeightChange(newHeight: String) {
        _uiState.update { it.copy(height = newHeight) }
    }

    fun onSubmit() {
        val currentState = _uiState.value

        // Basic validation
        if (currentState.email.isBlank() || currentState.height.isBlank()) {
            _uiState.update { it.copy(error = "Please fill in all fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Create the request object
            val request = CreateUserRequest(
                email = currentState.email,
                heightCm = currentState.height.toIntOrNull() ?: 0
            )

            // Call the Repository (The code you provided!)
            val result = userRepository.createUser(request)

            result.onSuccess { user ->
                sessionManager.saveUserId(user.id)
                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }

    fun login() {
        val currentState = _uiState.value

        if (currentState.email.isBlank()) {
            _uiState.update { it.copy(error = "Please enter an email") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // ASSUMPTION: Your UserRepository has a 'loginUser' function.
            // If not, you need to add it to the Repo interface first.
            val result = userRepository.loginUser(currentState.email)

            result.onSuccess { user ->
                // CRITICAL: Save the ID so GoalsViewModel can find it later!
                sessionManager.saveUserId(user.id)

                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}
