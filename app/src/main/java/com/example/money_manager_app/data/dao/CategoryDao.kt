package com.example.money_manager_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.CategoryWithTransfer
import com.example.money_manager_app.data.model.entity.enums.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getCategory() : Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCategory(listCategory : List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun editCategory(category: Category)

    @Query("SELECT * FROM category")
    fun getAllCategory() : List<Category>

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("DELETE FROM category WHERE category_id = :categoryId")
    suspend fun deleteCategory(categoryId: Long)

    @Query("SELECT * FROM category WHERE account_id = :accountId")
    fun getCategoriesWithTransferByAccountId(accountId: Long): List<CategoryWithTransfer>

    @Query("SELECT * FROM category WHERE account_id = :accountId")
    fun getCategoriesByAccountId(accountId: Long): Flow<List<Category>>
}