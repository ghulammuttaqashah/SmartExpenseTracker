package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.Expense
import com.example.madproject.data.ExpenseRepository
import com.example.madproject.data.SettingsDataStore
import com.example.madproject.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    repository: ExpenseRepository,
    settings: SettingsDataStore
) : ViewModel() {
    private val monthRange = DateUtils.currentMonthRange()

    private val monthlyTotal = repository.sumByRange(monthRange.first, monthRange.second)
    private val recentExpenses = repository.getAll()
    private val budget = settings.monthlyBudget

    val uiState: StateFlow<HomeUiState> = combine(
        monthlyTotal,
        recentExpenses,
        budget
    ) { total, expenses, budgetAmount ->
        HomeUiState(
            monthTotal = total,
            budget = budgetAmount,
            recent = expenses.take(5)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}

data class HomeUiState(
    val monthTotal: Double = 0.0,
    val budget: Double = 0.0,
    val recent: List<Expense> = emptyList()
)
