package com.example.macropp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.session.SessionManager
import com.example.macropp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            // Check your SessionManager for a user ID
            val userId = sessionManager.getUserId()

            // If ID is not null, they are logged in
            println(userId)
            _isUserLoggedIn.value = userId != null

            // We are done loading
            _isLoading.value = false
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

            val result = userRepository.loginUser(currentState.email)

            result.onSuccess { user ->

                sessionManager.saveUserId(user.id)

                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }
}
