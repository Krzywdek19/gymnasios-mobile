package com.krzywdek19.gymnasiosmobile.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krzywdek19.gymnasiosmobile.core.network.SessionManager
import com.krzywdek19.gymnasiosmobile.data.repository.AuthRepository
import com.krzywdek19.gymnasiosmobile.presentation.auth.login.LoginScreen
import com.krzywdek19.gymnasiosmobile.presentation.auth.login.LoginViewModel
import com.krzywdek19.gymnasiosmobile.presentation.auth.register.RegisterScreen
import com.krzywdek19.gymnasiosmobile.presentation.auth.register.RegisterViewModel
import com.krzywdek19.gymnasiosmobile.presentation.exercisetemplate.create.CreateExerciseTemplateScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.create.CreateTrainingPlanScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details.TrainingPlanDetailsScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.list.TrainingPlanScreen
import com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details.WorkoutTemplateDetailsScreen

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    authRepository: AuthRepository,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()
    val currentIsLoggedIn by sessionManager.isLoggedIn.collectAsState()

    LaunchedEffect(currentIsLoggedIn) {
        if (!currentIsLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (currentIsLoggedIn) {
            Screen.TrainingPlans.route
        } else {
            Screen.Login.route
        }
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.TrainingPlans.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.TrainingPlans.route) { backStackEntry ->
            TrainingPlanScreen(
                backStackEntry = backStackEntry,
                onPlanClick = { planId ->
                    navController.navigate(Screen.TrainingPlanDetails.createRoute(planId))
                },
                onAddClick = {
                    navController.navigate(Screen.CreateTrainingPlan.route)
                },
                onLogoutClick = {
                    authRepository.logout()
                }
            )
        }

        composable(Screen.TrainingPlanDetails.route) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            TrainingPlanDetailsScreen(
                planId = planId,
                onBack = { navController.popBackStack() },
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.WorkoutTemplateDetails.createRoute(workoutId))
                }
            )
        }

        composable(Screen.CreateTrainingPlan.route) {
            CreateTrainingPlanScreen(
                onBack = { navController.popBackStack() },
                onSave = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("plan_created", true)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.WorkoutTemplateDetails.route) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            WorkoutTemplateDetailsScreen(
                workoutId = workoutId,
                backStackEntry = backStackEntry,
                onBack = { navController.popBackStack() },
                onAddExerciseClick = { templateId ->
                    navController.navigate(Screen.CreateExerciseTemplate.createRoute(templateId))
                }
            )
        }

        composable(Screen.CreateExerciseTemplate.route) { backStackEntry ->
            val workoutTemplateId = backStackEntry.arguments?.getString("workoutTemplateId") ?: ""
            CreateExerciseTemplateScreen(
                workoutTemplateId = workoutTemplateId,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("exercise_created", true)
                    navController.popBackStack()
                }
            )
        }
    }
}