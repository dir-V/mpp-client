package com.example.macropp.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.macropp.domain.model.FoodLog
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogFood: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUserGoals()
        viewModel.loadFoodLogs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Log") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val dateString = uiState.selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    onNavigateToLogFood(dateString)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Log Food"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DateSelector(
                selectedDate = uiState.selectedDate,
                onPreviousDay = { viewModel.goToPreviousDay() },
                onNextDay = { viewModel.goToNextDay() },
                onTodayClick = { viewModel.goToToday() },
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            DailyTotalsGaugeCard(
                totalCalories = uiState.totalCalories,
                totalProtein = uiState.totalProtein,
                totalCarbs = uiState.totalCarbs,
                totalFats = uiState.totalFats,

                goalCalories = uiState.goalCalories,
                goalProtein = uiState.goalProtein,
                goalCarbs = uiState.goalCarbs,
                goalFats = uiState.goalFats,
                modifier = Modifier.padding(8.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.foodLogs.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No foods logged yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap + to log your first food",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.foodLogs) { foodLog ->
                            FoodLogItem(
                                foodLog = foodLog,
                                onClick = { viewModel.onFoodLogTapped(foodLog) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        uiState.selectedFoodLogForEdit?.let { selectedFoodLog ->
            TimestampEditBottomSheet(
                foodLog = selectedFoodLog,
                isUpdating = uiState.isUpdatingTimestamp,
                isDeleting = uiState.isDeletingFoodLog,
                onDismiss = { viewModel.dismissEditSheet() },
                onTimeSelected = { hour, minute -> viewModel.updateTimestamp(hour, minute) },
                onDelete = { viewModel.deleteFoodLog() }
            )
        }
    }
}

@Composable
private fun DateSelector(
    selectedDate: LocalDate,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onTodayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val isToday = selectedDate == today

    val dateText = when {
        isToday -> "Today"
        selectedDate == today.minusDays(1) -> "Yesterday"
        selectedDate == today.plusDays(1) -> "Tomorrow"
        else -> selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.getDefault()))
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous day"
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dateText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (!isToday) {
                TextButton(onClick = onTodayClick) {
                    Text(
                        text = "Go to Today",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        IconButton(onClick = onNextDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next day"
            )
        }
    }
}


@Composable
fun DailyTotalsGaugeCard(
    totalCalories: Int,
    totalProtein: BigDecimal,
    totalCarbs: BigDecimal,
    totalFats: BigDecimal,
    goalCalories: Int?,
    goalProtein: BigDecimal?,
    goalCarbs: BigDecimal?,
    goalFats: BigDecimal?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), // Extra padding for a spacious look
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily totals",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- HERO GAUGE (CALORIES) ---


            Spacer(modifier = Modifier.height(16.dp))

            // --- MACRO ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ArcProgress(
                    value = totalCalories.toString(),
                    label = "kcal",
                    subLabel = if(goalCalories != null) "of $goalCalories" else "",
                    progress = calculateProgress(totalCalories.toBigDecimal(), goalCalories?.toBigDecimal()),
                    modifier = Modifier.size(80.dp), // BIG Size
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary,
                    labelStyle = MaterialTheme.typography.titleMedium
                )
                // Protein
                ArcProgress(
                    value = "${formatDecimal(totalProtein)} g",
                    label = "Protein",
                    subLabel = if(goalProtein != null) "of ${formatDecimal(goalProtein)} g" else "",
                    progress = calculateProgress(totalProtein, goalProtein),
                    modifier = Modifier.size(80.dp), // Smaller Size
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary, // Blue
                    labelStyle = MaterialTheme.typography.titleMedium
                )

                // Carbs
                ArcProgress(
                    value = "${formatDecimal(totalCarbs)} g",
                    label = "Carbs",
                    subLabel = if (goalCarbs != null) "of ${formatDecimal(goalCarbs)} g" else "",
                    progress = calculateProgress(totalCarbs, goalCarbs),
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary, // Yellow
                    labelStyle = MaterialTheme.typography.titleMedium
                )

                // Fats
                ArcProgress(
                    value = "${formatDecimal(totalFats)} g",
                    label = "Fat",
                    subLabel = if (goalFats != null) "of ${formatDecimal(goalFats)} g" else "",
                    progress = calculateProgress(totalFats, goalFats),
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primary,
                    labelStyle = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * Custom Composable to draw an "Open Circle" or "Gauge"
 */
@Composable
fun ArcProgress(
    value: String,
    label: String,
    subLabel: String = "",
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp,
    color: Color,
    labelStyle: TextStyle
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Configuration for the Arc
            // 180 degrees = Half circle. 240 degrees = Open Circle "Speedometer" look.
            val sweepAngle = 240f
            val startAngle = 150f // (360 - 240) / 2 + 90 to rotate it to the bottom

            val strokeWidthPx = strokeWidth.toPx()

            // 1. Draw Background Track (Gray)
            drawArc(
                color = Color.LightGray.copy(alpha = 0.4f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // 2. Draw Progress (Color)
            drawArc(
                color = color,
                startAngle = startAngle,
                // Ensure we don't draw past the max sweep angle
                sweepAngle = sweepAngle * progress,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        // 3. The Text in the center/gap
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = labelStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (subLabel.isNotEmpty()) {
                Text(
                    text = subLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun DailyTotalsCard(
    totalCalories: Int,
    totalProtein: BigDecimal,
    totalCarbs: BigDecimal,
    totalFats: BigDecimal,
    goalCalories: Int?,
    goalProtein: BigDecimal?,
    goalCarbs: BigDecimal?,
    goalFats: BigDecimal?,
    modifier: Modifier = Modifier
) {
    // Helper to format "100 / 200" or just "100" if goal is missing
    fun formatProgress(current: String, goal: BigDecimal?): String {
        return if (goal != null) {
            "$current / ${formatDecimal(goal)}"
        } else {
            current
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Totals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Calories
                MacroColumn(
                    label = "Calories",
                    value = if (goalCalories != null) "$totalCalories / $goalCalories" else "$totalCalories",
                    unit = "kcal"
                )

                // Protein - Now uses the helper
                MacroColumn(
                    label = "Protein",
                    value = formatProgress(formatDecimal(totalProtein), goalProtein),
                    unit = "g"
                )

                // Carbs - Now actually uses goalCarbs!
                MacroColumn(
                    label = "Carbs",
                    value = formatProgress(formatDecimal(totalCarbs), goalCarbs),
                    unit = "g"
                )

                // Fats - Now actually uses goalFats!
                MacroColumn(
                    label = "Fats",
                    value = formatProgress(formatDecimal(totalFats), goalFats),
                    unit = "g"
                )
            }
        }
    }
}

@Composable
private fun MacroColumn(
    label: String,
    value: String,
    unit: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun FoodLogItem(
    foodLog: FoodLog,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = foodLog.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    foodLog.loggedAt?.let { timestamp ->
                        Text(
                            text = formatTimestamp(timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (foodLog.quantityGrams != null) {
                        Text(
                            text = "${formatDecimal(foodLog.quantityGrams)}g",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${foodLog.calories} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = buildMacroString(foodLog),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDecimal(value: BigDecimal?): String {
    return value?.setScale(1, RoundingMode.HALF_UP)?.stripTrailingZeros()?.toPlainString() ?: ""
}

private fun calculateProgress(current: BigDecimal, goal: BigDecimal?): Float {
    if (goal == null || goal.compareTo(BigDecimal.ZERO) == 0) return 0f
    val progress = current.toFloat() / goal.toFloat()
    return progress.coerceIn(0f, 1f)
}

private fun formatTimestamp(isoTimestamp: String): String {
    return try {
        val dateTime = LocalDateTime.parse(isoTimestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
        dateTime.format(formatter)
    } catch (e: Exception) {
        ""
    }
}

private fun buildMacroString(foodLog: FoodLog): String {
    val parts = mutableListOf<String>()
    foodLog.proteinGrams?.let { parts.add("P: ${formatDecimal(it)}") }
    foodLog.carbsGrams?.let { parts.add("C: ${formatDecimal(it)}") }
    foodLog.fatsGrams?.let { parts.add("F: ${formatDecimal(it)}") }
    return parts.joinToString(" | ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimestampEditBottomSheet(
    foodLog: FoodLog,
    isUpdating: Boolean,
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    val (initialHour, initialMinute) = if (foodLog.loggedAt != null) {
        try {
            val dateTime = LocalDateTime.parse(foodLog.loggedAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            dateTime.hour to dateTime.minute
        } catch (e: Exception) {
            val now = LocalDateTime.now()
            now.hour to now.minute
        }
    } else {
        val now = LocalDateTime.now()
        now.hour to now.minute
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    val isActionInProgress = isUpdating || isDeleting

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Time for ${foodLog.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            TimePicker(state = timePickerState)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isActionInProgress
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onTimeSelected(timePickerState.hour, timePickerState.minute)
                    },
                    enabled = !isActionInProgress
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onDelete,
                enabled = !isActionInProgress
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
