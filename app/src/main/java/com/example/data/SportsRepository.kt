package com.example.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class SportsRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "zensports_database"
    ).build()

    val sportsPostDao = db.sportsPostDao()
    val categoryDao = db.categoryDao()
    val favoriteDao = db.favoriteDao()
    val downloadDao = db.downloadDao()
    val userThemeDao = db.userThemeDao()

    suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        // Seed categories if empty
        val existingCategories = categoryDao.getAllCategories().firstOrNull()
        if (existingCategories.isNullOrEmpty()) {
            val defaultCategories = listOf(
                CategoryEntity("1", "Cricket", "sports_cricket", 1),
                CategoryEntity("2", "Football", "sports_soccer", 2),
                CategoryEntity("3", "Kabaddi", "sports_kabaddi", 3),
                CategoryEntity("4", "Basketball", "sports_basketball", 4),
                CategoryEntity("5", "Tennis", "sports_tennis", 5)
            )
            defaultCategories.forEach { categoryDao.insertCategory(it) }
        }
    }
}
