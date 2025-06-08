@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liveinpeace.ui.note

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.example.liveinpeace.ui.theme.GreenPrimary
import com.example.liveinpeace.viewModel.NoteViewModel
import com.example.liveinpeace.viewModel.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteDetailScreen(
    isNewNote: Boolean = true,
    noteId: String = "",
    initialTitle: String = "",
    initialContent: String = "",
    initialTag: String = "Semua",
    initialDate: String = "",
    initialTime: String = "",
    onNavigateBack: () -> Unit = {},
    onNoteSaved: (Note) -> Unit = {}
) {
    val context = LocalContext.current
    val repository = NoteRepository()
    val factory = NoteViewModelFactory(repository)
    val noteViewModel: NoteViewModel = viewModel(factory = factory)

    // State variables
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }
    var selectedTag by remember { mutableStateOf(initialTag) }
    var showDialog by remember { mutableStateOf(false) }

    // Setup dates
    val (currentDate, currentTime) = remember {
        if (initialDate.isNotEmpty() && initialTime.isNotEmpty()) {
            Pair(initialDate, initialTime)
        } else {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val now = calendar.time
            Pair(dateFormat.format(now), timeFormat.format(now))
        }
    }

    val displayDate = remember(currentDate, currentTime) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(currentDate)
            val displayDate = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(date ?: Date())
            val dayName = SimpleDateFormat("EEEE", Locale("id", "ID")).format(date ?: Date())
            "$displayDate ($dayName), $currentTime"
        } catch (e: Exception) {
            currentDate
        }
    }

    val finalNoteId = remember { noteId.ifEmpty { UUID.randomUUID().toString() } }

    // Tag list
    val tagList = listOf("Semua", "Motivasi", "Belajar", "Kerja")

    // Handle back button
    BackHandler {
        if (title.isNotEmpty() || content.isNotEmpty()) {
            showDialog = true
        } else {
            onNavigateBack()
        }
    }

    // Save function
    fun saveNote() {
        if (title.trim().isEmpty()) {
            Toast.makeText(context, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val note = Note(
            id = finalNoteId,
            title = title.trim(),
            content = content.trim(),
            date = currentDate,
            time = currentTime,
            tag = selectedTag
        )

        if (isNewNote) {
            noteViewModel.insertNote(note)
            Toast.makeText(context, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        } else {
            noteViewModel.updateNote(note)
            Toast.makeText(context, "Catatan berhasil diperbarui", Toast.LENGTH_SHORT).show()
        }

        onNoteSaved(note)
        onNavigateBack()
    }

    // Delete function
    fun deleteNote() {
        if (!isNewNote) {
            val note = Note(
                id = finalNoteId,
                title = title.trim(),
                content = content.trim(),
                date = currentDate,
                time = currentTime,
                tag = selectedTag
            )
            noteViewModel.deleteNote(note)
            Toast.makeText(context, "Catatan dihapus", Toast.LENGTH_SHORT).show()
        }
        onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top App Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (title.isNotEmpty() || content.isNotEmpty()) {
                            showDialog = true
                        } else {
                            onNavigateBack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                Button(
                    onClick = { saveNote() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Text(
                        text = "Simpan",
                        color = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFFFFFF)
            )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Date Display
            Text(
                text = displayDate,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        text = "Beri Judul",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tagList) { tag ->
                    FilterChip(
                        onClick = { selectedTag = tag },
                        label = { Text(tag) },
                        selected = selectedTag == tag,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Divider
            HorizontalDivider(
                color = Color.Black,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Isi Catatan") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 200.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                )
            )

            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom navigation
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah anda ingin menyimpan perubahan?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        saveNote()
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            showDialog = false
                            deleteNote()
                        }
                    ) {
                        Text("Hapus")
                    }
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Batal")
                    }
                }
            }
        )
    }
}