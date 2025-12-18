package com.example.macropp.presentation.food

data class CreateFoodUiState(
    val formData: CreateFoodFormData = CreateFoodFormData(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class CreateFoodFormData(
    val name: String = "",
    val caloriesPer100Grams: String = "",
    val proteinPer100Grams: String = "",
    val carbsPer100Grams: String = "",
    val fatsPer100Grams: String = "",
    val servingSizeGrams: String = "",
    val barcode: String = "",
)
