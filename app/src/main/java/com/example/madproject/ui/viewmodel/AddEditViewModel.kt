package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.Category
import com.example.madproject.data.Expense
import com.example.madproject.data.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState

    fun loadExpense(expenseId: Long) {
        if (expenseId <= 0) return
        viewModelScope.launch {
            val expense = repository.getById(expenseId).first() ?: return@launch
            val amountText = if (expense.amount % 1.0 == 0.0) {
                expense.amount.toLong().toString()
            } else {
                expense.amount.toString()
            }
            _uiState.update {
                it.copy(
                    id = expense.id,
                    title = expense.title,
                    amount = normalizeAmountInput(amountText),
                    category = Category.valueOf(expense.category),
                    dateMillis = expense.date,
                    note = expense.note.orEmpty()
                )
            }
        }
    }

    fun updateTitle(value: String) = updateState { copy(title = value, titleError = null) }

    fun updateAmount(value: String) = updateState {
        copy(amount = normalizeAmountInput(value), amountError = null)
    }

    fun updateCategory(value: Category) = updateState { copy(category = value) }

    fun updateDate(value: Long) = updateState { copy(dateMillis = value) }

    fun updateNote(value: String) = updateState { copy(note = value) }

    fun save(onSuccess: () -> Unit) {
        val current = _uiState.value
        val title = current.title.trim()
        val amount = current.amount.trim().toDoubleOrNull()

        if (title.isEmpty()) {
            updateState { copy(titleError = "Title is required") }
            return
        }
        if (amount == null || amount <= 0.0) {
            updateState { copy(amountError = "Enter a valid amount") }
            return
        }

        viewModelScope.launch {
            val expense = Expense(
                id = current.id,
                title = title,
                amount = amount,
                category = current.category.name,
                date = current.dateMillis,
                note = current.note.trim().ifEmpty { null }
            )
            if (current.id == 0L) {
                repository.insert(expense)
            } else {
                repository.update(expense)
            }
            onSuccess()
        }
    }

    private fun updateState(transform: AddEditUiState.() -> AddEditUiState) {
        _uiState.update(transform)
    }

    private fun normalizeAmountInput(raw: String): String {
        val filtered = raw.filter { it.isDigit() || it == '.' }
        val parts = filtered.split('.', limit = 2)
        return if (parts.size == 1) {
            parts[0]
        } else {
            val integerPart = parts[0]
            val decimalPart = parts[1].filter { it.isDigit() }
            if (decimalPart.isEmpty()) integerPart else "$integerPart.$decimalPart"
        }
    }
}

data class AddEditUiState(
    val id: Long = 0L,
    val title: String = "",
    val amount: String = "",
    val category: Category = Category.Food,
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val titleError: String? = null,
    val amountError: String? = null
)
