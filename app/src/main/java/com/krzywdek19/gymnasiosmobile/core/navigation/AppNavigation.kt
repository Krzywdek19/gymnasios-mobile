package com.krzywdek19.gymnasiosmobile.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krzywdek19.gymnasiosmobile.presentation.exercisetemplate.create.CreateExerciseTemplateScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.create.CreateTrainingPlanScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details.TrainingPlanDetailsScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.list.TrainingPlanScreen
import com.krzywdek19.gymnasiosmobile.presentation.workouttemplate.details.WorkoutTemplateDetailsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.TrainingPlans.route
    ) {
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
                onWorkoutClick = {workoutId -> navController.navigate(Screen.WorkoutTemplateDetails.createRoute(workoutId))}
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