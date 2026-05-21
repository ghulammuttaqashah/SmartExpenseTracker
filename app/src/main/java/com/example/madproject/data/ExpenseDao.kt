package com.example.madproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getById(id: Long): Flow<Expense?>

    @Query(
        """
        SELECT * FROM expenses
        WHERE (:query IS NULL OR title LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%')
        AND (:category IS NULL OR category = :category)
        ORDER BY date DESC
        """
    )
    fun searchAndFilter(query: String?, category: String?): Flow<List<Expense>>

    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses WHERE date BETWEEN :start AND :end")
    fun sumByRange(start: Long, end: Long): Flow<Double>

    @Query(
        """
        SELECT category, SUM(amount) as total
        FROM expenses
        WHERE date BETWEEN :start AND :end
        GROUP BY category
        """
    )
    fun sumByCategory(start: Long, end: Long): Flow<List<CategoryTotal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()

    @Query("DELETE FROM expenses WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}

data class CategoryTotal(
    val category: String,
    val total: Double
)
