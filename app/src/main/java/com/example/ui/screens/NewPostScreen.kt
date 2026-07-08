package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.viewmodel.SportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    viewModel: SportsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeConfig by viewModel.themeConfig.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val primaryColor = themeConfig.primaryColorHex.toColor()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var liveLink by remember { mutableStateOf("https://www.youtube.com/embed/dQw4w9WgXcQ") }

    var selectedCatName by remember { mutableStateOf("Cricket") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var isLiveNow by remember { mutableStateOf(true) }
    var isDownloadable by remember { mutableStateOf(true) }
    var hasSchedule by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0C0E14),
        topBar = {
            TopAppBar(
                title = { Text("NEW BROADCAST", fontWeight = FontWeight.Bold, color = primaryColor, letterSpacing = 1.5.sp, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back_new_post")) {
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
                "CREATE BROADCAST CHANNEL",
                color = Color.Gray,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            GlassmorphicCard(
                cornerRadius = 16.dp,
                cardStyle = themeConfig.cardStyle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; errorMessage = null },
                        label = { Text("Match Title / Heading") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_post_title"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    // Category Selection Box / Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedCatName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sports Category") },
                            trailingIcon = {
                                IconButton(onClick = { dropdownExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = primaryColor)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_post_category_trigger"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                focusedLabelColor = primaryColor,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF141722))
                                .testTag("dropdown_post_category")
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name, color = Color.White) },
                                    onClick = {
                                        selectedCatName = cat.name
                                        dropdownExpanded = false
                                    },
                                    modifier = Modifier.testTag("dropdown_item_${cat.name.lowercase()}")
                                )
                            }
                        }
                    }

                    // Live stream URL
                    OutlinedTextField(
                        value = liveLink,
                        onValueChange = { liveLink = it; errorMessage = null },
                        label = { Text("Stream Live URL / Embed URL") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_post_livelink"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it; errorMessage = null },
                        label = { Text("Description & Event Info") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("input_post_description"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            focusedLabelColor = primaryColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 4
                    )

                    // Toggle: Live State
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Broadcast Live Instantly", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("If disabled, goes to concluded/schedules", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isLiveNow,
                            onCheckedChange = {
                                isLiveNow = it
                                if (it) hasSchedule = false
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("switch_post_islive")
                        )
                    }

                    // Toggle: Downloadable Recording
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Downloadable Highlights Recording", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Allows users to save stream to offline library", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = isDownloadable,
                            onCheckedChange = { isDownloadable = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("switch_post_downloadable")
                        )
                    }

                    // Toggle: Scheduled
                    if (!isLiveNow) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Schedule Start Time", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Set upcoming match event schedule (In 3 hours)", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(
                                checked = hasSchedule,
                                onCheckedChange = { hasSchedule = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("switch_post_scheduled")
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit button
                    Button(
                        onClick = {
                            if (title.isBlank() || description.isBlank() || liveLink.isBlank()) {
                                errorMessage = "Please fill in all match parameters completely."
                                return@Button
                            }

                            val scheduleTimestamp = if (hasSchedule) System.currentTimeMillis() + 10800000 else null
                            val downloadUrl = if (isDownloadable) "https://example.com/recordings/new-match.mp4" else null

                            viewModel.createPost(
                                title = title,
                                description = description,
                                liveLink = liveLink,
                                category = selectedCatName,
                                isLive = isLiveNow,
                                scheduledAt = scheduleTimestamp,
                                downloadable = isDownloadable,
                                downloadUrl = downloadUrl
                            )

                            onNavigateBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_submit_new_post"),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Publish Broadcast Channel", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
