package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.viewmodel.SportsViewModel

@Composable
fun ProfileScreen(
    viewModel: SportsViewModel,
    onNavigateToAppearance: () -> Unit,
    onNavigateToAdminLogin: () -> Unit,
    onNavigateToAdminDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeConfig by viewModel.themeConfig.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    val primaryColor = themeConfig.primaryColorHex.toColor()

    var name by remember(themeConfig.name) { mutableStateOf(themeConfig.name) }
    var location by remember(themeConfig.location) { mutableStateOf(themeConfig.location) }
    var language by remember(themeConfig.language) { mutableStateOf(themeConfig.language) }

    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp) // Leave space for bottom nav
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MY ATHLETE PROFILE",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            // Edit Profile Toggle button
            IconButton(
                onClick = {
                    if (isEditing) {
                        viewModel.updateProfile(name, location, language)
                    }
                    isEditing = !isEditing
                },
                modifier = Modifier.testTag("btn_edit_profile")
            ) {
                Icon(
                    if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = primaryColor
                )
            }
        }
        Text(
            text = "Configure personal streaming identities, language & layouts",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Avatar Display
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(96.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.2f))
                .border(2.dp, primaryColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Avatar",
                tint = primaryColor,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Details Gated by Edit State
        if (isEditing) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("input_profile_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Streaming Location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("input_profile_location"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedTextField(
                value = language,
                onValueChange = { language = it },
                label = { Text("Preferred Language") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("input_profile_language"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        } else {
            // Read-Only details
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(name, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = primaryColor, modifier = Modifier.size(16.dp))
                    Text(location, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Language, contentDescription = "Language", tint = primaryColor, modifier = Modifier.size(16.dp))
                    Text(language, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Options List
        Text(
            text = "APPLICATION SETTINGS",
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Appearance entry
        ProfileOptionItem(
            title = "Appearance & Customization",
            subtitle = "Modify player overlays, glass effects, accent colors",
            icon = Icons.Default.Palette,
            primaryColor = primaryColor,
            themeConfig = themeConfig,
            onClick = onNavigateToAppearance,
            testTag = "option_appearance"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Info item
        ProfileOptionItem(
            title = "Privacy & Authorization",
            subtitle = "Anonymous Firebase device bound secure identification",
            icon = Icons.Default.Security,
            primaryColor = primaryColor,
            themeConfig = themeConfig,
            onClick = {},
            testTag = "option_security"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Secret Admin Portal Entry
        Text(
            text = "DEVELOPMENT GATEWAY",
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ProfileOptionItem(
            title = if (isAdminLoggedIn) "Admin Dashboard" else "Admin Console",
            subtitle = if (isAdminLoggedIn) "Manage sports broadcasts & views" else "Post highlights & broadcast schedules securely",
            icon = Icons.Default.AdminPanelSettings,
            primaryColor = primaryColor,
            themeConfig = themeConfig,
            onClick = {
                if (isAdminLoggedIn) {
                    onNavigateToAdminDashboard()
                } else {
                    onNavigateToAdminLogin()
                }
            },
            testTag = "option_admin"
        )
    }
}

@Composable
fun ProfileOptionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryColor: Color,
    themeConfig: com.example.data.UserThemeEntity,
    onClick: () -> Unit,
    testTag: String
) {
    GlassmorphicCard(
        cornerRadius = 12.dp,
        cardStyle = themeConfig.cardStyle,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(primaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = primaryColor)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.Gray)
        }
    }
}
