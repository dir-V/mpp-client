package com.example.macropp.presentation.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.macropp.domain.model.Food
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogFoodScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreateFood: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LogFoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.logSuccess) {
        if (uiState.logSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Food") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCreateFood) {
                        Icon(Icons.Default.Add, contentDescription = "Create New Food")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                label = { Text("Search your foods") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (uiState.searchResults.isEmpty() && uiState.searchQuery.isNotBlank()) {
                Text(
                    text = "No foods found",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.searchResults) { food ->
                        FoodSearchItem(
                            food = food,
                            onClick = { viewModel.selectFood(food) }
                        )
                    }
                }
            }
        }
    }

    uiState.selectedFood?.let { food ->
        QuantityDialog(
            food = food,
            quantity = uiState.quantityGrams,
            onQuantityChange = viewModel::updateQuantity,
            onDismiss = viewModel::clearSelectedFood,
            onConfirm = viewModel::logFood,
            isLoading = uiState.isLoading
        )
    }
}

@Composable
private fun FoodSearchItem(
    food: Food,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = food.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${food.caloriesPer100Grams} kcal per 100g",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (food.proteinPer100Grams != null || food.carbsPer100Grams != null || food.fatsPer100Grams != null) {
                Text(
                    text = buildMacroString(food),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuantityDialog(
    food: Food,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean
) {
    val quantityBigDecimal = quantity.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val ratio = if (quantityBigDecimal > BigDecimal.ZERO) {
        quantityBigDecimal.divide(BigDecimal("100"), 4, RoundingMode.HALF_UP)
    } else {
        BigDecimal.ZERO
    }
    val estimatedCalories = (food.caloriesPer100Grams.toBigDecimal() * ratio).setScale(0, RoundingMode.HALF_UP)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log ${food.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    label = { Text("Quantity") },
                    suffix = { Text("g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Estimated: $estimatedCalories kcal",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading && quantity.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Log")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun buildMacroString(food: Food): String {
    val parts = mutableListOf<String>()
    food.proteinPer100Grams?.let { parts.add("P: ${it}g") }
    food.carbsPer100Grams?.let { parts.add("C: ${it}g") }
    food.fatsPer100Grams?.let { parts.add("F: ${it}g") }
    return parts.joinToString(" | ")
}
