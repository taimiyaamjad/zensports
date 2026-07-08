package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SportsViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SportsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true, dynamicColor = false) {
                val themeConfig by viewModel.themeConfig.collectAsState()
                val primaryColor = themeConfig.primaryColorHex.toColor()

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Determine if we should show the bottom navigation bar
                val mainDestinations = listOf("home", "discover", "library", "profile")
                val showBottomBar = currentRoute in mainDestinations

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF0C0E14),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = Color(0x99121522), // Semi-transparent cosmic dark
                                modifier = Modifier
                                    .navigationBarsPadding() // Respect device gesture pill bottom insets!
                                    .testTag("main_bottom_navigation"),
                                tonalElevation = 8.dp
                            ) {
                                // Home Tab
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                                    icon = { Icon(if (currentRoute == "home") Icons.Default.Home else Icons.Outlined.Home, contentDescription = "Home") },
                                    label = { Text("Home", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = primaryColor,
                                        indicatorColor = primaryColor,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_home")
                                )

                                // Discover Tab
                                NavigationBarItem(
                                    selected = currentRoute == "discover",
                                    onClick = { navController.navigate("discover") { popUpTo("home") } },
                                    icon = { Icon(if (currentRoute == "discover") Icons.Default.Explore else Icons.Outlined.Explore, contentDescription = "Discover") },
                                    label = { Text("Discover", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = primaryColor,
                                        indicatorColor = primaryColor,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_discover")
                                )

                                // Library Tab
                                NavigationBarItem(
                                    selected = currentRoute == "library",
                                    onClick = { navController.navigate("library") { popUpTo("home") } },
                                    icon = { Icon(if (currentRoute == "library") Icons.Default.VideoLibrary else Icons.Outlined.VideoLibrary, contentDescription = "Library") },
                                    label = { Text("Library", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = primaryColor,
                                        indicatorColor = primaryColor,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_library")
                                )

                                // Profile Tab
                                NavigationBarItem(
                                    selected = currentRoute == "profile",
                                    onClick = { navController.navigate("profile") { popUpTo("home") } },
                                    icon = { Icon(if (currentRoute == "profile") Icons.Default.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                                    label = { Text("Profile", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = primaryColor,
                                        indicatorColor = primaryColor,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_tab_profile")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0C0E14))
                    ) {
                        // If enabled, draw a beautiful background technical alignment grid
                        if (themeConfig.homeGrid) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val gridCols = 10
                                val gridRows = 20
                                val xStep = size.width / gridCols
                                val yStep = size.height / gridRows
                                for (i in 0..gridCols) {
                                    drawLine(
                                        color = Color(0x03FFFFFF),
                                        start = androidx.compose.ui.geometry.Offset(i * xStep, 0f),
                                        end = androidx.compose.ui.geometry.Offset(i * xStep, size.height),
                                        strokeWidth = 1f
                                    )
                                }
                                for (i in 0..gridRows) {
                                    drawLine(
                                        color = Color(0x03FFFFFF),
                                        start = androidx.compose.ui.geometry.Offset(0f, i * yStep),
                                        end = androidx.compose.ui.geometry.Offset(size.width, i * yStep),
                                        strokeWidth = 1f
                                    )
                                }
                            }
                        }

                        // Navigation Graph
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(
                                top = innerPadding.calculateTopPadding(),
                                bottom = if (showBottomBar) 0.dp else innerPadding.calculateBottomPadding()
                            )
                        ) {
                            composable("home") {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetail = { postId ->
                                        navController.navigate("post_detail/$postId")
                                    }
                                )
                            }

                            composable("discover") {
                                DiscoverScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetail = { postId ->
                                        navController.navigate("post_detail/$postId")
                                    }
                                )
                            }

                            composable("library") {
                                LibraryScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetail = { postId ->
                                        navController.navigate("post_detail/$postId")
                                    }
                                )
                            }

                            composable("profile") {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    onNavigateToAppearance = {
                                        navController.navigate("appearance")
                                    },
                                    onNavigateToAdminLogin = {
                                        navController.navigate("admin_login")
                                    },
                                    onNavigateToAdminDashboard = {
                                        navController.navigate("admin_dashboard")
                                    }
                                )
                            }

                            composable(
                                route = "post_detail/{postId}",
                                arguments = listOf(navArgument("postId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                                PostDetailScreen(
                                    postId = postId,
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable("appearance") {
                                AppearanceScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable("admin_login") {
                                AdminLoginScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onLoginSuccess = {
                                        navController.navigate("admin_dashboard") {
                                            popUpTo("profile")
                                        }
                                    }
                                )
                            }

                            composable("admin_dashboard") {
                                AdminDashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToNewPost = {
                                        navController.navigate("new_post")
                                    },
                                    onNavigateBack = {
                                        navController.navigate("profile") {
                                            popUpTo("home")
                                        }
                                    }
                                )
                            }

                            composable("new_post") {
                                NewPostScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }

                        // Determine if we need first-time profile onboarding setup
                        val needsOnboarding = themeConfig.uid.isNotEmpty() && (themeConfig.name.isBlank() || themeConfig.location.isBlank() || themeConfig.language.isBlank())
                        if (needsOnboarding) {
                            OnboardingOverlay(
                                primaryColorHex = themeConfig.primaryColorHex,
                                onSubmit = { name, location, lang ->
                                    viewModel.updateProfile(name, location, lang)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingOverlay(
    primaryColorHex: String,
    onSubmit: (name: String, location: String, language: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("English") }

    val primaryColor = primaryColorHex.toColor()
    val languages = listOf("English", "Spanish", "French", "German", "Hindi", "Japanese", "Arabic")
    var expandedDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF20C0E14)) // Opaque dark overlay
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2134)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(primaryColor.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Welcome Icon",
                        tint = primaryColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ATHLETE PROFILE SETUP",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome to ZenSports! Enter your details to initialize your personalized live streaming dashboard.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name / Alias", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input"),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Streaming Location (e.g. London, UK)", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = primaryColor,
                        cursorColor = primaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_location_input"),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = language,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Preferred Language", color = Color.Gray) },
                        trailingIcon = {
                            IconButton(onClick = { expandedDropdown = !expandedDropdown }) {
                                Icon(
                                    imageVector = if (expandedDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Language",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = primaryColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_language_dropdown"),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Color(0xFF1E2134))
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang, color = Color.White) },
                                onClick = {
                                    language = lang
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && location.isNotBlank()) {
                            onSubmit(name.trim(), location.trim(), language)
                        }
                    },
                    enabled = name.isNotBlank() && location.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("onboarding_submit_button"),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "GET STARTED",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}
