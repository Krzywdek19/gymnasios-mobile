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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    onClick: (String) -> Unit,
    onEdit: (WorkoutTemplate) -> Unit,
    onDelete: (String) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick(workout.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = stringResource(R.string.workout_order, workout.orderIndex),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onMoveUp != null) {
                    IconButton(onClick = onMoveUp) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.move_workout_up)
                        )
                    }
                }

                if (onMoveDown != null) {
                    IconButton(onClick = onMoveDown) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.move_workout_down)
                        )
                    }
                }

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