package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sports_posts")
data class SportsPostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val liveLink: String,
    val thumbnailUrl: String,
    val category: String, // e.g. "Cricket", "Football", "Kabaddi"
    val isLive: Boolean,
    val scheduledAt: Long?,
    val status: String, // "draft", "published", "archived"
    val downloadable: Boolean,
    val downloadUrl: String?,
    val viewCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isServerPost: Boolean = true // If true, it simulates "Firestore" live sync
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String, // Material Icon identifier
    val sortOrder: Int
)

@Entity(tableName = "favorites", primaryKeys = ["uid", "postId"])
data class FavoriteEntity(
    val uid: String,
    val postId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads", primaryKeys = ["uid", "postId"])
data class DownloadEntity(
    val uid: String,
    val postId: String,
    val localPath: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val status: String // "queued", "downloading", "complete", "failed"
)

@Entity(tableName = "user_themes")
data class UserThemeEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val location: String,
    val language: String,
    val isAdmin: Boolean,
    val primaryColorHex: String, // e.g. "#00F2FE" (Neon Cyan)
    val cardStyle: String, // "apollo" (glassmorphic blurred), "flat" (solid cards)
    val appBarBlur: Boolean,
    val appBarOverlay: Boolean,
    val appBarCornerRadius: Int,
    val appBarImageUrl: String?,
    val homeBgImageUrl: String?,
    val homeGrid: Boolean,
    val homeBlur: Boolean
)
