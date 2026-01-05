package com.example.macropp.presentation.userGoal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUserGoalsScreen(
    onNavigateBack: () -> Unit,
    onSetGoalsSuccess: () -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val state by viewModel.goalState.collectAsState()
    val goalOptions = listOf("Maintenance", "Deficit", "Surplus")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Set Goals", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

//        Box(
//            modifier = Modifier
//                .padding(16.dp)
//        ) {
//            IconButton(onClick = { expanded = !expanded }) {
//                Icon(Icons.Default.MoreVert, contentDescription = "More options")
//            }
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                    DropdownMenuItem(
//                        text = { Text("Maintenance") },
//                        onClick = { /* Do something... */ }
//                    )
//                    DropdownMenuItem(
//                        text = { Text("Deficit") },
//                        onClick = { /* Do something... */}
//                    )
//                    DropdownMenuItem(
//                        text = { Text("Surplus") },
//                        onClick = { /* Do something... */ }
//                    )
//            }
//        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = state.goalType.ifBlank { "Select goal type" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Goal Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                goalOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            viewModel.onGoalTypeChange(option) // Update ViewModel
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.targetCalories,
            onValueChange = { viewModel.onTargetCaloriesChange(it) },
            label = { Text("Target Calories") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.targetProteinGrams,
            onValueChange = { viewModel.onTargetProteinGramsChange(it) },
            label = { Text("Target Protein(g)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.targetCarbsGrams,
            onValueChange = { viewModel.onTargetCarbsGramsChange(it) },
            label = { Text("Target Carbs(g)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.targetFatsGrams,
            onValueChange = { viewModel.onTargetFatsGramsChange(it) },
            label = { Text("Target Fats(g)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))


        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.onSubmit() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set Goal")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )  {
            Text("Cancel")
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}