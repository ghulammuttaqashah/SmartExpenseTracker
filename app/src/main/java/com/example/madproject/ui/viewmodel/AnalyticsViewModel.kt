package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.Category
import com.example.madproject.data.CategoryTotal
import com.example.madproject.data.ExpenseRepository
import com.example.madproject.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AnalyticsViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val monthRange = DateUtils.currentMonthRange()

    private val monthTotal = repository.sumByRange(monthRange.first, monthRange.second)
    private val categoryTotals = repository.sumByCategory(monthRange.first, monthRange.second)

    val uiState: StateFlow<AnalyticsUiState> = combine(monthTotal, categoryTotals) { total, categories ->
        val mapped = categories.mapNotNull { item ->
            val category = Category.values().firstOrNull { it.name == item.category }
            if (category != null && item.total > 0.0) {
                CategoryTotalUi(category, item.total)
            } else {
                null
            }
        }
        AnalyticsUiState(total, mapped)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnalyticsUiState())
}

data class AnalyticsUiState(
    val monthTotal: Double = 0.0,
    val categories: List<CategoryTotalUi> = emptyList()
)

data class CategoryTotalUi(
    val category: Category,
    val total: Double
)
