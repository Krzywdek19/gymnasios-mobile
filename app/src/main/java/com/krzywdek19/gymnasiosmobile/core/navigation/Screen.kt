package com.krzywdek19.gymnasiosmobile.core.navigation

sealed class Screen(val route: String) {
    data object TrainingPlans : Screen("training_plans")
    data object CreateTrainingPlan : Screen("create_training_plan")
    data object TrainingPlanDetails : Screen("training_plan_details/{planId}") {
        fun createRoute(planId: String) = "training_plan_details/$planId"
    }
}