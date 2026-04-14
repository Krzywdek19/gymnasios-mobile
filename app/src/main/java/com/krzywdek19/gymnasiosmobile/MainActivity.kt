package com.krzywdek19.gymnasiosmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.krzywdek19.gymnasiosmobile.core.navigation.AppNavigation
import com.krzywdek19.gymnasiosmobile.core.network.ApiFactory
import com.krzywdek19.gymnasiosmobile.core.ui.theme.GymnasiosMobileTheme
import com.krzywdek19.gymnasiosmobile.data.local.TokenStorage
import com.krzywdek19.gymnasiosmobile.data.repository.AuthRepository
import com.krzywdek19.gymnasiosmobile.presentation.auth.login.LoginViewModel
import com.krzywdek19.gymnasiosmobile.presentation.auth.register.RegisterViewModel

class MainActivity : ComponentActivity() {

    private val tokenStorage by lazy {
        TokenStorage(applicationContext)
    }

    private val authRepository by lazy {
        AuthRepository(
            authApi = ApiFactory.authApi,
            tokenStorage = tokenStorage
        )
    }

    private val loginViewModel by lazy {
        LoginViewModel(authRepository)
    }

    private val registerViewModel by lazy {
        RegisterViewModel(authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GymnasiosMobileTheme {
                AppNavigation(
                    isLoggedIn = tokenStorage.isLoggedIn(),
                    loginViewModel = loginViewModel,
                    registerViewModel = registerViewModel
                )
            }
        }
    }
}