package com.example.macropp.presentation.log

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScanResultScreen(
    viewModel: LogFoodViewModel,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analysing food...")
            } else if (uiState.scannedFoodLog != null) {
                val food = uiState.scannedFoodLog!!

                Text(text = "Food Found!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))

                // Display Details
                Text("Calories: ${food.calories}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Protein: ${food.proteinGrams}g")
                Text("Carbs: ${food.carbsGrams}g")
                Text("Fats: ${food.fatsGrams}g")

                Spacer(modifier = Modifier.height(48.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Cross / Cancel
                    OutlinedIconButton(
                        onClick = onCancel,
                        modifier = Modifier.height(64.dp).fillMaxWidth(0.4f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }

                    // Tick / Confirm
                    FilledIconButton(
                        onClick = {
                            viewModel.confirmScannedFood()
                            onConfirm()
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.height(64.dp).fillMaxWidth(0.4f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirm", tint = Color.White)
                    }
                }
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedIconButton(onClick = onCancel) {
                    Text("Try Again")
                }
            }
        }
    }
}