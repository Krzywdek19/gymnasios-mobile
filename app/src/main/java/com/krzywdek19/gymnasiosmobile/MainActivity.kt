package com.krzywdek19.gymnasiosmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.krzywdek19.gymnasiosmobile.core.navigation.AppNavigation
import com.krzywdek19.gymnasiosmobile.core.network.RetrofitProvider
import com.krzywdek19.gymnasiosmobile.core.ui.theme.GymnasiosMobileTheme
import com.krzywdek19.gymnasiosmobile.di.AppContainer
import com.krzywdek19.gymnasiosmobile.presentation.auth.login.LoginViewModel
import com.krzywdek19.gymnasiosmobile.presentation.auth.register.RegisterViewModel

class MainActivity : ComponentActivity() {

    private val authRepository by lazy {
        AppContainer.authRepository
    }

    private val loginViewModel by lazy {
        LoginViewModel(authRepository)
    }

    private val registerViewModel by lazy {
        RegisterViewModel(authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppContainer.init(applicationContext)

        val tokenStorage = AppContainer.getTokenStorage()
        val sessionManager = AppContainer.sessionManager

        RetrofitProvider.init(
            tokenStorage = tokenStorage,
            sessionManager = sessionManager
        )

        enableEdgeToEdge()

        setContent {
            GymnasiosMobileTheme {
                AppNavigation(
                    loginViewModel = loginViewModel,
                    registerViewModel = registerViewModel,
                    authRepository = authRepository,
                    sessionManager = sessionManager
                )
            }
        }
    }
}