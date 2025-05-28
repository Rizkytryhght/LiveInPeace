package com.example.liveinpeace.ui.dass

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.liveinpeace.data.repository.DASSRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun DASSOptionsScreen(navController: NavController) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784)),
        startY = 0f,
        endY = 600f
    )
    var isCardAnimated by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isCardAnimated) 1f else 0.9f,
        animationSpec = tween(500)
    )
    var showDialog by remember { mutableStateOf(false) }
    var canTakeQuiz by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val repository = DASSRepository(
        com.example.liveinpeace.data.local.room.AppDatabase.getDatabase(context).dassScoreDao(),
        FirebaseFirestore.getInstance()
    )
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isCardAnimated = true
        coroutineScope.launch {
            val lastTime = repository.getLastScoreTimestamp(userId)
            val now = System.currentTimeMillis()
            val oneWeekMillis = 7L * 24 * 60 * 60 * 1000
            canTakeQuiz = lastTime == null || (now - lastTime) >= oneWeekMillis
            isLoading = false
        }
        repository.clearAllScores()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        IconButton(
            onClick = {
                navController.navigate("main") // Kembali ke MainActivity
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.9f))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DASS Options",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                // Card Isi Kuesioner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .scale(scale)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    onClick = {
                        if (canTakeQuiz) {
                            navController.navigate("dass_introduction")
                        } else {
                            showDialog = true
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Isi Kuesioner",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Isi Kuesioner",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                    }
                }

                // Card History
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    onClick = { navController.navigate("dass_history") }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Lihat History",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Lihat History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                    }
                }

                // Dialog kalau belum 7 hari
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(
                                "Batas Kuesioner",
                                fontSize = 18.sp,
                                color = Color(0xFF2196F3)
                            )
                        },
                        text = {
                            Text(
                                "Kuesioner hanya boleh diisi sekali seminggu. Tunggu hingga minggu depan!",
                                fontSize = 16.sp
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("OK", color = Color(0xFF4CAF50))
                            }
                        },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
//        Button(onClick = {
//            coroutineScope.launch {
//                repository.clearAllScores()
//            }
//        }) {
//            Text("Clear Room Data (Debug)")
//        }
    }
}