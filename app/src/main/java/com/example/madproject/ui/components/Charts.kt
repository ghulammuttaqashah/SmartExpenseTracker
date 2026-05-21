package com.example.madproject.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.madproject.ui.viewmodel.CategoryTotalUi
import kotlin.math.max

@Composable
fun CategoryBarChart(categories: List<CategoryTotalUi>) {
    val maxValue = categories.maxOfOrNull { it.total } ?: 1.0

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(8.dp)
    ) {
        val barWidth = size.width / max(categories.size, 1)
        categories.forEachIndexed { index, category ->
            val heightRatio = if (maxValue == 0.0) 0.0 else category.total / maxValue
            val barHeight = (size.height * heightRatio).toFloat()
            val barWidthSafe = (barWidth - 24).coerceAtLeast(8f)
            drawRect(
                color = category.category.color,
                topLeft = Offset(x = index * barWidth + 12, y = size.height - barHeight),
                size = Size(width = barWidthSafe, height = barHeight)
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        categories.forEach { item ->
            Text(
                text = item.category.label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
