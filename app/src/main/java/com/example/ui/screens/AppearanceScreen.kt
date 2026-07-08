package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.viewmodel.SportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    viewModel: SportsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeConfig by viewModel.themeConfig.collectAsState()
    val primaryColor = themeConfig.primaryColorHex.toColor()

    val swatches = listOf(
        "#00F2FE" to "Neon Cyan",
        "#3DDC84" to "Android Green",
        "#FF2A54" to "Zen Red",
        "#FF9F0A" to "Championship Orange",
        "#7D5260" to "Vintage Burgundy",
        "#2A82FE" to "Cyber Blue"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0E14),
        topBar = {
            TopAppBar(
                title = { Text("THEME ENGINE", fontWeight = FontWeight.Bold, color = primaryColor, letterSpacing = 1.5.sp, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back_appearance")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            Text(
                "ACCENT COLOR PALETTE",
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Palette Swatch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                swatches.forEach { (hex, name) ->
                    val swatchColor = hex.toColor()
                    val isSelected = themeConfig.primaryColorHex.equals(hex, ignoreCase = true)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { viewModel.updateThemeConfig(primaryColorHex = hex) }
                            .testTag("swatch_${hex.replace("#", "")}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(swatchColor)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.Black, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name.substringBefore(" "),
                            color = if (isSelected) Color.White else Color.Gray,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Text(
                "CARD STYLE LAYOUTS",
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
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Glass Card Selection
                GlassmorphicCard(
                    cornerRadius = 12.dp,
                    cardStyle = if (themeConfig.cardStyle == "apollo") "apollo" else "flat",
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { viewModel.updateThemeConfig(cardStyle = "apollo") }
                        .border(
                            width = if (themeConfig.cardStyle == "apollo") 2.dp else 0.dp,
                            color = primaryColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("card_style_apollo")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Apollo Glass", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Flat Card Selection
                GlassmorphicCard(
                    cornerRadius = 12.dp,
                    cardStyle = "flat",
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                        .clickable { viewModel.updateThemeConfig(cardStyle = "flat") }
                        .border(
                            width = if (themeConfig.cardStyle == "flat") 2.dp else 0.dp,
                            color = primaryColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("card_style_flat")
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Flat Solid", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                "INTERFACE OVERLAYS & EFFECTS",
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Toggle Switches Gated in Glass Cards
            GlassmorphicCard(
                cornerRadius = 12.dp,
                cardStyle = themeConfig.cardStyle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // App Bar Blur toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Acrylic Glass Blur", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Adds realistic background refraction", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = themeConfig.appBarBlur,
                            onCheckedChange = { viewModel.updateThemeConfig(appBarBlur = it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("switch_app_bar_blur")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Background Grid toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Aesthetic Grid Alignment", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Draws technical sports overlay grids", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = themeConfig.homeGrid,
                            onCheckedChange = { viewModel.updateThemeConfig(homeGrid = it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("switch_home_grid")
                        )
                    }
                }
            }
        }
    }
}
