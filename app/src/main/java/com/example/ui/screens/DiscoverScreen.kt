package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.viewmodel.SportsViewModel

@Composable
fun DiscoverScreen(
    viewModel: SportsViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.categories.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val primaryColor = themeConfig.primaryColorHex.toColor()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp) // padding for bottom navigation
    ) {
        Text(
            text = "DISCOVER SPORTS",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Text(
            text = "Browse live coverage, pre-recorded highlights & analytical columns",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (selectedCategory != null) {
            // Category detail view
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.selectCategory(null) },
                        modifier = Modifier.testTag("btn_back_categories")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryColor)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedCategory!!,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${posts.count { it.category == selectedCategory }} items",
                    color = primaryColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            val categoryPosts = posts.filter { it.category.equals(selectedCategory, ignoreCase = true) }

            if (categoryPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No coverage currently available under $selectedCategory", color = Color.Gray)
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    categoryPosts.forEach { post ->
                        LatestPostRowItem(
                            post = post,
                            primaryColor = primaryColor,
                            themeConfig = themeConfig,
                            onClick = { onNavigateToDetail(post.id) }
                        )
                    }
                }
            }
        } else {
            // Default Category Grid View
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { cat ->
                    val postCount = posts.count { it.category.equals(cat.name, ignoreCase = true) }
                    val sportIcon = when (cat.name.lowercase()) {
                        "cricket" -> Icons.Default.SportsCricket
                        "football" -> Icons.Default.SportsSoccer
                        "basketball" -> Icons.Default.SportsBasketball
                        "tennis" -> Icons.Default.SportsTennis
                        else -> Icons.Default.Sports
                    }

                    GlassmorphicCard(
                        cornerRadius = 16.dp,
                        cardStyle = themeConfig.cardStyle,
                        modifier = Modifier
                            .aspectRatio(1.2f)
                            .clickable { viewModel.selectCategory(cat.name) }
                            .testTag("category_grid_item_${cat.name.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x05FFFFFF),
                                            primaryColor.copy(alpha = 0.08f)
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Icon(
                                sportIcon,
                                contentDescription = cat.name,
                                tint = primaryColor,
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.TopStart)
                            )

                            Column(
                                modifier = Modifier.align(Alignment.BottomStart)
                            ) {
                                Text(
                                    text = cat.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$postCount broadcast${if (postCount == 1) "" else "s"}",
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
