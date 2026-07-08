package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SportsPostEntity
import com.example.ui.components.GlassmorphicCard
import com.example.viewmodel.SportsViewModel

fun String.toColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: Exception) {
        Color(0xFF00F2FE)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SportsViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val primaryColor = themeConfig.primaryColorHex.toColor()

    // Filter posts
    val filteredPosts = posts.filter { post ->
        val matchesCategory = selectedCategory == null || post.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() || 
                post.title.contains(searchQuery, ignoreCase = true) ||
                post.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val livePosts = filteredPosts.filter { it.isLive }
    val latestPosts = filteredPosts.filter { !it.isLive }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp) // Leave space for bottom nav
    ) {
        // Hero Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_sports_1783444933050),
                contentDescription = "ZenSports Hero Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Overlay gradient for atmosphere
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xAA0C0E14),
                                Color(0xFF0C0E14)
                            )
                        )
                    )
            )

            // Greeting layout
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(primaryColor, CircleShape)
                    )
                    Text(
                        text = "WELCOME BACK",
                        color = primaryColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hey, ${themeConfig.name}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "📍 ${themeConfig.location} • Ready to stream",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("search_input"),
            placeholder = { Text("Search matches, schedules, summaries...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryColor) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = Color.Gray)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color(0x33FFFFFF),
                focusedContainerColor = Color(0x11FFFFFF),
                unfocusedContainerColor = Color(0x08FFFFFF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Categories Horizontal Row
        Text(
            text = "Browse Sports",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) },
                    label = { Text("All Sports") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0x11FFFFFF),
                        labelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedCategory == null,
                        borderColor = Color(0x22FFFFFF),
                        selectedBorderColor = primaryColor
                    ),
                    modifier = Modifier.testTag("category_chip_all")
                )
            }
            items(categories) { cat ->
                val isSelected = selectedCategory == cat.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(cat.name) },
                    label = { Text(cat.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0x11FFFFFF),
                        labelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color(0x22FFFFFF),
                        selectedBorderColor = primaryColor
                    ),
                    modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
                )
            }
        }

        // Live Section
        if (livePosts.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing Red Dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE BROADCASTS",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "${livePosts.size} active",
                    color = primaryColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(livePosts) { post ->
                    LivePostCard(
                        post = post,
                        themeConfig = themeConfig,
                        primaryColor = primaryColor,
                        onClick = { onNavigateToDetail(post.id) }
                    )
                }
            }
        }

        // Upcoming/Scheduled Section
        val upcomingPosts = filteredPosts.filter { it.scheduledAt != null && it.scheduledAt > System.currentTimeMillis() }
        if (upcomingPosts.isNotEmpty()) {
            Text(
                text = "UPCOMING EVENTS",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(upcomingPosts) { post ->
                    UpcomingPostCard(
                        post = post,
                        primaryColor = primaryColor,
                        themeConfig = themeConfig,
                        onClick = { onNavigateToDetail(post.id) }
                    )
                }
            }
        }

        // Latest Section
        Text(
            text = "LATEST SPORTS COVERAGE",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (latestPosts.isEmpty() && livePosts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.SportsSoccer,
                        contentDescription = "No sports coverages found",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No sports coverage matching filter", color = Color.Gray)
                }
            }
        } else {
            latestPosts.forEach { post ->
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

@Composable
fun LivePostCard(
    post: SportsPostEntity,
    themeConfig: com.example.data.UserThemeEntity,
    primaryColor: Color,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        cornerRadius = 16.dp,
        cardStyle = themeConfig.cardStyle,
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clickable(onClick = onClick)
            .testTag("live_card_${post.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0x22FFFFFF), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = post.category.uppercase(),
                        color = primaryColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column {
                Text(
                    text = post.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Viewers",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${post.viewCount} viewers streaming",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun UpcomingPostCard(
    post: SportsPostEntity,
    primaryColor: Color,
    themeConfig: com.example.data.UserThemeEntity,
    onClick: () -> Unit
) {
    val dateString = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        .format(java.util.Date(post.scheduledAt ?: 0))

    GlassmorphicCard(
        cornerRadius = 12.dp,
        cardStyle = themeConfig.cardStyle,
        modifier = Modifier
            .width(200.dp)
            .height(130.dp)
            .clickable(onClick = onClick)
            .testTag("upcoming_card_${post.id}")
    ) {
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
                Box(
                    modifier = Modifier
                        .background(Color(0x3300F2FE), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SCHEDULED",
                        color = primaryColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = post.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Starts at $dateString",
                    color = primaryColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LatestPostRowItem(
    post: SportsPostEntity,
    primaryColor: Color,
    themeConfig: com.example.data.UserThemeEntity,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        cornerRadius = 12.dp,
        cardStyle = themeConfig.cardStyle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("latest_card_${post.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category specific representative sport logo box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.radialGradient(listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent))),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (post.category.lowercase()) {
                    "cricket" -> Icons.Default.SportsCricket
                    "football" -> Icons.Default.SportsSoccer
                    "basketball" -> Icons.Default.SportsBasketball
                    "tennis" -> Icons.Default.SportsTennis
                    else -> Icons.Default.Sports
                }
                Icon(icon, contentDescription = post.category, tint = primaryColor, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = post.category.uppercase(),
                        color = primaryColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (post.downloadable) {
                        Box(
                            modifier = Modifier
                                .background(Color(0x22FFFFFF), CircleShape)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "DOWNLOADABLE",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = post.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = post.description,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Navigate detail",
                tint = Color.Gray
            )
        }
    }
}
