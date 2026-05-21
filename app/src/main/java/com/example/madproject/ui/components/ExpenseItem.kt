package com.example.madproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.madproject.data.Category
import com.example.madproject.data.Expense
import com.example.madproject.util.DateUtils
import com.example.madproject.util.FormatUtils

@Composable
fun ExpenseItem(
    expense: Expense,
    modifier: Modifier = Modifier,
    onEdit: ((Long) -> Unit)? = null,
    onDelete: ((Expense) -> Unit)? = null,
    selectable: Boolean = false,
    selected: Boolean = false,
    onSelectToggle: (() -> Unit)? = null
) {
    val category = Category.valueOf(expense.category)
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .let { base ->
                if (selectable && onSelectToggle != null) {
                    base.clickable { onSelectToggle() }
                } else {
                    base
                }
            },
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectable) {
                    Checkbox(checked = selected, onCheckedChange = { onSelectToggle?.invoke() })
                }
                Spacer(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(category.color)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = expense.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${category.label} • ${DateUtils.formatDate(expense.date)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = FormatUtils.formatCurrency(expense.amount),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                if (!selectable && onEdit != null) {
                    IconButton(onClick = { onEdit(expense.id) }) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
                    }
                }
                if (!selectable && onDelete != null) {
                    IconButton(onClick = { onDelete(expense) }) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    }
                }
            }
        }
    }
}
