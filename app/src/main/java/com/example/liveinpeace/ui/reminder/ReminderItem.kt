package com.example.liveinpeace.ui.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.liveinpeace.viewModel.ReminderViewModel

@Composable
fun ReminderItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Card putih
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black // Teks hitam
            )
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF2E7D32), // Hijau tua
                    checkedTrackColor = Color(0xFF4CAF50), // Hijau medium untuk track
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

//@Composable
//fun ReminderItem(
//    title: String,
//    isChecked: Boolean,
//    onCheckedChange: (Boolean) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.Green // Card putih
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.titleMedium,
//                color = Color(0xFF2E7D32) // Hijau tua untuk text
//            )
//            Switch(
//                checked = isChecked,
//                onCheckedChange = onCheckedChange,
//                colors = SwitchDefaults.colors(
//                    checkedThumbColor = Color(0xFF2E7D32), // Hijau tua
//                    checkedTrackColor = Color(0xFF4CAF50), // Hijau medium untuk track
//                    uncheckedThumbColor = Color.Gray,
//                    uncheckedTrackColor = Color.LightGray
//                )
//            )
//        }
//    }
//}