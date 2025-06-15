package com.example.liveinpeace.ui.features

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.theme.GreenPrimary
import kotlinx.coroutines.delay

@Composable
fun FeaturesScreen(
    onNavigate: (String) -> Unit,
    currentRoute: String,
    onFeatureClick: (String) -> Unit
) {
    // Animation states
    var headerVisible by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }

    // Trigger animations on composition
    LaunchedEffect(Unit) {
        delay(100)
        headerVisible = true
        delay(300)
        cardsVisible = true
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GreenPrimary,
                            GreenPrimary.copy(alpha = 0.8f),
                            GreenPrimary.copy(alpha = 0.9f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Animated Header
                AnimatedVisibility(
                    visible = headerVisible,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(600))
                ) {
                    Column {
                        Text(
                            text = "Ruang Tenang",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "Temukan kedamaian dalam setiap langkah",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )

                        // Animated Divider
                        val infiniteTransition = rememberInfiniteTransition(label = "divider")
                        val shimmer by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "shimmer"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .padding(vertical = 12.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.3f),
                                            Color.White.copy(alpha = 0.8f + shimmer * 0.2f),
                                            Color.White.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Animated Feature Cards
                AnimatedVisibility(
                    visible = cardsVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(800))
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Checklist Ibadah - Full Width
                        EnhancedFeatureCard(
                            title = "Checklist Ibadah",
                            subtitle = "Pantau ibadah harian",
                            iconRes = R.drawable.mosque,
                            onClick = { onFeatureClick("checklist") },
                            modifier = Modifier.fillMaxWidth(),
                            delay = 0
                        )

                        // Reminder Ibadah - Full Width
                        EnhancedFeatureCard(
                            title = "Reminder Ibadah",
                            subtitle = "Pengingat waktu sholat",
                            iconRes = R.drawable.clock,
                            onClick = { onFeatureClick("reminder") },
                            modifier = Modifier.fillMaxWidth(),
                            delay = 100
                        )

                        // Test DASS - Full Width
                        EnhancedFeatureCard(
                            title = "Test DASS",
                            subtitle = "Evaluasi kesehatan mental",
                            iconRes = R.drawable.kuisioner,
                            onClick = { onFeatureClick("dass") },
                            modifier = Modifier.fillMaxWidth(),
                            delay = 200
                        )

                        // Mood Tracker - Full Width
                        EnhancedFeatureCard(
                            title = "Mood Tracker",
                            subtitle = "Lacak suasana hati",
                            iconRes = R.drawable.mood,
                            onClick = { onFeatureClick("mood") },
                            modifier = Modifier.fillMaxWidth(),
                            delay = 300
                        )
                    }
                }

                // Bottom padding to ensure content doesn't get cut off
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun EnhancedFeatureCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    delay: Int = 0
) {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    // Scale animation for press effect
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Rotation animation for icon
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Trigger entrance animation
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(400))
    ) {
        Card(
            modifier = modifier
                .height(180.dp)
                .scale(scale)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color.Black.copy(alpha = 0.1f),
                    spotColor = Color.Black.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            onClick = {
                isPressed = true
                onClick()
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Subtle gradient background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GreenPrimary.copy(alpha = 0.05f),
                                    Color.Transparent
                                ),
                                radius = 300f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Animated Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .rotate(rotation),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icon shadow/glow effect
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = title,
                            modifier = Modifier
                                .size(75.dp)
                                .rotate(-rotation) // Counter-rotate to keep icon upright
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    // Subtitle
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Shine effect on hover/press
                if (isPressed) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    ),
                                    radius = 200f
                                )
                            )
                    )
                }
            }
        }
    }

    // Reset press state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
private fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
    ) {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            val items = listOf(
                NavItem("Catatan", R.drawable.ic_note, "notes"),
                NavItem("Ruang Tenang", R.drawable.ic_star, "features"),
                NavItem("Profile", R.drawable.ic_profile, "profile")
            )

            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(20.dp),
                            tint = if (currentRoute == item.route) GreenPrimary else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (currentRoute == item.route) GreenPrimary else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenPrimary,
                        selectedTextColor = GreenPrimary,
                        indicatorColor = Color(0xFFADEAA5),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}

data class NavItem(val title: String, val icon: Int, val route: String)