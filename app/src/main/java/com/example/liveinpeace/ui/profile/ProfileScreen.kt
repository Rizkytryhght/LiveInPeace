package com.example.liveinpeace.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.liveinpeace.R
import com.example.liveinpeace.data.ProfileModel
import java.io.File
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import com.example.liveinpeace.ui.theme.GreenPrimary

@Composable
fun ProfileScreen(
    profile: ProfileModel,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigate: (String) -> Unit,
    currentRoute: String
) {
    val context = LocalContext.current
    val imageFile = remember(profile.profileImagePath) {
        if (profile.profileImagePath.isNotBlank()) File(profile.profileImagePath) else null
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = GreenPrimary
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    thickness = 2.dp,
                    color = Color.White
                )
            }

            item {
                ProfileCard(profile, imageFile, onEditClick)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                AccountSettingsCard(Modifier, onLogoutClick)
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: ProfileModel,
    imageFile: File?,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start // agar teks dan info label rata kiri
        ) {
            // Center foto profil
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val painter = rememberAsyncImagePainter(
                    model = imageFile ?: R.drawable.user
                )
                Image(
                    painter = painter,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info rata kiri
            InfoLabel("Nama", "${profile.firstName} ${profile.lastName}")
            InfoLabel("Email", profile.email)
            InfoLabel("Jenis Kelamin", profile.gender ?: "Tidak ditentukan")
            InfoLabel("Nomor Telepon", profile.phoneNumber ?: "Tidak ditentukan")

            IconButton(onClick = onEditClick, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
            }
        }
    }
}

@Composable
fun InfoLabel(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF82A67D))
        Text(text = value, fontSize = 16.sp, color = Color.Black)
    }
}

@Composable
fun AccountSettingsCard(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomEnd) // pojok kanan bawah
                .wrapContentSize(), // agar ukuran kecil
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)), // merah
            elevation = CardDefaults.cardElevation(4.dp),
            onClick = onLogoutClick
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.exit),
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White // ikon putih
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    fontSize = 14.sp,
                    color = Color.White // teks putih
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Surface(
//        tonalElevation = 8.dp,
//        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
    ) {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // Atur tinggi sesuai kebutuhan
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