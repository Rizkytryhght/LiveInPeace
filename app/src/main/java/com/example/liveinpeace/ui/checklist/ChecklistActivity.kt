package com.example.liveinpeace.ui.checklist

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ChecklistActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayDate = dateFormat.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {
                ChecklistScreen(
                    firestore = firestore,
                    auth = auth,
                    todayDate = todayDate
                )
            }
        }
    }
}

@Composable
fun ChecklistScreen(
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    todayDate: String
) {
    val context = LocalContext.current

    // State untuk checkbox
    var subuhChecked by remember { mutableStateOf(false) }
    var dzuhurChecked by remember { mutableStateOf(false) }
    var asharChecked by remember { mutableStateOf(false) }
    var maghribChecked by remember { mutableStateOf(false) }
    var isyaChecked by remember { mutableStateOf(false) }
    var dzikirPagiChecked by remember { mutableStateOf(false) }
    var dzikirSoreChecked by remember { mutableStateOf(false) }

    // Memuat data checklist saat Composable diinisialisasi
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        val docRef = firestore.collection("users").document(uid)
            .collection("checklists").document(todayDate)

        docRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                subuhChecked = doc.getBoolean("subuh") ?: false
                dzuhurChecked = doc.getBoolean("dzuhur") ?: false
                asharChecked = doc.getBoolean("ashar") ?: false
                maghribChecked = doc.getBoolean("maghrib") ?: false
                isyaChecked = doc.getBoolean("isya") ?: false
                dzikirPagiChecked = doc.getBoolean("dzikir_pagi") ?: false
                dzikirSoreChecked = doc.getBoolean("dzikir_sore") ?: false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Checklist Ibadah Harian",
            style = MaterialTheme.typography.headlineSmall
        )

        // Checkbox untuk setiap ibadah, beberapa dikomentari
//        ChecklistItem("Sholat Subuh", subuhChecked) { subuhChecked = it }
//        ChecklistItem("Sholat Dzuhur", dzuhurChecked) { dzuhurChecked = it }
//        ChecklistItem("Sholat Ashar", asharChecked) { asharChecked = it }
//        ChecklistItem("Sholat Maghrib", maghribChecked) { maghribChecked = it }
//        ChecklistItem("Sholat Isya", isyaChecked) { isyaChecked = it }
//        ChecklistItem("Dzikir Pagi", dzikirPagiChecked) { dzikirPagiChecked = it }
//        ChecklistItem("Dzikir Sore", dzikirSoreChecked) { dzikirSoreChecked = it }

        // Tombol Simpan, dikomentari
//        Button(
//            onClick = {
//                Log.d("ChecklistActivity", "TOMBOL DISENTUH 🥺")
//                Toast.makeText(context, "Checklist disimpan!", Toast.LENGTH_SHORT).show()
//
//                val uid = auth.currentUser?.uid ?: return@Button
//                val checklistData = hashMapOf(
//                    "date" to todayDate,
//                    "subuh" to subuhChecked,
//                    "dzuhur" to dzuhurChecked,
//                    "ashar" to asharChecked,
//                    "maghrib" to maghribChecked,
//                    "isya" to isyaChecked,
//                    "dzikir_pagi" to dzikirPagiChecked,
//                    "dzikir_sore" to dzikirSoreChecked
//                )
//
//                firestore.collection("users").document(uid)
//                    .collection("checklists").document(todayDate)
//                    .set(checklistData)
//                    .addOnSuccessListener {
//                        Toast.makeText(context, "Checklist berhasil disimpan", Toast.LENGTH_SHORT).show()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(context, "Gagal menyimpan checklist", Toast.LENGTH_SHORT).show()
//                    }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 16.dp)
//        ) {
//            Text("Simpan")
//        }
    }
}

@Composable
fun ChecklistItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}