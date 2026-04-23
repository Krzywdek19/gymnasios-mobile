package com.krzywdek19.gymnasiosmobile.core.network

import com.krzywdek19.gymnasiosmobile.data.repository.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(
    private val tokenStorage: TokenStorage
) {

    private val _isLoggedIn = MutableStateFlow(tokenStorage.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun refreshSessionState() {
        _isLoggedIn.value = tokenStorage.isLoggedIn()
    }

    fun onLogin(
        accessToken: String,
        refreshToken: String,
        expiresIn: Long
    ) {
        tokenStorage.saveTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiresIn
        )
        _isLoggedIn.value = true
    }

    fun logout() {
        tokenStorage.clear()
        _isLoggedIn.value = false
    }
}