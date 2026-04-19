package com.krzywdek19.gymnasiosmobile.presentation.trainingplan.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.krzywdek19.gymnasiosmobile.R
import com.krzywdek19.gymnasiosmobile.domain.model.WorkoutTemplate

@Composable
fun WorkoutCard(
    workout: WorkoutTemplate,
    displayOrder: Int,
    isNextWorkout: Boolean,
    isDragging: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    onEdit: (WorkoutTemplate) -> Unit,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(if (isNextWorkout) 20.dp else 18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 10.dp else if (isNextWorkout) 4.dp else 2.dp
        ),
        colors = if (isNextWorkout) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            )
        } else {
            CardDefaults.cardColors()
        },
        onClick = { onClick(workout.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isNextWorkout) 18.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = workout.name,
                        style = if (isNextWorkout) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.titleMedium
                        },
                        fontWeight = if (isNextWorkout) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Medium
                        }
                    )

                    if (isNextWorkout) {
                        Text(
                            text = stringResource(R.string.next_workout_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = stringResource(R.string.workout_order, displayOrder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = { onEdit(workout) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_workout)
                        )
                    }

                    IconButton(
                        onClick = { onDelete(workout.id) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_workout)
                        )
                    }
                }
            }
        }
    }
}