package com.krzywdek19.gymnasiosmobile.presentation.auth.login

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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val infoMessageRes: Int? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            errorMessageRes = null,
            infoMessageRes = null
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            errorMessageRes = null,
            infoMessageRes = null
        )
    }

    fun showRegistrationSuccessMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessageRes = null,
            infoMessageRes = R.string.register_success
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessageRes = null,
            infoMessageRes = null
        )
    }

    fun login() {
        val state = _uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(
                errorMessageRes = R.string.error_login_fields_required,
                infoMessageRes = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                errorMessageRes = null,
                infoMessageRes = null
            )

            val result = authRepository.login(state.email.trim(), state.password)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    errorMessageRes = null,
                    infoMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = mapLoginError(result.exceptionOrNull()),
                    infoMessageRes = null
                )
            }
        }
    }

    @StringRes
    private fun mapLoginError(error: Throwable?): Int {
        val message = error?.message.orEmpty().lowercase()

        if (
            message.contains("inactive") ||
            message.contains("not active") ||
            message.contains("not enabled") ||
            message.contains("verify") ||
            message.contains("verification")
        ) {
            return R.string.error_account_not_activated
        }

        if (
            message.contains("bad credentials") ||
            message.contains("invalid credentials") ||
            message.contains("wrong password") ||
            message.contains("user not found")
        ) {
            return R.string.error_invalid_email_or_password
        }

        if (error is HttpException) {
            return when (error.code()) {
                401 -> R.string.error_invalid_email_or_password
                403 -> R.string.error_account_not_activated
                else -> R.string.error_login_failed
            }
        }

        return R.string.error_login_failed
    }
}