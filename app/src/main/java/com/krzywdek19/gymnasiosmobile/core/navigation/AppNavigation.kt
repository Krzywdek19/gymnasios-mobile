package com.krzywdek19.gymnasiosmobile.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.create.CreateTrainingPlanScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details.TrainingPlanDetailsScreen
import com.krzywdek19.gymnasiosmobile.presentation.trainingplan.list.TrainingPlanScreen

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
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateTrainingPlan.route) {
            CreateTrainingPlanScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
    }
}