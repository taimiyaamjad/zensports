package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SportsPostEntity::class,
        CategoryEntity::class,
        FavoriteEntity::class,
        DownloadEntity::class,
        UserThemeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sportsPostDao(): SportsPostDao
    abstract fun categoryDao(): CategoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun downloadDao(): DownloadDao
    abstract fun userThemeDao(): UserThemeDao
}
