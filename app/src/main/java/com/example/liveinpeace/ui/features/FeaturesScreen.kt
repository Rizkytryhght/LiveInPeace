package com.example.liveinpeace.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.theme.GreenPrimary

@Composable
fun FeaturesScreen(
    onNavigate: (String) -> Unit,
    currentRoute: String,
    onFeatureClick: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = GreenPrimary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Ruang Tenang",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Divider
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 2.dp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Row pertama - Checklist Ibadah & Reminder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    title = "Checklist Ibadah",
                    iconRes = R.drawable.mosque,
                    onClick = { onFeatureClick("checklist") },
                    modifier = Modifier.weight(1f)
                )

                FeatureCard(
                    title = "Reminder Ibadah",
                    iconRes = R.drawable.clock,
                    onClick = { onFeatureClick("reminder") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row kedua - Test DASS & Mood Tracker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureCard(
                    title = "Test DASS",
                    iconRes = R.drawable.kuisioner,
                    onClick = { onFeatureClick("dass") },
                    modifier = Modifier.weight(1f)
                )

                FeatureCard(
                    title = "Mood Tracker",
                    iconRes = R.drawable.mood,
                    onClick = { onFeatureClick("mood") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // sesuai XML
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Surface(
//        tonalElevation = 8.dp,
//        shadowElevation = 8.dp,
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