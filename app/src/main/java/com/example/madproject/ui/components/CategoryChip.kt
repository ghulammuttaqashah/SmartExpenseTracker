package com.example.madproject.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.madproject.data.Category

@Composable
fun CategoryChip(
    category: Category,
    selected: Boolean,
    onSelected: (Category) -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = { onSelected(category) },
        label = { Text(category.label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = category.color.copy(alpha = 0.2f),
            selectedLabelColor = Color.Unspecified
        )
    )
}
