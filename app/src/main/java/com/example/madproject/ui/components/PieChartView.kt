package com.example.madproject.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.example.madproject.ui.viewmodel.CategoryTotalUi
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun CategoryPieChart(categories: List<CategoryTotalUi>, modifier: Modifier = Modifier) {
    val entries = categories.map { item ->
        PieEntry(item.total.toFloat(), item.category.label)
    }
    val colors = categories.map { it.category.color.toArgb() }
    val labelColor = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                holeRadius = 62f
                transparentCircleRadius = 68f
                setUsePercentValues(false)
                setEntryLabelColor(labelColor)
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val dataSet = PieDataSet(entries, "").apply {
                setDrawValues(false)
                this.colors = if (colors.isEmpty()) listOf(Color.GRAY) else colors
            }
            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}
