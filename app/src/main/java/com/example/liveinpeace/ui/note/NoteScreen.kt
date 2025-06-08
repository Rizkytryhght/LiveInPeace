package com.example.liveinpeace.ui.note

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.example.liveinpeace.viewModel.NoteViewModel
import com.example.liveinpeace.ui.theme.GreenPrimary
import kotlinx.coroutines.delay

@Composable
fun NotesScreen(
    noteViewModel: NoteViewModel
) {
    val context = LocalContext.current
    val notes by noteViewModel.realtimeNotes.observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Semua") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var fabScale by remember { mutableFloatStateOf(0f) }

    // Trigger animations on first composition
    LaunchedEffect(Unit) {
        isVisible = true
        delay(300)
        fabScale = 1f
    }

    // Filter notes berdasarkan search query dan tag
    val filteredNotes = remember(notes, searchQuery, selectedTag) {
        var filtered = notes

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { note ->
                note.title.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true)
            }
        }

        if (selectedTag != "Semua") {
            filtered = filtered.filter { it.tag == selectedTag }
        }

        filtered.sortedByDescending { it.date + it.time }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GreenPrimary,
                        GreenPrimary.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    BottomNavigationBar(context = context)
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = fabScale > 0f,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn()
                ) {
                    FloatingActionButton(
                        onClick = {
                            val intent = Intent(context, NoteDetailActivity::class.java)
                            intent.putExtra("is_new", true)
                            context.startActivity(intent)
                        },
                        containerColor = Color.White,
                        contentColor = GreenPrimary,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .scale(fabScale)
                            .shadow(8.dp, CircleShape),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah catatan baru",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Animated Header
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    Text(
                        text = "✨ Luapkan emosimu ke sini, sobat",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    )
                }

                // Animated Search Card
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 100
                        )
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .shadow(4.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Cari catatan...",
                                    color = Color.Gray.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { keyboardController?.hide() }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent,
                                errorBorderColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                }

                // Animated Filter Chips
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 200
                        )
                    )
                ) {
                    val tagList = listOf("Semua", "Motivasi", "Belajar", "Kerja")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        itemsIndexed(tagList) { index, tag ->
                            AnimatedFilterChip(
                                tag = tag,
                                selected = selectedTag == tag,
                                onClick = { selectedTag = tag },
                                delay = index * 50
                            )
                        }
                    }
                }

                // Animated Notes List
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 300
                        )
                    ) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    if (filteredNotes.isEmpty()) {
                        EmptyNotesAnimation()
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            itemsIndexed(filteredNotes) { index, note ->
                                AnimatedNoteCard(
                                    note = note,
                                    delay = index * 100,
                                    onClick = {
                                        val intent = Intent(context, NoteDetailActivity::class.java)
                                        intent.putExtra("note_id", note.id)
                                        intent.putExtra("title", note.title)
                                        intent.putExtra("content", note.content)
                                        intent.putExtra("tag", note.tag)
                                        intent.putExtra("date", note.date)
                                        intent.putExtra("time", note.time)
                                        intent.putExtra("is_new", false)
                                        context.startActivity(intent)
                                    },
                                    onDelete = {
                                        noteViewModel.deleteNote(note)
                                        Toast.makeText(context, "Catatan dihapus", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedFilterChip(
    tag: String,
    selected: Boolean,
    onClick: () -> Unit,
    delay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

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
        ) + fadeIn()
    ) {
        FilterChip(
            selected = selected,
            onClick = onClick,
            label = {
                Text(
                    tag,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                )
            },
            modifier = Modifier
//                .shadow(if (selected) 4.dp else 2.dp, RoundedCornerShape(20.dp))
                .animateContentSize(),
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFADEAA5),
                containerColor = Color.White.copy(alpha = 0.95f),
                labelColor = GreenPrimary,
                selectedLabelColor = GreenPrimary
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = if (selected) Color.Transparent else Color.Gray.copy(alpha = 0.2f),
                selectedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun AnimatedNoteCard(
    note: Note,
    delay: Int = 0,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn()
    ) {
        Card(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .graphicsLayer {
                    translationY = if (isPressed) 2.dp.toPx() else 0f
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White,
                                Color.White.copy(alpha = 0.95f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = note.title.ifEmpty { "Tanpa Judul" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = note.content.ifEmpty { "Tanpa konten" },
                                fontSize = 14.sp,
                                color = Color.Gray.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = GreenPrimary.copy(alpha = 0.15f),
                                ) {
                                    Text(
                                        text = note.tag.ifEmpty { "Umum" },
                                        fontSize = 12.sp,
                                        color = GreenPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                Text(
                                    text = "${note.date} • ${note.time}",
                                    fontSize = 12.sp,
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.1f))
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus catatan",
                                tint = Color.Red.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Subtle gradient overlay for depth
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    GreenPrimary.copy(alpha = 0.3f),
                                    GreenPrimary.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Hapus Catatan",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus catatan ini?",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Batal", color = GreenPrimary, fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun EmptyNotesAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📝",
            fontSize = 48.sp,
            modifier = Modifier.scale(scale)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Belum ada catatan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = "Mulai tulis ceritamu sekarang!",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Suppress("DEPRECATION")
@Composable
private fun BottomNavigationBar(context: Context) {
    Surface(
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        shadowElevation = 16.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
    ) {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.White.copy(alpha = 0.98f)
                        )
                    )
                )
        ) {
            val items = listOf(
                NavItem("Catatan", R.drawable.ic_note, "notes"),
                NavItem("Ruang Tenang", R.drawable.ic_star, "features"),
                NavItem("Profile", R.drawable.ic_profile, "profile")
            )

            items.forEach { item ->
                val selected = item.route == "notes"

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        when(item.route) {
                            "features" -> {
                                val intent = Intent(context, FeaturesListActivity::class.java)
                                context.startActivity(intent)
                                if (context is NoteActivity) {
                                    context.overridePendingTransition(0, 0)
                                    context.finish()
                                }
                            }
                            "profile" -> {
                                val intent = Intent(context, ProfileActivity::class.java)
                                context.startActivity(intent)
                                if (context is NoteActivity) {
                                    context.overridePendingTransition(0, 0)
                                    context.finish()
                                }
                            }
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(if (selected) 28.dp else 24.dp)
                                .background(
                                    if (selected) GreenPrimary.copy(alpha = 0.1f) else Color.Transparent,
                                    CircleShape
                                )
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                modifier = Modifier.size(if (selected) 22.dp else 20.dp),
                                tint = if (selected) GreenPrimary else Color.Gray.copy(alpha = 0.6f)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) GreenPrimary else Color.Gray.copy(alpha = 0.6f)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenPrimary,
                        selectedTextColor = GreenPrimary,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}

data class NavItem(val title: String, val icon: Int, val route: String)