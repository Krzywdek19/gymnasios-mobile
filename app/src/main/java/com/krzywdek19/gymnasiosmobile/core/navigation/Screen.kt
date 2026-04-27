package com.krzywdek19.gymnasiosmobile.core.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")

    data object TrainingPlans : Screen("training_plans")
    data object CreateTrainingPlan : Screen("create_training_plan")

    data object TrainingPlanDetails : Screen("training_plan_details/{planId}") {
        fun createRoute(planId: String) = "training_plan_details/$planId"
    }

    data object WorkoutTemplateDetails : Screen("workout_template_details/{workoutId}") {
        fun createRoute(workoutId: String) = "workout_template_details/$workoutId"
    }

    data object CreateExerciseTemplate : Screen("create_exercise_template/{workoutTemplateId}") {
        fun createRoute(workoutTemplateId: String) = "create_exercise_template/$workoutTemplateId"
    }

    data object WorkoutSession : Screen("workout_session/{workoutSessionId}") {
        fun createRoute(workoutSessionId: String) = "workout_session/$workoutSessionId"
    }
}