package com.krzywdek19.gymnasiosmobile.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.krzywdek19.gymnasiosmobile.presentation.workoutsession.active.WorkoutSessionScreen
import com.krzywdek19.gymnasiosmobile.presentation.workoutsession.history.WorkoutSessionHistoryScreen
import com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details.WorkoutTemplateDetailsScreen

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    authRepository: AuthRepository,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(isLoggedIn, currentRoute) {
        if (isLoggedIn) {
            if (
                currentRoute == null ||
                currentRoute == Screen.Login.route ||
                currentRoute == Screen.Register.route
            ) {
                navController.navigate(Screen.TrainingPlans.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else {
            val isAuthRoute = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route

            if (currentRoute != null && !isAuthRoute) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) { backStackEntry ->
            val registrationSuccess by backStackEntry.savedStateHandle
                .getStateFlow("registration_success", false)
                .collectAsState()

            LaunchedEffect(registrationSuccess) {
                if (registrationSuccess) {
                    loginViewModel.showRegistrationSuccessMessage()
                    backStackEntry.savedStateHandle["registration_success"] = false
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onGoToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("registration_success", true)
                    navController.popBackStack()
                },
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
                },
                onWorkoutSessionReady = { workoutSessionId ->
                    navController.navigate(Screen.WorkoutSession.createRoute(workoutSessionId))
                },
                onWorkoutHistoryClick = {
                    navController.navigate(Screen.WorkoutSessionHistory.route)
                },
                onPlanDeleted = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.WorkoutSession.route) { backStackEntry ->
            val workoutSessionId = backStackEntry.arguments?.getString("workoutSessionId") ?: ""

            WorkoutSessionScreen(
                workoutSessionId = workoutSessionId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WorkoutSessionHistory.route) {
            WorkoutSessionHistoryScreen(
                onBack = { navController.popBackStack() },
                onSessionClick = { workoutSessionId ->
                    navController.navigate(Screen.WorkoutSession.createRoute(workoutSessionId))
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