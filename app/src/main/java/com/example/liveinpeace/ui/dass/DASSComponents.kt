package com.example.liveinpeace.ui.dass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape


@Composable
fun QuestionItem(
    option: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF2196F3),
                    unselectedColor = Color.Gray
                ),
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = option,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected) Color(0xFF2196F3) else Color.Black
            )
        }
    }
}

@Composable
fun NavigationButtons(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    isFirstQuestion: Boolean,
    isLastQuestion: Boolean,
    isAnswered: Boolean,
    showBackDialog: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                if (isFirstQuestion) showBackDialog(true)
                else onBackClick()
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back", color = Color.White, fontSize = 16.sp)
        }
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(12.dp),
            enabled = isAnswered
        ) {
            Text(
                text = if (isLastQuestion) "Selesai" else "Next",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ResultCard(
    category: String,
    score: Int,
    level: String,
    modifier: Modifier = Modifier
) {
    val levelColor = when (level) {
        "Normal" -> Color(0xFF4CAF50) // Hijau
        "Ringan" -> Color(0xFF90CAF9) // Biru Muda
        "Sedang" -> Color(0xFFFFCA28) // Kuning
        "Berat" -> Color(0xFFFF9800) // Oranye
        else -> Color(0xFFF44336) // Merah
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Text(
                    text = level,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = levelColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Skor: $score",
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
        }
    }
}