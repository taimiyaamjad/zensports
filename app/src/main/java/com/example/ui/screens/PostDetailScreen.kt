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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SportsPostEntity
import com.example.ui.components.GlassmorphicCard
import com.example.viewmodel.SportsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    viewModel: SportsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val allPostsAdmin by viewModel.allPostsForAdmin.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()

    val primaryColor = themeConfig.primaryColorHex.toColor()

    // Find post from published or admin lists
    val post = posts.find { it.id == postId } ?: allPostsAdmin.find { it.id == postId }

    val isFavorite = favorites.any { it.postId == postId }
    val downloadInfo = downloads.find { it.postId == postId }

    // Increment view count
    LaunchedEffect(postId) {
        viewModel.incrementViewCount(postId)
    }

    // Mock Live Chat messages
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                "🔥 ZenSports Stream is incredibly smooth!",
                "Incredible tactics on display today!",
                "Unbelievable defensive play here!",
                "Wow, what a move!! 🔥👏",
                "Is anyone else experiencing that insane wind in the stadium?",
                "Match of the century absolute classic!"
            )
        )
    }

    LaunchedEffect(post?.isLive) {
        if (post?.isLive == true) {
            val names = listOf("Alex_9", "Strikerr", "FanaticSports", "Rover_X", "LeoFan10", "Gamer_23")
            val texts = listOf(
                "AMAZING TEAMPLAY!! ⚽🏆",
                "He's unstoppable today!",
                "Unreal precision on that attempt!",
                "Absolute beauty of a shot!",
                "LET'S GO TEAM ZEN!! 🔴🔥",
                "Outstanding coverage ZenSports!"
            )
            while (true) {
                delay(3000)
                val randomMessage = "${names.random()}: ${texts.random()}"
                chatMessages = (chatMessages + randomMessage).takeLast(6)
            }
        }
    }

    if (post == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Sports post not found", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0E14),
        topBar = {
            TopAppBar(
                title = { Text(post.category.uppercase(), fontWeight = FontWeight.Bold, color = primaryColor, letterSpacing = 1.5.sp, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back_detail")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFavorite(post.id) },
                        modifier = Modifier.testTag("btn_favorite_detail")
                    ) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Interactive Glassmorphic Broadcast Screen
            GlassmorphicCard(
                cornerRadius = 16.dp,
                cardStyle = themeConfig.cardStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .testTag("broadcast_player_console")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF141722),
                                    Color(0xFF0C0E14)
                                )
                            )
                        )
                ) {
                    // Custom aesthetic grid background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridCount = 8
                        val rowStep = size.height / gridCount
                        val colStep = size.width / gridCount
                        for (i in 0..gridCount) {
                            drawLine(
                                color = Color(0x05FFFFFF),
                                start = androidx.compose.ui.geometry.Offset(0f, i * rowStep),
                                end = androidx.compose.ui.geometry.Offset(size.width, i * rowStep),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color(0x05FFFFFF),
                                start = androidx.compose.ui.geometry.Offset(i * colStep, 0f),
                                end = androidx.compose.ui.geometry.Offset(i * colStep, size.height),
                                strokeWidth = 1f
                            )
                        }
                    }

                    if (post.isLive) {
                        // Live Console Layout
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color.Red, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "LIVE STREAM",
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    "1080p60 • HD",
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            // Interactive center play circle
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(primaryColor.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, primaryColor, CircleShape)
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Stream playing", tint = primaryColor, modifier = Modifier.size(28.dp))
                            }

                            // Interactive telemetry metrics line
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "🎥 Sub-Feed: CAM-1 (Stadium Main)",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "⚡ Bitrate: 8.4 Mbps",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    } else {
                        // Scheduled / Ended Post Console
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val scheduledAt = post.scheduledAt
                            if (scheduledAt != null && scheduledAt > System.currentTimeMillis()) {
                                Icon(Icons.Outlined.Alarm, contentDescription = "Upcoming", tint = primaryColor, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Upcoming Broadcast Event", color = Color.White, fontWeight = FontWeight.Bold)
                                Text(
                                    "Scheduled for " + java.text.SimpleDateFormat("MMM dd, yyyy - hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(scheduledAt)),
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                Icon(Icons.Default.PlayCircleOutline, contentDescription = "Post live replay", tint = primaryColor, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Event Broadcast Concluded", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Click to stream full match recording", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Block
            Text(
                text = post.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Box(
                    modifier = Modifier
                        .background(primaryColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        post.category.uppercase(),
                        color = primaryColor,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // View Count
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.TrendingUp, contentDescription = "Views", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    Text(
                        "${post.viewCount} spectators",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row: Download or Save Metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (post.downloadable) {
                    val progress = downloadProgress[post.id]
                    val isDownloading = progress != null

                    Button(
                        onClick = {
                            if (downloadInfo == null) {
                                viewModel.startDownload(post.id)
                            }
                        },
                        enabled = !isDownloading && downloadInfo?.status != "complete",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_download_post"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            disabledContainerColor = primaryColor.copy(alpha = 0.4f),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                if (downloadInfo?.status == "complete") Icons.Default.CheckCircle else Icons.Default.Download,
                                contentDescription = "Download"
                            )
                            Text(
                                text = when {
                                    isDownloading -> "Downloading: $progress%"
                                    downloadInfo?.status == "complete" -> "Saved Offline"
                                    else -> "Download Stream"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Regular stream: Allows saving details to Library for quick reference
                    Button(
                        onClick = {
                            if (downloadInfo == null) {
                                viewModel.startDownload(post.id) // saves metadata
                            }
                        },
                        enabled = downloadInfo == null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_save_metadata_post"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x11FFFFFF),
                            disabledContainerColor = Color(0x05FFFFFF),
                            contentColor = Color.White,
                            disabledContentColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                if (downloadInfo != null) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save Metadata"
                            )
                            Text(
                                text = if (downloadInfo != null) "Saved to Library" else "Save for Later",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Description Text
            Text(
                "SUMMARY & BROADCAST DETAIL",
                color = primaryColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.description,
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            // Live spectator scrolling chat overlay if match is active
            if (post.isLive) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "🔴 LIVE SPECTATOR COMMENTARY",
                    color = Color.Red,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                GlassmorphicCard(
                    cornerRadius = 12.dp,
                    cardStyle = themeConfig.cardStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        chatMessages.forEach { msg ->
                            Text(
                                text = msg,
                                color = if (msg.contains("user_")) primaryColor else Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
