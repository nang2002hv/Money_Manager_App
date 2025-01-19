package com.example.money_manager_app.data.repository

import com.example.money_manager_app.data.dao.CategoryDao
import com.example.money_manager_app.data.model.entity.Category
import com.example.money_manager_app.data.model.entity.CategoryWithTransfer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CategoryRepository {
    suspend fun insertCategory(category: Category)

    suspend fun insertCategory(listCategory : List<Category>)

    suspend fun editCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun deleteCategory(categoryId: Long)

    fun getCategoriesWithTransferByAccountId(accountId: Long): List<CategoryWithTransfer>

    fun getCategory() : Flow<List<Category>>

    fun getAllCategory() : List<Category>

    fun getCategoriesByAccountId(accountId: Long): Flow<List<Category>>
}

class CategoryRepositoryImpl @Inject constructor(private val categoryDao: CategoryDao) : CategoryRepository {
    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    override suspend fun insertCategory(listCategory : List<Category>) {
        categoryDao.insertCategory(listCategory)
    }

    override suspend fun editCategory(category: Category) {
        categoryDao.editCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteCategory(categoryId)
    }

    override fun getCategoriesWithTransferByAccountId(accountId: Long): List<CategoryWithTransfer> {
        return categoryDao.getCategoriesWithTransferByAccountId(accountId)
    }

    override fun getCategory(): Flow<List<Category>> {
        return categoryDao.getCategory()
    }

    override fun getAllCategory(): List<Category> {
        return categoryDao.getAllCategory()
    }

    override fun getCategoriesByAccountId(accountId: Long): Flow<List<Category>> {
        return categoryDao.getCategoriesByAccountId(accountId)
    }
}