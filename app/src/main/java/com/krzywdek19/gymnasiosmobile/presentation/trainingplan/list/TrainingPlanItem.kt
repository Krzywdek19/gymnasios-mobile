package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.model.TrainingPlan

@Composable
fun TrainingPlanItem(
    plan: TrainingPlan,
    onClick: (TrainingPlan) -> Unit
) {
    Card(
        onClick = {onClick(plan)},
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.training_plan_status, plan.status),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}