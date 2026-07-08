package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SportsPostEntity
import com.example.ui.components.GlassmorphicCard
import com.example.viewmodel.SportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: SportsViewModel,
    onNavigateToNewPost: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeConfig by viewModel.themeConfig.collectAsState()
    val posts by viewModel.allPostsForAdmin.collectAsState()

    val primaryColor = themeConfig.primaryColorHex.toColor()

    // Aggregate Analytics
    val totalViews = posts.sumOf { it.viewCount }
    val liveBroadcastsCount = posts.count { it.isLive }
    val totalBroadcasts = posts.size

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0E14),
        topBar = {
            TopAppBar(
                title = { Text("ADMIN PANEL", fontWeight = FontWeight.Bold, color = primaryColor, letterSpacing = 1.5.sp, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back_admin_dashboard")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logoutAdmin(); onNavigateBack() },
                        modifier = Modifier.testTag("btn_logout_admin")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToNewPost,
                icon = { Icon(Icons.Default.Add, contentDescription = "New Broadcast") },
                text = { Text("Create Live", fontWeight = FontWeight.Bold) },
                containerColor = primaryColor,
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .testTag("fab_create_post")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Analytics Row
            Text(
                "BROADCAST TELEMETRY",
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Views card
                GlassmorphicCard(
                    cornerRadius = 12.dp,
                    cardStyle = themeConfig.cardStyle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Spectators", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            totalViews.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Active Live broadcasts card
                GlassmorphicCard(
                    cornerRadius = 12.dp,
                    cardStyle = themeConfig.cardStyle,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Live Streams", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (liveBroadcastsCount > 0) Color.Red else Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                liveBroadcastsCount.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Post list header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "MANAGE BROADCAST CHANNELS ($totalBroadcasts)",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (posts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No broadcast posts created yet. Tap '+' to create.", color = Color.Gray)
                }
            } else {
                posts.forEach { post ->
                    AdminPostRowItem(
                        post = post,
                        primaryColor = primaryColor,
                        themeConfig = themeConfig,
                        onToggleLive = {
                            viewModel.updatePostDetails(post.copy(isLive = !post.isLive))
                        },
                        onDelete = {
                            viewModel.deletePostById(post.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminPostRowItem(
    post: SportsPostEntity,
    primaryColor: Color,
    themeConfig: com.example.data.UserThemeEntity,
    onToggleLive: () -> Unit,
    onDelete: () -> Unit
) {
    GlassmorphicCard(
        cornerRadius = 12.dp,
        cardStyle = themeConfig.cardStyle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("admin_post_item_${post.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sport Badge icon box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    val sportIcon = when (post.category.lowercase()) {
                        "cricket" -> Icons.Default.SportsCricket
                        "football" -> Icons.Default.SportsSoccer
                        "basketball" -> Icons.Default.SportsBasketball
                        "tennis" -> Icons.Default.SportsTennis
                        else -> Icons.Default.Sports
                    }
                    Icon(sportIcon, contentDescription = post.category, tint = primaryColor)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(post.category.uppercase(), color = primaryColor, style = MaterialTheme.typography.labelSmall)
                        Text("•", color = Color.Gray)
                        Text("${post.viewCount} views", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Delete Button
                IconButton(onClick = onDelete, modifier = Modifier.testTag("btn_delete_post_${post.id}")) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete broadcast", tint = Color.Red.copy(alpha = 0.8f))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0x11FFFFFF))

            // Action row inside card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(if (post.isLive) Color.Red else Color.Gray, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (post.isLive) "STREAMING LIVE" else "REPLAY MODE",
                        color = if (post.isLive) Color.Red else Color.LightGray,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black
                    )
                }

                // Toggle Live state button
                Button(
                    onClick = onToggleLive,
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("btn_toggle_live_${post.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (post.isLive) Color.Gray else primaryColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (post.isLive) "Stop Live" else "Go Live Now",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
