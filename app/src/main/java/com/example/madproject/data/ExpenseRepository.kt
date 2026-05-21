package com.example.madproject.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ExpenseRepository(private val dao: ExpenseDao) {
    fun getAll(): Flow<List<Expense>> = dao.getAll()

    fun getById(id: Long): Flow<Expense?> = dao.getById(id)

    fun searchAndFilter(query: String?, category: Category?): Flow<List<Expense>> {
        return dao.searchAndFilter(query?.takeIf { it.isNotBlank() }, category?.name)
    }

    fun sumByRange(start: Long, end: Long): Flow<Double> = dao.sumByRange(start, end)

    fun sumByCategory(start: Long, end: Long): Flow<List<CategoryTotal>> = dao.sumByCategory(start, end)

    suspend fun insert(expense: Expense): Long = dao.insert(expense)

    suspend fun update(expense: Expense) = dao.update(expense)

    suspend fun delete(expense: Expense) = dao.delete(expense)

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun deleteByIds(ids: List<Long>) = dao.deleteByIds(ids)

    suspend fun getAllOnce(): List<Expense> = dao.getAll().first()
}
