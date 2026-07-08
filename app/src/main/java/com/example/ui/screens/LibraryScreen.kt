package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.screens.toColor
import com.example.data.SportsPostEntity
import androidx.compose.ui.text.style.TextOverflow
import com.example.viewmodel.SportsViewModel

@Composable
fun LibraryScreen(
    viewModel: SportsViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()

    val primaryColor = themeConfig.primaryColorHex.toColor()

    var activeTab by remember { mutableStateOf(0) } // 0 = Downloads / Saved, 1 = Favorites

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp) // Leave space for bottom nav
    ) {
        Text(
            text = "ZEN LIBRARY",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Text(
            text = "Your offline savings, bookmarks, and favorite live streams",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Custom Glassmorphic Tab Row
        GlassmorphicCard(
            cornerRadius = 12.dp,
            cardStyle = themeConfig.cardStyle,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(bottom = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (activeTab == 0) primaryColor.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { activeTab = 0 }
                        .testTag("tab_downloads"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Downloads",
                            tint = if (activeTab == 0) primaryColor else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Downloads & Saved",
                            color = if (activeTab == 0) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (activeTab == 1) primaryColor.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { activeTab = 1 }
                        .testTag("tab_favorites"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = if (activeTab == 1) primaryColor else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Favorites",
                            color = if (activeTab == 1) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Contents
        if (activeTab == 0) {
            // Downloads / Saved Content
            if (downloads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.DownloadForOffline,
                            contentDescription = "No downloads",
                            tint = Color.Gray,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No Offline Broadcasts Saved Yet",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap the download icon on your favorite match or recap",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    downloads.forEach { download ->
                        val post = posts.find { it.id == download.postId }
                        if (post != null) {
                            DownloadRowItem(
                                post = post,
                                status = download.status,
                                progress = downloadProgress[post.id] ?: 0,
                                primaryColor = primaryColor,
                                themeConfig = themeConfig,
                                onNavigate = { onNavigateToDetail(post.id) },
                                onDelete = { viewModel.deleteDownload(post.id) }
                            )
                        }
                    }
                }
            }
        } else {
            // Favorites Content
            val favoritePosts = posts.filter { post -> favorites.any { it.postId == post.id } }

            if (favoritePosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = "No favorites",
                            tint = Color.Gray,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No Bookmarks Yet",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Double tap or heart a post to keep track here",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    favoritePosts.forEach { post ->
                        LatestPostRowItem(
                            post = post,
                            primaryColor = primaryColor,
                            themeConfig = themeConfig,
                            onClick = { onNavigateToDetail(post.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadRowItem(
    post: SportsPostEntity,
    status: String,
    progress: Int,
    primaryColor: Color,
    themeConfig: com.example.data.UserThemeEntity,
    onNavigate: () -> Unit,
    onDelete: () -> Unit
) {
    GlassmorphicCard(
        cornerRadius = 12.dp,
        cardStyle = themeConfig.cardStyle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onNavigate)
            .testTag("download_row_${post.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DownloadForOffline,
                        contentDescription = "Offline content icon",
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (post.downloadable) "OFFLINE STREAM RECORDING" else "SAVED METADATA",
                        color = primaryColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when (status) {
                            "queued" -> "Waiting to download..."
                            "downloading" -> "Downloading: $progress%"
                            "complete" -> "Available Offline"
                            else -> "Failed"
                        },
                        color = if (status == "complete") Color.Green else Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete Download Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("btn_delete_download_${post.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete download", tint = Color.Red.copy(alpha = 0.8f))
                }
            }

            // Progress bar if downloading
            if (status == "downloading") {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = primaryColor,
                    trackColor = Color(0x22FFFFFF)
                )
            }
        }
    }
}
