package com.example.madproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.madproject.data.Category
import com.example.madproject.data.Expense
import com.example.madproject.ui.components.CategoryChip
import com.example.madproject.ui.components.EmptyState
import com.example.madproject.ui.components.ExpenseItem
import com.example.madproject.ui.components.GlassCard
import com.example.madproject.ui.viewmodel.AppViewModelFactory
import com.example.madproject.ui.viewmodel.HistoryViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(factory: AppViewModelFactory, onEdit: (Long) -> Unit) {
    val viewModel: HistoryViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var deleteTarget by remember { mutableStateOf<Expense?>(null) }
    var deleteSelectedDialog by remember { mutableStateOf(false) }
    var clearAllDialog by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    val selectedCount = selectedIds.size
    val scope = rememberCoroutineScope()

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
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Expense history",
                style = MaterialTheme.typography.headlineSmall
            )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                AssistChip(
                    onClick = {
                        selectionMode = !selectionMode
                        if (!selectionMode) {
                            selectedIds = emptySet()
                        }
                    },
                    label = { Text(if (selectionMode) "Done" else "Select") },
                    colors = AssistChipDefaults.assistChipColors()
                )
            }
            item {
                AssistChip(
                    onClick = {
                        selectedIds = uiState.expenses.map { it.id }.toSet()
                        selectionMode = true
                    },
                    label = { Text("Select all") },
                    enabled = uiState.expenses.isNotEmpty()
                )
            }
            item {
                AssistChip(
                    onClick = { deleteSelectedDialog = true },
                    label = { Text("Delete ($selectedCount)") },
                    enabled = selectionMode && selectedIds.isNotEmpty()
                )
            }
            item {
                AssistChip(
                    onClick = { clearAllDialog = true },
                    label = { Text("Clear all") },
                    enabled = uiState.expenses.isNotEmpty()
                )
            }
        }

            GlassCard {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::onQueryChange,
                    label = { Text("Search expenses") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                Category.values().forEach { category ->
                    CategoryChip(
                        category = category,
                        selected = uiState.category == category,
                        onSelected = { selected ->
                            viewModel.onCategorySelected(if (uiState.category == selected) null else selected)
                        }
                    )
                }
            }

        Spacer(modifier = Modifier.height(4.dp))

            if (uiState.expenses.isEmpty()) {
                EmptyState(
                    title = "Nothing to show",
                    message = "Try adjusting the search or filter to see results."
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.expenses, key = { it.id }) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onEdit = if (selectionMode) null else onEdit,
                            onDelete = if (selectionMode) null else { target -> deleteTarget = target },
                            selectable = selectionMode,
                            selected = selectedIds.contains(expense.id),
                            onSelectToggle = {
                                selectedIds = if (selectedIds.contains(expense.id)) {
                                    selectedIds - expense.id
                                } else {
                                    selectedIds + expense.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete expense") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    val target = deleteTarget
                    if (target != null) {
                        scope.launch { viewModel.deleteExpense(target) }
                    }
                    deleteTarget = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (deleteSelectedDialog) {
        AlertDialog(
            onDismissRequest = { deleteSelectedDialog = false },
            title = { Text("Delete selected") },
            text = { Text("Remove $selectedCount selected expenses?") },
            confirmButton = {
                TextButton(onClick = {
                    val ids = selectedIds
                    scope.launch { viewModel.deleteSelected(ids) }
                    selectedIds = emptySet()
                    selectionMode = false
                    deleteSelectedDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteSelectedDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (clearAllDialog) {
        AlertDialog(
            onDismissRequest = { clearAllDialog = false },
            title = { Text("Clear all expenses") },
            text = { Text("This will remove all expenses for a fresh month. Continue?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { viewModel.clearAll() }
                    selectedIds = emptySet()
                    selectionMode = false
                    clearAllDialog = false
                }) {
                    Text("Clear all")
                }
            },
            dismissButton = {
                TextButton(onClick = { clearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
