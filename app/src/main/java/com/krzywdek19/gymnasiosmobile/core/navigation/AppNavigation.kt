package com.krzywdek19.gymnasiosmobile.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    isLoggedIn: Boolean,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    authRepository: AuthRepository
) {
    val navController = rememberNavController()
    val startDestination = if (isLoggedIn) {
        Screen.TrainingPlans.route
    } else {
        Screen.Login.route
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showTopBar = currentRoute != Screen.Login.route && currentRoute != Screen.Register.route
    val title = getScreenTitle(currentRoute)

    Scaffold(
        topBar = {
            if (showTopBar) {
                AppTopBar(
                    title = title,
                    onLogoutClick = {
                        authRepository.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.TrainingPlans.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
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
                    onRegisterSuccess = {
                        navController.popBackStack()
                    },
                    onGoToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.TrainingPlans.route) {
                TrainingPlanScreen(
                    onPlanClick = { planId ->
                        navController.navigate(Screen.TrainingPlanDetails.createRoute(planId))
                    },
                    onAddClick = {
                        navController.navigate(Screen.CreateTrainingPlan.route)
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
                    onSave = { navController.popBackStack() }
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
}

private fun getScreenTitle(route: String?): String {
    return when (route) {
        Screen.TrainingPlans.route -> "Training Plans"
        Screen.CreateTrainingPlan.route -> "Create Plan"
        Screen.TrainingPlanDetails.route -> "Plan Details"
        Screen.WorkoutTemplateDetails.route -> "Workout Details"
        Screen.CreateExerciseTemplate.route -> "Create Exercise"
        else -> "Gymnasios"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(
    title: String,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            TextButton(onClick = onLogoutClick) {
                Text("Wyloguj")
            }
        }
    )
}