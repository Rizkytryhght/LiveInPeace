//package com.example.liveinpeace.viewModel
//
//import android.content.Context
//import android.widget.Toast
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.liveinpeace.R
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.launch
//import java.text.SimpleDateFormat
//import java.util.*
//
//class ChecklistIbadahViewModel : ViewModel() {
//    private val firestore = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//    // State untuk tanggal
//    val selectedDate = mutableStateOf(dateFormat.format(Date()))
//    val isToday = mutableStateOf(true) // Tambahkan state untuk melacak apakah tanggal adalah hari ini
//
//    // State untuk Spinner (Jamaah/Sendiri)
//    val subuhJamaah = mutableStateOf(0)
//    val dzuhurJamaah = mutableStateOf(0)
//    val asharJamaah = mutableStateOf(0)
//    val maghribJamaah = mutableStateOf(0)
//    val isyaJamaah = mutableStateOf(0)
//
//    // State untuk CheckBox (Sholat Wajib)
//    val subuhTepat = mutableStateOf(false)
//    val dzuhurTepat = mutableStateOf(false)
//    val asharTepat = mutableStateOf(false)
//    val maghribTepat = mutableStateOf(false)
//    val isyaTepat = mutableStateOf(false)
//
//    val subuhQadha = mutableStateOf(false)
//    val dzuhurQadha = mutableStateOf(false)
//    val asharQadha = mutableStateOf(false)
//    val maghribQadha = mutableStateOf(false)
//    val isyaQadha = mutableStateOf(false)
//
//    val subuhBadiyah = mutableStateOf(false)
//    val dzuhurBadiyah = mutableStateOf(false)
//    val asharBadiyah = mutableStateOf(false)
//    val maghribBadiyah = mutableStateOf(false)
//    val isyaBadiyah = mutableStateOf(false)
//
//    // State untuk CheckBox (Ibadah Sunnah)
//    val dhuha = mutableStateOf(false)
//    val tahajud = mutableStateOf(false)
//    val rawatib = mutableStateOf(false)
//    val puasaSunnah = mutableStateOf(false)
//    val dzikirPagi = mutableStateOf(false)
//    val dzikirPetang = mutableStateOf(false)
//    val shalawat = mutableStateOf(false)
//    val istighfar = mutableStateOf(false)
//    val muhasabah = mutableStateOf(false)
//    val hafalan = mutableStateOf(false)
//
//    // State untuk TextField
//    val quranFrom = mutableStateOf("")
//    val quranTo = mutableStateOf("")
//    val harapan = mutableStateOf("")
//    val syukur = mutableStateOf("")
//
//    // Flag untuk mencegah Toast saat load
//    private var isLoaded = mutableStateOf(false)
//
//    init {
//        loadChecklist(selectedDate.value)
//    }
//
//    fun updateSelectedDate(dateInMillis: Long, context: Context) {
//        val newDate = dateFormat.format(Date(dateInMillis))
//        selectedDate.value = newDate
//        isToday.value = newDate == dateFormat.format(Date())
//        loadChecklist(newDate, context)
//    }
//
//    private fun loadChecklist(date: String, context: Context? = null) {
//        isLoaded.value = false
//        val uid = auth.currentUser?.uid ?: return
//        val docRef = firestore.collection("users").document(uid)
//            .collection("checklists").document(date)
//
//        viewModelScope.launch {
//            docRef.get().addOnSuccessListener { doc ->
//                if (doc.exists()) {
//                    subuhJamaah.value = doc.getLong("subuh_jamaah")?.toInt() ?: 0
//                    dzuhurJamaah.value = doc.getLong("dzuhur_jamaah")?.toInt() ?: 0
//                    asharJamaah.value = doc.getLong("ashar_jamaah")?.toInt() ?: 0
//                    maghribJamaah.value = doc.getLong("maghrib_jamaah")?.toInt() ?: 0
//                    isyaJamaah.value = doc.getLong("isya_jamaah")?.toInt() ?: 0
//
//                    subuhTepat.value = doc.getBoolean("subuh_tepat") ?: false
//                    dzuhurTepat.value = doc.getBoolean("dzuhur_tepat") ?: false
//                    asharTepat.value = doc.getBoolean("ashar_tepat") ?: false
//                    maghribTepat.value = doc.getBoolean("maghrib_tepat") ?: false
//                    isyaTepat.value = doc.getBoolean("isya_tepat") ?: false
//
//                    subuhQadha.value = doc.getBoolean("subuh_qadha") ?: false
//                    dzuhurQadha.value = doc.getBoolean("dzuhur_qadha") ?: false
//                    asharQadha.value = doc.getBoolean("ashar_qadha") ?: false
//                    maghribQadha.value = doc.getBoolean("maghrib_qadha") ?: false
//                    isyaQadha.value = doc.getBoolean("isya_qadha") ?: false
//
//                    subuhBadiyah.value = doc.getBoolean("subuh_badiyah") ?: false
//                    dzuhurBadiyah.value = doc.getBoolean("dzuhur_badiyah") ?: false
//                    asharBadiyah.value = doc.getBoolean("ashar_badiyah") ?: false
//                    maghribBadiyah.value = doc.getBoolean("maghrib_badiyah") ?: false
//                    isyaBadiyah.value = doc.getBoolean("isya_badiyah") ?: false
//
//                    dhuha.value = doc.getBoolean("dhuha") ?: false
//                    tahajud.value = doc.getBoolean("tahajud") ?: false
//                    rawatib.value = doc.getBoolean("rawatib") ?: false
//                    puasaSunnah.value = doc.getBoolean("puasa_sunnah") ?: false
//                    dzikirPagi.value = doc.getBoolean("dzikir_pagi") ?: false
//                    dzikirPetang.value = doc.getBoolean("dzikir_petang") ?: false
//                    shalawat.value = doc.getBoolean("shalawat") ?: false
//                    istighfar.value = doc.getBoolean("istighfar") ?: false
//                    muhasabah.value = doc.getBoolean("muhasabah") ?: false
//                    hafalan.value = doc.getBoolean("hafalan") ?: false
//
//                    quranFrom.value = doc.getString("quran_from") ?: ""
//                    quranTo.value = doc.getString("quran_to") ?: ""
//                    harapan.value = doc.getString("harapan") ?: ""
//                    syukur.value = doc.getString("syukur") ?: ""
//                } else {
//                    resetAllFields()
//                }
//                isLoaded.value = true
//            }.addOnFailureListener {
//                context?.let {
//                    Toast.makeText(it, R.string.checklist_load_failed, Toast.LENGTH_SHORT).show()
//                }
//                resetAllFields()
//                isLoaded.value = true
//            }
//        }
//    }
//
//    private fun resetAllFields() {
//        subuhJamaah.value = 0
//        dzuhurJamaah.value = 0
//        asharJamaah.value = 0
//        maghribJamaah.value = 0
//        isyaJamaah.value = 0
//
//        subuhTepat.value = false
//        dzuhurTepat.value = false
//        asharTepat.value = false
//        maghribTepat.value = false
//        isyaTepat.value = false
//
//        subuhQadha.value = false
//        dzuhurQadha.value = false
//        asharQadha.value = false
//        maghribQadha.value = false
//        isyaQadha.value = false
//
//        subuhBadiyah.value = false
//        dzuhurBadiyah.value = false
//        asharBadiyah.value = false
//        maghribBadiyah.value = false
//        isyaBadiyah.value = false
//
//        dhuha.value = false
//        tahajud.value = false
//        rawatib.value = false
//        puasaSunnah.value = false
//        dzikirPagi.value = false
//        dzikirPetang.value = false
//        shalawat.value = false
//        istighfar.value = false
//        muhasabah.value = false
//        hafalan.value = false
//
//        quranFrom.value = ""
//        quranTo.value = ""
//        harapan.value = ""
//        syukur.value = ""
//    }
//
//    fun saveChecklist(context: Context, onNavigateBack: () -> Unit) {
//        val uid = auth.currentUser?.uid ?: return
//        val checklistData = hashMapOf(
//            "date" to selectedDate.value,
//            "subuh_jamaah" to subuhJamaah.value,
//            "dzuhur_jamaah" to dzuhurJamaah.value,
//            "ashar_jamaah" to asharJamaah.value,
//            "maghrib_jamaah" to maghribJamaah.value,
//            "isya_jamaah" to isyaJamaah.value,
//            "subuh_tepat" to subuhTepat.value,
//            "dzuhur_tepat" to dzuhurTepat.value,
//            "ashar_tepat" to asharTepat.value,
//            "maghrib_tepat" to maghribTepat.value,
//            "isya_tepat" to isyaTepat.value,
//            "subuh_qadha" to subuhQadha.value,
//            "dzuhur_qadha" to dzuhurQadha.value,
//            "ashar_qadha" to asharQadha.value,
//            "maghrib_qadha" to maghribQadha.value,
//            "isya_qadha" to isyaQadha.value,
//            "subuh_badiyah" to subuhBadiyah.value,
//            "dzuhur_badiyah" to dzuhurBadiyah.value,
//            "ashar_badiyah" to asharBadiyah.value,
//            "maghrib_badiyah" to maghribBadiyah.value,
//            "isya_badiyah" to isyaBadiyah.value,
//            "dhuha" to dhuha.value,
//            "tahajud" to tahajud.value,
//            "rawatib" to rawatib.value,
//            "puasa_sunnah" to puasaSunnah.value,
//            "dzikir_pagi" to dzikirPagi.value,
//            "dzikir_petang" to dzikirPetang.value,
//            "shalawat" to shalawat.value,
//            "istighfar" to istighfar.value,
//            "muhasabah" to muhasabah.value,
//            "hafalan" to hafalan.value,
//            "quran_from" to quranFrom.value,
//            "quran_to" to quranTo.value,
//            "harapan" to harapan.value,
//            "syukur" to syukur.value
//        )
//
//        viewModelScope.launch {
//            firestore.collection("users").document(uid)
//                .collection("checklists").document(selectedDate.value)
//                .set(checklistData)
//                .addOnSuccessListener {
//                    val today = dateFormat.format(Date())
//                    if (selectedDate.value == today) {
//                        Toast.makeText(context, R.string.checklist_save_success_today, Toast.LENGTH_SHORT).show()
//                        onNavigateBack()
//                    } else {
//                        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
//                        val displayDate = displayFormat.format(dateFormat.parse(selectedDate.value) ?: Date())
//                        Toast.makeText(context, context.getString(R.string.checklist_save_success_date, displayDate), Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .addOnFailureListener {
//                    Toast.makeText(context, R.string.checklist_save_failed, Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
//
//    fun showDzikirPagiToast(context: Context, isChecked: Boolean) {
//        if (isLoaded.value) {
//            val msg = if (isChecked) {
//                context.getString(R.string.dzikir_pagi_checked)
//            } else {
//                context.getString(R.string.dzikir_pagi_unchecked)
//            }
//            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun showDzikirPetangToast(context: Context, isChecked: Boolean) {
//        if (isLoaded.value) {
//            val msg = if (isChecked) {
//                context.getString(R.string.dzikir_petang_checked)
//            } else {
//                context.getString(R.string.dzikir_petang_unchecked)
//            }
//            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
//        }
//    }
//}