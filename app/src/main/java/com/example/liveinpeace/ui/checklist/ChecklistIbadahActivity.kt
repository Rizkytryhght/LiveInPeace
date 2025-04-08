package com.example.liveinpeace.ui.checklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.features.FeatureListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ChecklistIbadahActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var checkSubuh: CheckBox
    private lateinit var checkDzuhur: CheckBox
    private lateinit var checkAshar: CheckBox
    private lateinit var checkMaghrib: CheckBox
    private lateinit var checkIsya: CheckBox
    private lateinit var checkDzikirPagi: CheckBox
    private lateinit var checkDzikirSore: CheckBox
    private lateinit var btnSimpan: Button

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayDate = dateFormat.format(Date())

    // Flag agar toast hanya muncul saat user mencentang sendiri
    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist_ibadah)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        checkSubuh = findViewById(R.id.checkSubuh)
        checkDzuhur = findViewById(R.id.checkDzuhur)
        checkAshar = findViewById(R.id.checkAshar)
        checkMaghrib = findViewById(R.id.checkMaghrib)
        checkIsya = findViewById(R.id.checkIsya)
        checkDzikirPagi = findViewById(R.id.checkDzikirPagi)
        checkDzikirSore = findViewById(R.id.checkDzikirSore)
        btnSimpan = findViewById(R.id.btnSimpan)

        loadChecklist()

        setupCheckListeners()

        btnSimpan.setOnClickListener {
            saveChecklist()
        }
    }

    private fun loadChecklist() {
        val uid = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("users").document(uid)
            .collection("checklists").document(todayDate)

        docRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                checkSubuh.isChecked = doc.getBoolean("subuh") ?: false
                checkDzuhur.isChecked = doc.getBoolean("dzuhur") ?: false
                checkAshar.isChecked = doc.getBoolean("ashar") ?: false
                checkMaghrib.isChecked = doc.getBoolean("maghrib") ?: false
                checkIsya.isChecked = doc.getBoolean("isya") ?: false
                checkDzikirPagi.isChecked = doc.getBoolean("dzikir_pagi") ?: false
                checkDzikirSore.isChecked = doc.getBoolean("dzikir_sore") ?: false
            }
            isLoaded = true
        }
    }

    private fun setupCheckListeners() {
        checkSubuh.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) showToast(isChecked, "Subuh")
        }
        checkDzuhur.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) showToast(isChecked, "Dzuhur")
        }
        checkAshar.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) showToast(isChecked, "Ashar")
        }
        checkMaghrib.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) showToast(isChecked, "Maghrib")
        }
        checkIsya.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) showToast(isChecked, "Isya")
        }
        checkDzikirPagi.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) {
                val msg = if (isChecked) "Masya Allah! Dzikir paginya jangan sampai ketinggalan ya!" else "Yuk semangat mulai dzikir pagi!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
        checkDzikirSore.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) {
                val msg = if (isChecked) "Masya Allah! Dzikir sore udah dilakukan~" else "Dzikir sore jangan lupa yaa 🥹"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showToast(isChecked: Boolean, name: String) {
        val msg = if (isChecked) "Masya Allah! $name udah dikerjain!" else "Yuk jangan tinggalin $name ya!"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun saveChecklist() {
        val uid = auth.currentUser?.uid ?: return
        val checklistData = hashMapOf(
            "date" to todayDate,
            "subuh" to checkSubuh.isChecked,
            "dzuhur" to checkDzuhur.isChecked,
            "ashar" to checkAshar.isChecked,
            "maghrib" to checkMaghrib.isChecked,
            "isya" to checkIsya.isChecked,
            "dzikir_pagi" to checkDzikirPagi.isChecked,
            "dzikir_sore" to checkDzikirSore.isChecked
        )

        firestore.collection("users").document(uid)
            .collection("checklists").document(todayDate)
            .set(checklistData)
            .addOnSuccessListener {
                Toast.makeText(this, "Checklist berhasil disimpan", Toast.LENGTH_SHORT).show()
                Log.d("ChecklistActivity", "Simpan sukses, mau ke FeatureListActivity")
                // Pindah ke halaman Ruang Tenang
                val intent = Intent(this, FeatureListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan checklist", Toast.LENGTH_SHORT).show()
            }
    }
}
