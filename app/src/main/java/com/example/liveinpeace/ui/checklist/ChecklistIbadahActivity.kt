package com.example.liveinpeace.ui.checklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.features.FeaturesListActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ChecklistIbadahActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var calendarView: CalendarView
    private lateinit var dateDisplay: TextView

    // UI Elements for Prayer Times
    private lateinit var spinnerSubuhJamaah: Spinner
    private lateinit var spinnerDzuhurJamaah: Spinner
    private lateinit var spinnerAsharJamaah: Spinner
    private lateinit var spinnerMaghribJamaah: Spinner
    private lateinit var spinnerIsyaJamaah: Spinner

    // UI Elements for Prayer Checkboxes
    private lateinit var checkSubuhTepat: CheckBox
    private lateinit var checkDzuhurTepat: CheckBox
    private lateinit var checkAsharTepat: CheckBox
    private lateinit var checkMaghribTepat: CheckBox
    private lateinit var checkIsyaTepat: CheckBox

    private lateinit var checkSubuhQadha: CheckBox
    private lateinit var checkDzuhurQadha: CheckBox
    private lateinit var checkAsharQadha: CheckBox
    private lateinit var checkMaghribQadha: CheckBox
    private lateinit var checkIsyaQadha: CheckBox

    private lateinit var checkSubuhBadiyah: CheckBox
    private lateinit var checkDzuhurBadiyah: CheckBox
    private lateinit var checkAsharBadiyah: CheckBox
    private lateinit var checkMaghribBadiyah: CheckBox
    private lateinit var checkIsyaBadiyah: CheckBox

    // UI Elements for Sunnah Prayers
    private lateinit var checkDhuha: CheckBox
    private lateinit var checkTahajud: CheckBox
    private lateinit var checkRawatib: CheckBox
    private lateinit var checkPuasaSunnah: CheckBox
    private lateinit var checkDzikirPagi: CheckBox
    private lateinit var checkDzikirPetang: CheckBox
    private lateinit var checkShalawat: CheckBox
    private lateinit var checkIstighfar: CheckBox
    private lateinit var checkMuhasabah: CheckBox
    private lateinit var checkHafalan: CheckBox

    // UI Elements for Text Fields
    private lateinit var editTextQuranFrom: EditText
    private lateinit var editTextQuranTo: EditText
    private lateinit var editTextHarapan: EditText
    private lateinit var editTextSyukur: EditText

    private lateinit var btnSimpan: Button
    private lateinit var backButton: ImageView

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var selectedDate = dateFormat.format(Date()) // Default to today

    // Flag to prevent toast messages during initial data loading
    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checklist_ibadah)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize views
        initializeViews()

        // Setup spinner adapters
        setupSpinners()

        // Setup calendar
        setupCalendar()

        // Load today's checklist data
        loadChecklist(selectedDate)

        // Setup listeners
        setupListeners()

        // Back button functionality
        backButton.setOnClickListener {
            finish()
        }

        btnSimpan.setOnClickListener {
            saveChecklist()
        }
    }

    private fun initializeViews() {
        // Calendar & Header
        calendarView = findViewById(R.id.calendarView)
        dateDisplay = findViewById(R.id.header_title)
        backButton = findViewById(R.id.back_button)

        // Prayer Jamaah Spinners
        spinnerSubuhJamaah = findViewById(R.id.spinnerSubuhJamaah)
        spinnerDzuhurJamaah = findViewById(R.id.spinnerDzuhurJamaah)
        spinnerAsharJamaah = findViewById(R.id.spinnerAsharJamaah)
        spinnerMaghribJamaah = findViewById(R.id.spinnerMaghribJamaah)
        spinnerIsyaJamaah = findViewById(R.id.spinnerIsyaJamaah)

        // Prayer Checkboxes - Tepat Waktu
        checkSubuhTepat = findViewById(R.id.checkSubuhTepat)
        checkDzuhurTepat = findViewById(R.id.checkDzuhurTepat)
        checkAsharTepat = findViewById(R.id.checkAsharTepat)
        checkMaghribTepat = findViewById(R.id.checkMaghribTepat)
        checkIsyaTepat = findViewById(R.id.checkIsyaTepat)

        // Prayer Checkboxes - Qadha
        checkSubuhQadha = findViewById(R.id.checkSubuhQadha)
        checkDzuhurQadha = findViewById(R.id.checkDzuhurQadha)
        checkAsharQadha = findViewById(R.id.checkAsharQadha)
        checkMaghribQadha = findViewById(R.id.checkMaghribQadha)
        checkIsyaQadha = findViewById(R.id.checkIsyaQadha)

        // Prayer Checkboxes - Ba'diyah
        checkSubuhBadiyah = findViewById(R.id.checkSubuhBadiyah)
        checkDzuhurBadiyah = findViewById(R.id.checkDzuhurBadiyah)
        checkAsharBadiyah = findViewById(R.id.checkAsharBadiyah)
        checkMaghribBadiyah = findViewById(R.id.checkMaghribBadiyah)
        checkIsyaBadiyah = findViewById(R.id.checkIsyaBadiyah)

        // Sunnah Prayers
        checkDhuha = findViewById(R.id.checkDhuha)
        checkTahajud = findViewById(R.id.checkTahajud)
        checkRawatib = findViewById(R.id.checkRawatib)
        checkPuasaSunnah = findViewById(R.id.checkPuasaSunnah)
        checkDzikirPagi = findViewById(R.id.checkDzikirPagi)
        checkDzikirPetang = findViewById(R.id.checkDzikirPetang)
        checkShalawat = findViewById(R.id.checkShalawat)
        checkIstighfar = findViewById(R.id.checkIstighfar)
        checkMuhasabah = findViewById(R.id.checkMuhasabah)
        checkHafalan = findViewById(R.id.checkHafalan)

        // Text Fields
        editTextQuranFrom = findViewById(R.id.editTextQuranFrom)
        editTextQuranTo = findViewById(R.id.editTextQuranTo)
        editTextHarapan = findViewById(R.id.editTextHarapan)
        editTextSyukur = findViewById(R.id.editTextSyukur)

        // Save Button
        btnSimpan = findViewById(R.id.btnSave)
    }

    private fun setupSpinners() {
        val jamaahOptions = arrayOf("Belum", "Jamaah", "Sendiri")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, jamaahOptions)

        spinnerSubuhJamaah.adapter = adapter
        spinnerDzuhurJamaah.adapter = adapter
        spinnerAsharJamaah.adapter = adapter
        spinnerMaghribJamaah.adapter = adapter
        spinnerIsyaJamaah.adapter = adapter
    }

    private fun setupCalendar() {
        // Set calendar to show today's date by default
        calendarView.date = Calendar.getInstance().timeInMillis
        updateHeaderDate(selectedDate)

        // Setup calendar date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)

            updateHeaderDate(selectedDate)
            loadChecklist(selectedDate)
        }
    }

    private fun updateHeaderDate(dateStr: String) {
        val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val date = dateFormat.parse(dateStr) ?: Date()
        val isToday = dateStr == dateFormat.format(Date())

        if (isToday) {
            dateDisplay.text = "Sudahkah kamu beribadah hari ini?"
        } else {
            dateDisplay.text = "Ibadah pada ${displayFormat.format(date)}"
        }
    }

    private fun loadChecklist(date: String) {
        isLoaded = false // Reset flag during loading

        val uid = auth.currentUser?.uid ?: return
        val docRef = firestore.collection("users").document(uid)
            .collection("checklists").document(date)

        docRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                // Jamaah status
                spinnerSubuhJamaah.setSelection(doc.getLong("subuh_jamaah")?.toInt() ?: 0)
                spinnerDzuhurJamaah.setSelection(doc.getLong("dzuhur_jamaah")?.toInt() ?: 0)
                spinnerAsharJamaah.setSelection(doc.getLong("ashar_jamaah")?.toInt() ?: 0)
                spinnerMaghribJamaah.setSelection(doc.getLong("maghrib_jamaah")?.toInt() ?: 0)
                spinnerIsyaJamaah.setSelection(doc.getLong("isya_jamaah")?.toInt() ?: 0)

                // Tepat waktu
                checkSubuhTepat.isChecked = doc.getBoolean("subuh_tepat") ?: false
                checkDzuhurTepat.isChecked = doc.getBoolean("dzuhur_tepat") ?: false
                checkAsharTepat.isChecked = doc.getBoolean("ashar_tepat") ?: false
                checkMaghribTepat.isChecked = doc.getBoolean("maghrib_tepat") ?: false
                checkIsyaTepat.isChecked = doc.getBoolean("isya_tepat") ?: false

                // Qadha
                checkSubuhQadha.isChecked = doc.getBoolean("subuh_qadha") ?: false
                checkDzuhurQadha.isChecked = doc.getBoolean("dzuhur_qadha") ?: false
                checkAsharQadha.isChecked = doc.getBoolean("ashar_qadha") ?: false
                checkMaghribQadha.isChecked = doc.getBoolean("maghrib_qadha") ?: false
                checkIsyaQadha.isChecked = doc.getBoolean("isya_qadha") ?: false

                // Ba'diyah
                checkSubuhBadiyah.isChecked = doc.getBoolean("subuh_badiyah") ?: false
                checkDzuhurBadiyah.isChecked = doc.getBoolean("dzuhur_badiyah") ?: false
                checkAsharBadiyah.isChecked = doc.getBoolean("ashar_badiyah") ?: false
                checkMaghribBadiyah.isChecked = doc.getBoolean("maghrib_badiyah") ?: false
                checkIsyaBadiyah.isChecked = doc.getBoolean("isya_badiyah") ?: false

                // Sunnah prayers
                checkDhuha.isChecked = doc.getBoolean("dhuha") ?: false
                checkTahajud.isChecked = doc.getBoolean("tahajud") ?: false
                checkRawatib.isChecked = doc.getBoolean("rawatib") ?: false
                checkPuasaSunnah.isChecked = doc.getBoolean("puasa_sunnah") ?: false
                checkDzikirPagi.isChecked = doc.getBoolean("dzikir_pagi") ?: false
                checkDzikirPetang.isChecked = doc.getBoolean("dzikir_petang") ?: false
                checkShalawat.isChecked = doc.getBoolean("shalawat") ?: false
                checkIstighfar.isChecked = doc.getBoolean("istighfar") ?: false
                checkMuhasabah.isChecked = doc.getBoolean("muhasabah") ?: false
                checkHafalan.isChecked = doc.getBoolean("hafalan") ?: false

                // Text fields
                editTextQuranFrom.setText(doc.getString("quran_from") ?: "")
                editTextQuranTo.setText(doc.getString("quran_to") ?: "")
                editTextHarapan.setText(doc.getString("harapan") ?: "")
                editTextSyukur.setText(doc.getString("syukur") ?: "")
            } else {
                // Reset all fields if no data exists for this date
                resetAllFields()
            }
            isLoaded = true
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            resetAllFields()
            isLoaded = true
        }
    }

    private fun resetAllFields() {
        // Reset spinners
        spinnerSubuhJamaah.setSelection(0)
        spinnerDzuhurJamaah.setSelection(0)
        spinnerAsharJamaah.setSelection(0)
        spinnerMaghribJamaah.setSelection(0)
        spinnerIsyaJamaah.setSelection(0)

        // Reset all checkboxes
        val allCheckboxes = listOf(
            checkSubuhTepat, checkDzuhurTepat, checkAsharTepat, checkMaghribTepat, checkIsyaTepat,
            checkSubuhQadha, checkDzuhurQadha, checkAsharQadha, checkMaghribQadha, checkIsyaQadha,
            checkSubuhBadiyah, checkDzuhurBadiyah, checkAsharBadiyah, checkMaghribBadiyah, checkIsyaBadiyah,
            checkDhuha, checkTahajud, checkRawatib, checkPuasaSunnah, checkDzikirPagi,
            checkDzikirPetang, checkShalawat, checkIstighfar, checkMuhasabah, checkHafalan
        )

        allCheckboxes.forEach { it.isChecked = false }

        // Reset text fields
        editTextQuranFrom.setText("")
        editTextQuranTo.setText("")
        editTextHarapan.setText("")
        editTextSyukur.setText("")
    }

    private fun setupListeners() {
        // Add listeners for prayers if needed
        // This is where you would add toast messages for checkbox changes

        // Example for Dzikir Pagi:
        checkDzikirPagi.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) {
                val msg = if (isChecked) "Masya Allah! Dzikir paginya jangan sampai ketinggalan ya!" else "Yuk semangat mulai dzikir pagi!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        // Example for Dzikir Petang:
        checkDzikirPetang.setOnCheckedChangeListener { _, isChecked ->
            if (isLoaded) {
                val msg = if (isChecked) "Masya Allah! Dzikir petang udah dilakukan~" else "Dzikir petang jangan lupa yaa 🥹"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        // You can add more listeners for other checkboxes as needed
    }

    private fun saveChecklist() {
        val uid = auth.currentUser?.uid ?: return
        val checklistData = hashMapOf(
            "date" to selectedDate,

            // Jamaah status (store as integers for spinner positions)
            "subuh_jamaah" to spinnerSubuhJamaah.selectedItemPosition,
            "dzuhur_jamaah" to spinnerDzuhurJamaah.selectedItemPosition,
            "ashar_jamaah" to spinnerAsharJamaah.selectedItemPosition,
            "maghrib_jamaah" to spinnerMaghribJamaah.selectedItemPosition,
            "isya_jamaah" to spinnerIsyaJamaah.selectedItemPosition,

            // Tepat waktu
            "subuh_tepat" to checkSubuhTepat.isChecked,
            "dzuhur_tepat" to checkDzuhurTepat.isChecked,
            "ashar_tepat" to checkAsharTepat.isChecked,
            "maghrib_tepat" to checkMaghribTepat.isChecked,
            "isya_tepat" to checkIsyaTepat.isChecked,

            // Qadha
            "subuh_qadha" to checkSubuhQadha.isChecked,
            "dzuhur_qadha" to checkDzuhurQadha.isChecked,
            "ashar_qadha" to checkAsharQadha.isChecked,
            "maghrib_qadha" to checkMaghribQadha.isChecked,
            "isya_qadha" to checkIsyaQadha.isChecked,

            // Ba'diyah
            "subuh_badiyah" to checkSubuhBadiyah.isChecked,
            "dzuhur_badiyah" to checkDzuhurBadiyah.isChecked,
            "ashar_badiyah" to checkAsharBadiyah.isChecked,
            "maghrib_badiyah" to checkMaghribBadiyah.isChecked,
            "isya_badiyah" to checkIsyaBadiyah.isChecked,

            // Sunnah prayers
            "dhuha" to checkDhuha.isChecked,
            "tahajud" to checkTahajud.isChecked,
            "rawatib" to checkRawatib.isChecked,
            "puasa_sunnah" to checkPuasaSunnah.isChecked,
            "dzikir_pagi" to checkDzikirPagi.isChecked,
            "dzikir_petang" to checkDzikirPetang.isChecked,
            "shalawat" to checkShalawat.isChecked,
            "istighfar" to checkIstighfar.isChecked,
            "muhasabah" to checkMuhasabah.isChecked,
            "hafalan" to checkHafalan.isChecked,

            // Text fields
            "quran_to" to editTextQuranTo.text.toString(),
            "quran_from" to editTextQuranFrom.text.toString(),
            "harapan" to editTextHarapan.text.toString(),
            "syukur" to editTextSyukur.text.toString()
        )

        firestore.collection("users").document(uid)
            .collection("checklists").document(selectedDate)
            .set(checklistData)
            .addOnSuccessListener {
                val todayFormat = dateFormat.format(Date())
                if (selectedDate == todayFormat) {
                    Toast.makeText(this, "Checklist ibadah hari ini berhasil disimpan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Checklist ibadah tanggal ${formatDateForDisplay(selectedDate)} berhasil disimpan", Toast.LENGTH_SHORT).show()
                }

                // Only navigate back to feature list if this is today's checklist
                if (selectedDate == todayFormat) {
                    val intent = Intent(this, FeaturesListActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan checklist", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatDateForDisplay(dateStr: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val date = inputFormat.parse(dateStr) ?: return dateStr
        return outputFormat.format(date)
    }
}

//package com.example.liveinpeace.ui.checklist
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.CheckBox
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.R
//import com.example.liveinpeace.ui.features.FeatureListActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//import java.util.*
//
//class ChecklistIbadahActivity : AppCompatActivity() {
//
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var auth: FirebaseAuth
//
//    private lateinit var checkSubuh: CheckBox
//    private lateinit var checkDzuhur: CheckBox
//    private lateinit var checkAshar: CheckBox
//    private lateinit var checkMaghrib: CheckBox
//    private lateinit var checkIsya: CheckBox
//    private lateinit var checkDzikirPagi: CheckBox
//    private lateinit var checkDzikirSore: CheckBox
//    private lateinit var btnSimpan: Button
//
//    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    private val todayDate = dateFormat.format(Date())
//
//    // Flag agar toast hanya muncul saat user mencentang sendiri
//    private var isLoaded = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_checklist_ibadah)
//
//        firestore = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
//        checkSubuh = findViewById(R.id.checkSubuh)
//        checkDzuhur = findViewById(R.id.checkDzuhur)
//        checkAshar = findViewById(R.id.checkAshar)
//        checkMaghrib = findViewById(R.id.checkMaghrib)
//        checkIsya = findViewById(R.id.checkIsya)
//        checkDzikirPagi = findViewById(R.id.checkDzikirPagi)
//        checkDzikirSore = findViewById(R.id.checkDzikirSore)
//        btnSimpan = findViewById(R.id.btnSimpan)
//
//        loadChecklist()
//
//        setupCheckListeners()
//
//        btnSimpan.setOnClickListener {
//            saveChecklist()
//        }
//    }
//
//    private fun loadChecklist() {
//        val uid = auth.currentUser?.uid ?: return
//        val docRef = firestore.collection("users").document(uid)
//            .collection("checklists").document(todayDate)
//
//        docRef.get().addOnSuccessListener { doc ->
//            if (doc.exists()) {
//                checkSubuh.isChecked = doc.getBoolean("subuh") ?: false
//                checkDzuhur.isChecked = doc.getBoolean("dzuhur") ?: false
//                checkAshar.isChecked = doc.getBoolean("ashar") ?: false
//                checkMaghrib.isChecked = doc.getBoolean("maghrib") ?: false
//                checkIsya.isChecked = doc.getBoolean("isya") ?: false
//                checkDzikirPagi.isChecked = doc.getBoolean("dzikir_pagi") ?: false
//                checkDzikirSore.isChecked = doc.getBoolean("dzikir_sore") ?: false
//            }
//            isLoaded = true
//        }
//    }
//
//    private fun setupCheckListeners() {
//        checkSubuh.setOnCheckedChangeListener { _, isChecked ->
//            if (isLoaded) showToast(isChecked, "Subuh")
//        }
//        checkDzuhur.setOnCheckedChangeListener { _, isChecked ->
//            if (isLoaded) showToast(isChecked, "Dzuhur")
//        }
//        checkAshar.setOnCheckedChangeListener { _, isChecked ->
//            if (isLoaded) showToast(isChecked, "Ashar")
//        }
//        checkMaghrib.setOnCheckedChangeListener { _, isChecked ->
//            if (isLoaded) showToast(isChecked, "Maghrib")
//        }
//        checkIsya.setOnCheckedChangeListener { _, isChecked ->
//            if (isLoaded) showToast(isChecked, "Isya")
//        }
//        checkDzikirPagi.setOnCheckedChangeListener { _, isChecked ->
//            if (isLoaded) {
//                val msg = if (isChecked) "Masya Allah! Dzikir paginya jangan sampai ketinggalan ya!" else "Yuk semangat mulai dzikir pagi!"
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//            }
//        }
//        checkDzikirSore.setOnCheckedChangeListener { _, isChecked ->
//            if (isLoaded) {
//                val msg = if (isChecked) "Masya Allah! Dzikir sore udah dilakukan~" else "Dzikir sore jangan lupa yaa 🥹"
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun showToast(isChecked: Boolean, name: String) {
//        val msg = if (isChecked) "Masya Allah! $name udah dikerjain!" else "Yuk jangan tinggalin $name ya!"
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun saveChecklist() {
//        val uid = auth.currentUser?.uid ?: return
//        val checklistData = hashMapOf(
//            "date" to todayDate,
//            "subuh" to checkSubuh.isChecked,
//            "dzuhur" to checkDzuhur.isChecked,
//            "ashar" to checkAshar.isChecked,
//            "maghrib" to checkMaghrib.isChecked,
//            "isya" to checkIsya.isChecked,
//            "dzikir_pagi" to checkDzikirPagi.isChecked,
//            "dzikir_sore" to checkDzikirSore.isChecked
//        )
//
//        firestore.collection("users").document(uid)
//            .collection("checklists").document(todayDate)
//            .set(checklistData)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Checklist berhasil disimpan", Toast.LENGTH_SHORT).show()
//                Log.d("ChecklistActivity", "Simpan sukses, mau ke FeatureListActivity")
//                // Pindah ke halaman Ruang Tenang
//                val intent = Intent(this, FeatureListActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Gagal menyimpan checklist", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
