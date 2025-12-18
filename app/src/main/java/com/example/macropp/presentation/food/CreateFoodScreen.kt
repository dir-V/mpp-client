package com.example.macropp.presentation.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreateFoodScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateFoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateFoodScreen(
       foodName = uiState.formData.name,
       caloriesPer100Grams = uiState.formData.caloriesPer100Grams,
       proteinPer100Grams = uiState.formData.proteinPer100Grams,
       carbsPer100Grams = uiState.formData.carbsPer100Grams,
       fatsPer100Grams = uiState.formData.fatsPer100Grams,
       servingSizeGrams = uiState.formData.servingSizeGrams,
       onFoodNameChange = viewModel::updateFoodName,
       onCaloriesChange = viewModel::updateCalories,
       onProteinChange = viewModel::updateProtein,
       onCarbsChange = viewModel::updateCarbs,
       onFatsChange = viewModel::updateFats,
       onServingSizeChange = viewModel::updateServingSize,
       onSave = viewModel::saveFood,
       modifier = modifier
    )
}

@Composable
internal fun CreateFoodScreen(
    foodName: String,
    caloriesPer100Grams: String,
    proteinPer100Grams: String,
    carbsPer100Grams: String,
    fatsPer100Grams: String,
    servingSizeGrams: String,
    onFoodNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onFatsChange: (String) -> Unit,
    onServingSizeChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create New Food",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = foodName,
            onValueChange = onFoodNameChange,
            label = { Text("Food Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "Nutritional Information (per 100g)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = caloriesPer100Grams,
            onValueChange = onCaloriesChange,
            label = { Text("Calories") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("kcal") }
        )

        OutlinedTextField(
            value = proteinPer100Grams,
            onValueChange = onProteinChange,
            label = { Text("Protein") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("g") }
        )

        OutlinedTextField(
            value = carbsPer100Grams,
            onValueChange = onCarbsChange,
            label = { Text("Carbohydrates") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("g") }
        )

        OutlinedTextField(
            value = fatsPer100Grams,
            onValueChange = onFatsChange,
            label = { Text("Fats") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("g") }
        )

        Text(
            text = "Serving Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = servingSizeGrams,
            onValueChange = onServingSizeChange,
            label = { Text("Serving Size") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("g") }
        )

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = foodName.isNotBlank()
        ) {
            Text("Save Food")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateFoodScreenPreview() {
    MaterialTheme {
        CreateFoodScreen(
            foodName = "Chicken Breast",
            caloriesPer100Grams = "165",
            proteinPer100Grams = "31",
            carbsPer100Grams = "0",
            fatsPer100Grams = "3.6",
            servingSizeGrams = "100",
            onFoodNameChange = {},
            onCaloriesChange = {},
            onProteinChange = {},
            onCarbsChange = {},
            onFatsChange = {},
            onServingSizeChange = {},
            onSave = {}
        )
    }
}