package com.example.macropp.presentation.weighin

import WeightLineGraph
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WeighInScreen(
    viewModel: WeighInViewModel = hiltViewModel(),
    onWeighInSaved: () -> Unit,
    onNavigateBack: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onWeighInSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Enter weight:",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.weight,
            onValueChange = viewModel::onWeightChange,
            label = { Text("Weight (kg)") },
            placeholder = { Text("0.0") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.bodyLarge,
            isError = uiState.error != null
        )

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = viewModel::onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = (uiState.weight.toDoubleOrNull() ?: 0.0) > 0.0 && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Save Weight")
            }
        }

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        )  {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("History", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))

        WeightLineGraph(
            data = uiState.weighInHistory,
            modifier = Modifier.padding(vertical = 16.dp)
//                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
        )
    }
}
