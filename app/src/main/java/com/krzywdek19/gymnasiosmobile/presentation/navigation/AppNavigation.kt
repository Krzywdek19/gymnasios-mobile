package com.krzywdek19.gymnasiosmobile.presentation.navigation

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
        startDestination = "training_plans"
    ) {
        composable("training_plans") {
            TrainingPlanScreen(
                onPlanClick = { planId ->
                    navController.navigate("training_plan_details/$planId")
                },
                onAddClick = {
                    navController.navigate("create_training_plan")
                }
            )
        }

        composable("training_plan_details/{planId}") { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            TrainingPlanDetailsScreen(planId = planId, onBack = {navController.popBackStack()})
        }

        composable("create_training_plan") {
            CreateTrainingPlanScreen(
                onSave = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}