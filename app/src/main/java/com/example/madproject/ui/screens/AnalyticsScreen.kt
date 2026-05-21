package com.example.madproject.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madproject.ui.components.CategoryPieChart
import com.example.madproject.ui.components.EmptyState
import com.example.madproject.ui.components.GlassCard
import com.example.madproject.ui.viewmodel.AnalyticsViewModel
import com.example.madproject.ui.viewmodel.AppViewModelFactory
import com.example.madproject.util.FormatUtils

@Composable
fun AnalyticsScreen(factory: AppViewModelFactory) {
    val viewModel: AnalyticsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.headlineSmall
            )

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "Monthly total", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = FormatUtils.formatCurrency(uiState.monthTotal),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (uiState.categories.isNotEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = "Spending by category", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoryPieChart(categories = uiState.categories)
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = "Category totals", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    uiState.categories.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(item.category.color)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = item.category.label,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = FormatUtils.formatCurrency(item.total),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        } else {
            EmptyState(
                title = "No analytics yet",
                message = "Add expenses to see your monthly insights."
            )
        }
        }
    }
}
