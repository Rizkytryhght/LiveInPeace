package com.example.liveinpeace.ui.checklist

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ChecklistActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var cbSubuh: CheckBox
    private lateinit var cbDzuhur: CheckBox
    private lateinit var cbAshar: CheckBox
    private lateinit var cbMaghrib: CheckBox
    private lateinit var cbIsya: CheckBox
    private lateinit var cbDzikirPagi: CheckBox
    private lateinit var cbDzikirSore: CheckBox
    private lateinit var btnSimpan: Button

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayDate = dateFormat.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist_ibadah)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

//        cbSubuh = findViewById(R.id.checkSubuh)
//        cbDzuhur = findViewById(R.id.checkDzuhur)
//        cbAshar = findViewById(R.id.checkAshar)
//        cbMaghrib = findViewById(R.id.checkMaghrib)
//        cbIsya = findViewById(R.id.checkIsya)
//        cbDzikirPagi = findViewById(R.id.checkDzikirPagi)
//        cbDzikirSore = findViewById(R.id.checkDzikirSore)
//        btnSimpan = findViewById<Button>(R.id.btnSimpan)

        loadChecklist()

        Log.d("ChecklistActivity", "btnSimpan: $btnSimpan")
        btnSimpan.setOnClickListener {
            Log.d("ChecklistActivity", "TOMBOL DISENTUH 🥺")
            Toast.makeText(this, "Checklist disimpan!", Toast.LENGTH_SHORT).show()
            saveChecklist()
        }
    }

    private fun loadChecklist() {
        val uid = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("users").document(uid)
            .collection("checklists").document(todayDate)

        docRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                cbSubuh.isChecked = doc.getBoolean("subuh") ?: false
                cbDzuhur.isChecked = doc.getBoolean("dzuhur") ?: false
                cbAshar.isChecked = doc.getBoolean("ashar") ?: false
                cbMaghrib.isChecked = doc.getBoolean("maghrib") ?: false
                cbIsya.isChecked = doc.getBoolean("isya") ?: false
                cbDzikirPagi.isChecked = doc.getBoolean("dzikir_pagi") ?: false
                cbDzikirSore.isChecked = doc.getBoolean("dzikir_sore") ?: false
            }
        }
    }

    private fun saveChecklist() {
        val uid = auth.currentUser?.uid ?: return
        val checklistData = hashMapOf(
            "date" to todayDate,
            "subuh" to cbSubuh.isChecked,
            "dzuhur" to cbDzuhur.isChecked,
            "ashar" to cbAshar.isChecked,
            "maghrib" to cbMaghrib.isChecked,
            "isya" to cbIsya.isChecked,
            "dzikir_pagi" to cbDzikirPagi.isChecked,
            "dzikir_sore" to cbDzikirSore.isChecked
        )

        firestore.collection("users").document(uid)
            .collection("checklists").document(todayDate)
            .set(checklistData)
            .addOnSuccessListener {
                Toast.makeText(this, "Checklist berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan checklist", Toast.LENGTH_SHORT).show()
            }
    }
}