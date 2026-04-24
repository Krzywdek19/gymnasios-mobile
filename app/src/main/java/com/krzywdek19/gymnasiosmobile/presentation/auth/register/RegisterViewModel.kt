package com.krzywdek19.gymnasiosmobile.presentation.auth.register

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            errorMessageRes = null
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            errorMessageRes = null
        )
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = value,
            errorMessageRes = null
        )
    }

    fun register() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword

        when {
            email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                _uiState.value = state.copy(errorMessageRes = R.string.error_register_fields_required)
                return
            }

            password != confirmPassword -> {
                _uiState.value = state.copy(errorMessageRes = R.string.error_register_password_mismatch)
                return
            }

            !isPasswordValid(password) -> {
                _uiState.value = state.copy(errorMessageRes = R.string.error_password_requirements)
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = authRepository.register(email, password)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = mapRegisterError(result.exceptionOrNull())
                )
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasMinLength && hasUppercase && hasDigit && hasSpecialChar
    }

    @StringRes
    private fun mapRegisterError(error: Throwable?): Int {
        val message = error?.message.orEmpty().lowercase()

        if (
            message.contains("email already") ||
            message.contains("already exists") ||
            message.contains("already taken") ||
            message.contains("duplicate") ||
            message.contains("user exists")
        ) {
            return R.string.error_email_already_taken
        }

        if (
            message.contains("password") &&
            (
                    message.contains("uppercase") ||
                            message.contains("special") ||
                            message.contains("digit") ||
                            message.contains("weak") ||
                            message.contains("requirements")
                    )
        ) {
            return R.string.error_password_requirements
        }

        if (error is HttpException) {
            return when (error.code()) {
                409 -> R.string.error_email_already_taken
                400, 422 -> R.string.error_password_requirements
                else -> R.string.error_register_failed
            }
        }

        return R.string.error_register_failed
    }
}