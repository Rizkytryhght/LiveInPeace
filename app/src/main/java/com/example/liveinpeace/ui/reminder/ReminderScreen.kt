package com.example.liveinpeace.ui.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liveinpeace.viewModel.ReminderViewModel

@Composable
fun ReminderScreen() {
    val viewModel: ReminderViewModel = viewModel()

    // Collect prayer times
    val fajrTime by viewModel.fajrTime.collectAsState()
    val dhuhrTime by viewModel.dhuhrTime.collectAsState()
    val asrTime by viewModel.asrTime.collectAsState()
    val maghribTime by viewModel.maghribTime.collectAsState()
    val ishaTime by viewModel.ishaTime.collectAsState()

    // For legacy support
    val dzikirPagiTime by viewModel.dzikirPagiTime.collectAsState()
    val dzikirPetangTime by viewModel.dzikirPetangTime.collectAsState()

    // Track reminder states
    var fajrEnabled by remember { mutableStateOf(true) }
    var dhuhrEnabled by remember { mutableStateOf(true) }
    var asrEnabled by remember { mutableStateOf(true) }
    var maghribEnabled by remember { mutableStateOf(true) }
    var ishaEnabled by remember { mutableStateOf(true) }
    var pagiEnabled by remember { mutableStateOf(true) }
    var petangEnabled by remember { mutableStateOf(true) }

    // Fetch prayer times when screen launches
    LaunchedEffect(Unit) {
        viewModel.fetchTimings()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Reminder Ibadah",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Prayer reminders
        item {
            Text(
                text = "Waktu Sholat",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            ReminderItem(
                title = "Subuh ($fajrTime)",
                isChecked = fajrEnabled,
                onCheckedChange = {
                    fajrEnabled = it
                    viewModel.setFajrEnabled(it)
                }
            )
        }

        item {
            ReminderItem(
                title = "Dzuhur ($dhuhrTime)",
                isChecked = dhuhrEnabled,
                onCheckedChange = {
                    dhuhrEnabled = it
                    viewModel.setDhuhrEnabled(it)
                }
            )
        }

        item {
            ReminderItem(
                title = "Ashar ($asrTime)",
                isChecked = asrEnabled,
                onCheckedChange = {
                    asrEnabled = it
                    viewModel.setAsrEnabled(it)
                }
            )
        }

        item {
            ReminderItem(
                title = "Maghrib ($maghribTime)",
                isChecked = maghribEnabled,
                onCheckedChange = {
                    maghribEnabled = it
                    viewModel.setMaghribEnabled(it)
                }
            )
        }

        item {
            ReminderItem(
                title = "Isya ($ishaTime)",
                isChecked = ishaEnabled,
                onCheckedChange = {
                    ishaEnabled = it
                    viewModel.setIshaEnabled(it)
                }
            )
        }

        // Dzikir reminders section
        item {
            Text(
                text = "Waktu Dzikir",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            ReminderItem(
                title = "Dzikir Pagi ($dzikirPagiTime)",
                isChecked = pagiEnabled,
                onCheckedChange = {
                    pagiEnabled = it
                    viewModel.setDzikirPagiEnabled(it)
                }
            )
        }

        item {
            ReminderItem(
                title = "Dzikir Petang ($dzikirPetangTime)",
                isChecked = petangEnabled,
                onCheckedChange = {
                    petangEnabled = it
                    viewModel.setDzikirPetangEnabled(it)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.fetchTimings() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Refresh Jadwal")
            }
        }
    }
}


//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.tooling.preview.Preview
//
//@Composable
//fun ReminderScreen(
//    isPagiChecked: Boolean,
//    isPetangChecked: Boolean,
//    onPagiToggle: (Boolean) -> Unit,
//    onPetangToggle: (Boolean) -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // Title
//        Text(
//            text = "Reminder Ibadah",
//            style = MaterialTheme.typography.headlineLarge,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(bottom = 24.dp)
//        )
//
//        // Dzikir Pagi
//        ReminderItem(
//            title = "Dzikir Pagi (07:00)",
//            isChecked = isPagiChecked,
//            onCheckedChange = onPagiToggle
//        )
//
//        // Dzikir Petang
//        Spacer(modifier = Modifier.height(16.dp))
//        ReminderItem(
//            title = "Dzikir Petang (16:30)",
//            isChecked = isPetangChecked,
//            onCheckedChange = onPetangToggle
//        )
//    }
//}
//
//@Composable
//fun ReminderItem(
//    title: String,
//    isChecked: Boolean,
//    onCheckedChange: (Boolean) -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = title,
//            modifier = Modifier.weight(1f),
//            style = MaterialTheme.typography.bodyLarge,
//            color = MaterialTheme.colorScheme.onBackground
//        )
//        Switch(
//            checked = isChecked,
//            onCheckedChange = onCheckedChange,
//            colors = SwitchDefaults.colors(
//                uncheckedThumbColor = Color.Gray,
//                checkedThumbColor = MaterialTheme.colorScheme.primary
//            )
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ReminderScreenPreview() {
//    ReminderScreen(
//        isPagiChecked = true,
//        isPetangChecked = false,
//        onPagiToggle = {},
//        onPetangToggle = {}
//    )
//}
