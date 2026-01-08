// WeightLineGraph.kt

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.macropp.domain.model.WeighIn
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun WeightLineGraph(
    data: List<WeighIn>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    pointColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) {
        Box(modifier = modifier.height(200.dp), contentAlignment = Alignment.Center) {
            Text("No history yet", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val axisColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(modifier = modifier.height(250.dp).fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // 1. Prepare Data
            val sortedData = data.sortedBy { it.weightDate }

            val leftPadding = 60f  // Space for Y-axis labels (weight)
            val bottomPadding = 40f // Space for X-axis labels (date)
            val topPadding = 20f   // Space at top
            val rightPadding = 20f // Space at right

            val graphWidth = size.width - leftPadding - rightPadding
            val graphHeight = size.height - bottomPadding - topPadding

            // Find Min/Max for Y-Axis (Weight)
            val minWeight = sortedData.minOf { it.weightKg }.toFloat()
            val maxWeight = sortedData.maxOf { it.weightKg }.toFloat()

            // Round to nice numbers for axis
            val yMin = floor(minWeight - 1).coerceAtLeast(0f)
            val yMax = ceil(maxWeight + 1)
            val yRange = yMax - yMin

            // Draw Y-Axis
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding),
                end = Offset(leftPadding, topPadding + graphHeight),
                strokeWidth = 2f
            )

            // Draw X-Axis
            drawLine(
                color = axisColor,
                start = Offset(leftPadding, topPadding + graphHeight),
                end = Offset(leftPadding + graphWidth, topPadding + graphHeight),
                strokeWidth = 2f
            )

            // Find Min/Max for X-Axis (Date)
            val firstDate = sortedData.first().weightDate
            val lastDate = sortedData.last().weightDate
            val totalDays = ChronoUnit.DAYS.between(firstDate, lastDate).toFloat()
            val xRange = if (totalDays > 0) totalDays else 1f

            // 2. Setup text paint for labels
            val textPaint = Paint().apply {
                color = textColor.toArgb()
                textSize = 28f
                textAlign = Paint.Align.CENTER
            }

            val textPaintLeft = Paint().apply {
                color = textColor.toArgb()
                textSize = 28f
                textAlign = Paint.Align.RIGHT
            }

            // 3. Draw Y-Axis Labels (Weight in kg)
            val ySteps = 5
            for (i in 0..ySteps) {
                val weight = yMin + (yRange * i / ySteps)
                val y = topPadding + graphHeight - (i.toFloat() / ySteps * graphHeight)

                // Draw tick mark
                drawLine(
                    color = axisColor,
                    start = Offset(leftPadding - 5f, y),
                    end = Offset(leftPadding, y),
                    strokeWidth = 2f
                )

                // Draw label
                drawContext.canvas.nativeCanvas.drawText(
                    "${weight.roundToInt()} kg",
                    leftPadding - 10f,
                    y + 10f,
                    textPaintLeft
                )

                // Draw grid line
                drawLine(
                    color = axisColor.copy(alpha = 0.2f),
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + graphWidth, y),
                    strokeWidth = 1f
                )
            }

            // 4. Draw X-Axis Labels (Dates)
            val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
            val xSteps = minOf(sortedData.size - 1, 4) // Show max 5 date labels

            for (i in 0..xSteps) {
                val dataIndex = (i * (sortedData.size - 1) / xSteps.coerceAtLeast(1))
                val dateEntry = sortedData[dataIndex]
                val daysFromStart = ChronoUnit.DAYS.between(firstDate, dateEntry.weightDate).toFloat()
                val x = leftPadding + (daysFromStart / xRange) * graphWidth

                // Draw tick mark
                drawLine(
                    color = axisColor,
                    start = Offset(x, topPadding + graphHeight),
                    end = Offset(x, topPadding + graphHeight + 5f),
                    strokeWidth = 2f
                )

                // Draw label
                drawContext.canvas.nativeCanvas.drawText(
                    dateEntry.weightDate.format(dateFormatter),
                    x,
                    topPadding + graphHeight + 30f,
                    textPaint
                )
            }

            // 5. Map Data to Screen Coordinates
            val points = sortedData.map { item ->
                val daysFromStart = ChronoUnit.DAYS.between(firstDate, item.weightDate).toFloat()

                // Calculate X position within the graph area
                val x = leftPadding + (daysFromStart / xRange) * graphWidth

                // Calculate Y position within the graph area (inverted)
                val normalizedWeight = (item.weightKg.toFloat() - yMin) / yRange
                val y = topPadding + graphHeight - (normalizedWeight * graphHeight)

                Offset(x, y)
            }

            // 6. Draw Line Path
            val path = Path().apply {
                points.forEachIndexed { index, point ->
                    if (index == 0) moveTo(point.x, point.y)
                    else lineTo(point.x, point.y)
                }
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // 7. Draw Points (Dots)
            points.forEach { point ->
                drawCircle(
                    color = pointColor,
                    center = point,
                    radius = 5.dp.toPx()
                )
                // Draw white center for better visibility
                drawCircle(
                    color = Color.White,
                    center = point,
                    radius = 2.dp.toPx()
                )
            }
        }
    }
}
