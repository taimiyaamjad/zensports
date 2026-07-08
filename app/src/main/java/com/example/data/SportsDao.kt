package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SportsPostDao {
    @Query("SELECT * FROM sports_posts WHERE status = 'published' AND isServerPost = 1 ORDER BY createdAt DESC")
    fun getPublishedPosts(): Flow<List<SportsPostEntity>>

    @Query("SELECT * FROM sports_posts WHERE isServerPost = 1 ORDER BY createdAt DESC")
    fun getAllServerPosts(): Flow<List<SportsPostEntity>>

    @Query("SELECT * FROM sports_posts WHERE id = :id")
    fun getPostById(id: String): Flow<SportsPostEntity?>

    @Query("SELECT * FROM sports_posts WHERE id = :id")
    suspend fun getPostByIdSuspend(id: String): SportsPostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: SportsPostEntity)

    @Update
    suspend fun updatePost(post: SportsPostEntity)

    @Query("DELETE FROM sports_posts WHERE id = :id")
    suspend fun deletePostById(id: String)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE uid = :uid")
    fun getFavoritesForUser(uid: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE uid = :uid AND postId = :postId)")
    fun isFavorite(uid: String, postId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE uid = :uid AND postId = :postId")
    suspend fun deleteFavorite(uid: String, postId: String)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads WHERE uid = :uid")
    fun getDownloadsForUser(uid: String): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE uid = :uid AND postId = :postId")
    fun getDownload(uid: String, postId: String): Flow<DownloadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE uid = :uid AND postId = :postId")
    suspend fun deleteDownload(uid: String, postId: String)
}

@Dao
interface UserThemeDao {
    @Query("SELECT * FROM user_themes WHERE uid = :uid")
    fun getThemeForUser(uid: String): Flow<UserThemeEntity?>

    @Query("SELECT * FROM user_themes WHERE uid = :uid")
    suspend fun getThemeForUserSuspend(uid: String): UserThemeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: UserThemeEntity)
}
