package com.example.liveinpeace.ui.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ReminderScreen(
    isPagiChecked: Boolean,
    isPetangChecked: Boolean,
    onPagiToggle: (Boolean) -> Unit,
    onPetangToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Reminder Ibadah",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Dzikir Pagi
        ReminderItem(
            title = "Dzikir Pagi (07:00)",
            isChecked = isPagiChecked,
            onCheckedChange = onPagiToggle
        )

        // Dzikir Petang
        Spacer(modifier = Modifier.height(16.dp))
        ReminderItem(
            title = "Dzikir Petang (16:30)",
            isChecked = isPetangChecked,
            onCheckedChange = onPetangToggle
        )
    }
}

@Composable
fun ReminderItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.Gray,
                checkedThumbColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderScreenPreview() {
    ReminderScreen(
        isPagiChecked = true,
        isPetangChecked = false,
        onPagiToggle = {},
        onPetangToggle = {}
    )
}
