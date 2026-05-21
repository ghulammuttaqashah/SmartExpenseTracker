package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.Category
import com.example.madproject.data.Expense
import com.example.madproject.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class HistoryViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val category = MutableStateFlow<Category?>(null)

    private val expenses = combine(query, category) { q, c ->
        q to c
    }.flatMapLatest { (q, c) ->
        repository.searchAndFilter(q, c)
    }

    val uiState: StateFlow<HistoryUiState> = combine(query, category, expenses) { q, c, list ->
        HistoryUiState(
            query = q,
            category = c,
            expenses = list
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onCategorySelected(value: Category?) {
        category.value = value
    }

    suspend fun deleteExpense(expense: Expense) {
        repository.delete(expense)
    }

    suspend fun deleteSelected(ids: Set<Long>) {
        if (ids.isNotEmpty()) {
            repository.deleteByIds(ids.toList())
        }
    }

    suspend fun clearAll() {
        repository.deleteAll()
    }
}

data class HistoryUiState(
    val query: String = "",
    val category: Category? = null,
    val expenses: List<Expense> = emptyList()
)
