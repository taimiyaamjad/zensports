package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class SportsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SportsRepository(application)

    // Current Auth State
    private val _currentUserId = MutableStateFlow("user_anon_" + UUID.randomUUID().toString().take(6))
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminEmail = MutableStateFlow("admin@zensports.com")
    val adminEmail: StateFlow<String> = _adminEmail.asStateFlow()

    // Active Category Filter
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Active Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // DB flows
    val categories: StateFlow<List<CategoryEntity>> = repository.categoryDao.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<SportsPostEntity>> = repository.sportsPostDao.getPublishedPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPostsForAdmin: StateFlow<List<SportsPostEntity>> = repository.sportsPostDao.getAllServerPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteEntity>> = _currentUserId.flatMapLatest { uid ->
        repository.favoriteDao.getFavoritesForUser(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadEntity>> = _currentUserId.flatMapLatest { uid ->
        repository.downloadDao.getDownloadsForUser(uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Theme Config flow
    val themeConfig: StateFlow<UserThemeEntity> = _currentUserId.flatMapLatest { uid ->
        repository.userThemeDao.getThemeForUser(uid).map { theme ->
            theme ?: UserThemeEntity(
                uid = uid,
                name = "",
                location = "",
                language = "",
                isAdmin = false,
                primaryColorHex = "#00F2FE", // Neon Cyan
                cardStyle = "apollo", // glassmorphic
                appBarBlur = true,
                appBarOverlay = true,
                appBarCornerRadius = 16,
                appBarImageUrl = null,
                homeBgImageUrl = null,
                homeGrid = true,
                homeBlur = false
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserThemeEntity(
            uid = "",
            name = "",
            location = "",
            language = "",
            isAdmin = false,
            primaryColorHex = "#00F2FE",
            cardStyle = "apollo",
            appBarBlur = true,
            appBarOverlay = true,
            appBarCornerRadius = 16,
            appBarImageUrl = null,
            homeBgImageUrl = null,
            homeGrid = true,
            homeBlur = false
        )
    )

    // Download in-progress simulation map (postId -> progress percentage)
    private val _downloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Int>> = _downloadProgress.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedIfNeeded()
        }
    }

    // Auth Actions
    fun loginAsAdmin(email: String, pword: String): Boolean {
        if (email.trim().lowercase() == _adminEmail.value && pword == "password123") {
            _isAdminLoggedIn.value = true
            viewModelScope.launch(Dispatchers.IO) {
                val currentTheme = themeConfig.value
                repository.userThemeDao.insertTheme(currentTheme.copy(isAdmin = true))
            }
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        viewModelScope.launch(Dispatchers.IO) {
            val currentTheme = themeConfig.value
            repository.userThemeDao.insertTheme(currentTheme.copy(isAdmin = false))
        }
    }

    // Category / Filter Actions
    fun selectCategory(categoryName: String?) {
        _selectedCategory.value = categoryName
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Favorite Actions
    fun toggleFavorite(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = _currentUserId.value
            val isFav = favorites.value.any { it.postId == postId }
            if (isFav) {
                repository.favoriteDao.deleteFavorite(uid, postId)
            } else {
                repository.favoriteDao.insertFavorite(FavoriteEntity(uid, postId))
            }
        }
    }

    fun isFavoritePost(postId: String): Flow<Boolean> {
        return repository.favoriteDao.isFavorite(_currentUserId.value, postId)
    }

    // Download Simulation Flow
    fun startDownload(postId: String) {
        val uid = _currentUserId.value
        viewModelScope.launch(Dispatchers.IO) {
            // Write "queued" status to database
            repository.downloadDao.insertDownload(
                DownloadEntity(
                    uid = uid,
                    postId = postId,
                    localPath = "offline_media/$postId.mp4",
                    status = "queued"
                )
            )

            // Start simulated downloading sequence
            _downloadProgress.update { it + (postId to 0) }
            repository.downloadDao.insertDownload(
                DownloadEntity(
                    uid = uid,
                    postId = postId,
                    localPath = "offline_media/$postId.mp4",
                    status = "downloading"
                )
            )

            for (progress in 10..100 step 15) {
                delay(400)
                _downloadProgress.update { it + (postId to progress.coerceAtMost(100)) }
            }

            // Mark complete in database
            repository.downloadDao.insertDownload(
                DownloadEntity(
                    uid = uid,
                    postId = postId,
                    localPath = "offline_media/$postId.mp4",
                    status = "complete"
                )
            )
            _downloadProgress.update { it - postId }
        }
    }

    fun deleteDownload(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.downloadDao.deleteDownload(_currentUserId.value, postId)
        }
    }

    // Admin Panel Database CRUD Operations
    fun createPost(
        title: String,
        description: String,
        liveLink: String,
        category: String,
        isLive: Boolean,
        scheduledAt: Long?,
        downloadable: Boolean,
        downloadUrl: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val newPost = SportsPostEntity(
                id = "post_" + UUID.randomUUID().toString().take(8),
                title = title,
                description = description,
                liveLink = liveLink,
                thumbnailUrl = "",
                category = category,
                isLive = isLive,
                scheduledAt = scheduledAt,
                status = "published",
                downloadable = downloadable,
                downloadUrl = downloadUrl,
                viewCount = 0,
                createdAt = now,
                updatedAt = now,
                isServerPost = true
            )
            repository.sportsPostDao.insertPost(newPost)
        }
    }

    fun updatePostDetails(post: SportsPostEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sportsPostDao.updatePost(post.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deletePostById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sportsPostDao.deletePostById(id)
        }
    }

    fun incrementViewCount(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val post = repository.sportsPostDao.getPostByIdSuspend(postId)
            if (post != null) {
                repository.sportsPostDao.updatePost(post.copy(viewCount = post.viewCount + 1))
            }
        }
    }

    // Profile Settings Actions
    fun updateProfile(name: String, location: String, language: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTheme = themeConfig.value
            repository.userThemeDao.insertTheme(
                currentTheme.copy(
                    name = name,
                    location = location,
                    language = language
                )
            )
        }
    }

    // Theme Config Actions
    fun updateThemeConfig(
        primaryColorHex: String? = null,
        cardStyle: String? = null,
        appBarBlur: Boolean? = null,
        appBarOverlay: Boolean? = null,
        appBarCornerRadius: Int? = null,
        appBarImageUrl: String? = null,
        homeBgImageUrl: String? = null,
        homeGrid: Boolean? = null,
        homeBlur: Boolean? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentTheme = themeConfig.value
            val updatedTheme = currentTheme.copy(
                primaryColorHex = primaryColorHex ?: currentTheme.primaryColorHex,
                cardStyle = cardStyle ?: currentTheme.cardStyle,
                appBarBlur = appBarBlur ?: currentTheme.appBarBlur,
                appBarOverlay = appBarOverlay ?: currentTheme.appBarOverlay,
                appBarCornerRadius = appBarCornerRadius ?: currentTheme.appBarCornerRadius,
                appBarImageUrl = appBarImageUrl ?: currentTheme.appBarImageUrl,
                homeBgImageUrl = homeBgImageUrl ?: currentTheme.homeBgImageUrl,
                homeGrid = homeGrid ?: currentTheme.homeGrid,
                homeBlur = homeBlur ?: currentTheme.homeBlur
            )
            repository.userThemeDao.insertTheme(updatedTheme)
        }
    }
}
