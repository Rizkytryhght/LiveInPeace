package com.example.liveinpeace.ui.note

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.liveinpeace.R
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.example.liveinpeace.ui.profile.ProfileActivity
import com.example.liveinpeace.viewModel.NoteViewModel
import com.example.liveinpeace.ui.theme.GreenPrimary

@Composable
fun NotesScreen(
    noteViewModel: NoteViewModel
) {
    val context = LocalContext.current
    val notes by noteViewModel.realtimeNotes.observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Semua") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Filter notes berdasarkan search query dan tag
    val filteredNotes = remember(notes, searchQuery, selectedTag) {
        var filtered = notes

        // Filter berdasarkan search query
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { note ->
                note.title.contains(searchQuery, ignoreCase = true) ||
                        note.content.contains(searchQuery, ignoreCase = true)
            }
        }

        // Filter berdasarkan tag
        if (selectedTag != "Semua") {
            filtered = filtered.filter { it.tag == selectedTag }
        }

        // Sort berdasarkan tanggal dan waktu terbaru
        filtered.sortedByDescending { it.date + it.time }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(context = context)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, NoteDetailActivity::class.java)
                    intent.putExtra("is_new", true)
                    context.startActivity(intent)
                },
                containerColor = Color.White,
                contentColor = GreenPrimary,
                modifier = Modifier.padding(bottom = 16.dp),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah catatan baru",
                )
            }
        },
        containerColor = GreenPrimary
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Header Text
            Text(
                text = "Luapkan emosimu ke sini, sobat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Search Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari catatan...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
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
                        errorBorderColor = Color.Transparent
                    )
                )
            }

            // Filter Chips
            val tagList = listOf("Semua", "Motivasi", "Belajar", "Kerja")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(tagList) { tag ->
                    FilterChip(
                        selected = selectedTag == tag,
                        onClick = { selectedTag = tag },
                        label = { Text(tag) },
                        enabled = true,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFADEAA5),
                            containerColor = Color.White,
                            labelColor = GreenPrimary,
                            selectedLabelColor = GreenPrimary
                        )
                    )
                }
            }

            // Notes List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredNotes) { note ->
                    NoteCard(
                        note = note,
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

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.title.ifEmpty { "Tanpa Judul" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = note.content.ifEmpty { "Tanpa konten" },
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tag chip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenPrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = note.tag.ifEmpty { "Umum" },
                                fontSize = 12.sp,
                                color = GreenPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "${note.date} • ${note.time}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Delete button
                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_delete),
                        contentDescription = "Hapus catatan",
                        tint = Color.Red,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Catatan") },
            text = { Text("Apakah Anda yakin ingin menghapus catatan ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Suppress("DEPRECATION")
@Composable
private fun BottomNavigationBar(context: Context) {
    Surface(
//        tonalElevation = 8.dp,
//        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
    ) {
        NavigationBar(
            containerColor = Color(0xFFFFFFFF),
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
                    selected = item.route == "notes", // Always selected for notes
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
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(20.dp),
                            tint = if (item.route == "notes") GreenPrimary else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (item.route == "notes") GreenPrimary else Color.Gray
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