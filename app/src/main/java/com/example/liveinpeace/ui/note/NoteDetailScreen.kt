@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.liveinpeace.ui.note

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liveinpeace.data.Note
import com.example.liveinpeace.data.repository.NoteRepository
import com.example.liveinpeace.ui.theme.GreenPrimary
import com.example.liveinpeace.viewModel.NoteViewModel
import com.example.liveinpeace.viewModel.NoteViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(DelicateCoroutinesApi::class)
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
    var isSaving by remember { mutableStateOf(false) }

    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    val animationSpec = tween<Float>(
        durationMillis = 800,
        easing = FastOutSlowInEasing
    )

    // Launch enter animation
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Animated values
    val slideOffset by animateFloatAsState(
        targetValue = if (isVisible) 0f else 100f,
        animationSpec = animationSpec,
        label = "slide"
    )

    val fadeAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = animationSpec,
        label = "fade"
    )

    val scaleValue by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

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

    // Tag list with colors
    val tagList = listOf(
        "Semua" to Color(0xFF6B73FF),
        "Motivasi" to Color(0xFF9C27B0),
        "Belajar" to Color(0xFF2196F3),
        "Kerja" to Color(0xFF4CAF50)
    )

    // Handle back button
    BackHandler {
        if (title.isNotEmpty() || content.isNotEmpty()) {
            showDialog = true
        } else {
            onNavigateBack()
        }
    }

    // Save function with animation
    suspend fun saveNoteWithAnimation() {
        isSaving = true
        delay(500) // Simulate save delay for better UX

        if (title.trim().isEmpty()) {
            Toast.makeText(context, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            isSaving = false
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
        isSaving = false
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationY = slideOffset
                    alpha = fadeAlpha
                    scaleX = scaleValue
                    scaleY = scaleValue
                }
        ) {
            // Enhanced Top App Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isNewNote) "Catatan Baru" else "Edit Catatan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF2C3E50)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (title.isNotEmpty() || content.isNotEmpty()) {
                                    showDialog = true
                                } else {
                                    onNavigateBack()
                                }
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .background(
                                    Color(0xFFF1F3F4),
                                    CircleShape
                                )
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF2C3E50)
                            )
                        }
                    },
                    actions = {
                        // Save button with animation
                        val scope = rememberCoroutineScope()

                        Button(
                            onClick = {
                                kotlinx.coroutines.GlobalScope.launch {
                                    saveNoteWithAnimation()
                                }
                            },
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary,
                                disabledContainerColor = GreenPrimary.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .shadow(4.dp, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            AnimatedContent(
                                targetState = isSaving,
                                transitionSpec = {
                                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                                },
                                label = "save_button"
                            ) { saving ->
                                if (saving) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Text("Menyimpan...", color = Color.White, fontSize = 14.sp)
                                    }
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color.White
                                        )
                                        Text("Simpan", color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Content with staggered animation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Animated Date Display
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(600, delayMillis = 200)
                    ) + fadeIn(tween(600, delayMillis = 200))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8F9FA)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = displayDate,
                            fontSize = 14.sp,
                            color = Color(0xFF6C757D),
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Animated Title Input
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(600, delayMillis = 300)
                    ) + fadeIn(tween(600, delayMillis = 300))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = {
                                Text(
                                    text = "Beri Judul",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF9E9E9E)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C3E50)
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = GreenPrimary
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                }

                // Animated Tags
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(600, delayMillis = 400)
                    ) + fadeIn(tween(600, delayMillis = 400))
                ) {
                    Column {
                        Text(
                            text = "Kategori",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E50),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(bottom = 20.dp)
                        ) {
                            items(tagList) { (tag, color) ->
                                val isSelected = selectedTag == tag
                                val animatedScale by animateFloatAsState(
                                    targetValue = if (isSelected) 1.05f else 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    ),
                                    label = "tag_scale"
                                )

                                FilterChip(
                                    onClick = { selectedTag = tag },
                                    label = {
                                        Text(
                                            text = tag,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    selected = isSelected,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = color,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = color
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = color,
                                        selectedBorderColor = color,
                                        borderWidth = 1.5.dp
                                    ),
                                    modifier = Modifier
                                        .scale(animatedScale)
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = expandHorizontally(
                        animationSpec = tween(400, delayMillis = 500),
                        expandFrom = Alignment.CenterHorizontally
                    ) + fadeIn(tween(400, delayMillis = 500))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        GreenPrimary.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Animated Content Input
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(600, delayMillis = 600)
                    ) + fadeIn(tween(600, delayMillis = 600))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 300.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = {
                                Text(
                                    "Bagaimana kondisi kamu?\nSampaikan perasaanmu di sini",
                                    color = Color(0xFF9E9E9E),
                                    lineHeight = 24.sp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 280.dp)
                                .padding(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = GreenPrimary
                            ),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Default
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                lineHeight = 24.sp,
                                color = Color(0xFF2C3E50)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }

    // Enhanced Confirmation Dialog - FIXED: Using proper border API
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    "Konfirmasi",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
            },
            text = {
                Text(
                    "Apakah Anda ingin menyimpan perubahan pada catatan ini?",
                    color = Color(0xFF6C757D)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        kotlinx.coroutines.GlobalScope.launch {
                            saveNoteWithAnimation()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showDialog = false
                            deleteNote()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE74C3C)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(
                            enabled = true
                        ).copy(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFE74C3C), Color(0xFFE74C3C))
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hapus")
                    }
                    OutlinedButton(
                        onClick = { showDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal")
                    }
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}