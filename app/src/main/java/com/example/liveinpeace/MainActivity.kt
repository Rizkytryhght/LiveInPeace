package com.example.liveinpeace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.liveinpeace.ui.auth.AuthActivity
import com.example.liveinpeace.ui.note.NoteActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, AuthActivity::class.java))
        } else {
            startActivity(Intent(this, NoteActivity::class.java))
        }

        finish()
    }
}

//package com.example.liveinpeace
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.liveinpeace.ui.auth.AuthActivity
//import com.example.liveinpeace.ui.note.NoteActivity
//import com.example.liveinpeace.ui.theme.GreenPrimary
//import com.example.liveinpeace.ui.theme.LiveInPeaceTheme
//import com.example.liveinpeace.viewModel.AuthViewModel
//import com.google.firebase.auth.FirebaseAuth
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            val authViewModel: AuthViewModel = viewModel()
//            val isEncryptionReady by authViewModel.isEncryptionReady.collectAsState()
//            val context = LocalContext.current
//            var authError by remember { mutableStateOf<String?>(null) }
//
//            LiveInPeaceTheme {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.White),
//                    contentAlignment = Alignment.Center
//                ) {
//                    LaunchedEffect(Unit) {
//                        Log.d("MainActivity", "Checking Firebase user")
//                        try {
//                            val user = FirebaseAuth.getInstance().currentUser
//                            if (user == null) {
//                                Log.d("MainActivity", "No user logged in, redirecting to AuthActivity")
//                                Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
//                                context.startActivity(Intent(context, AuthActivity::class.java))
//                                (context as Activity).finish()
//                            }
//                        } catch (e: Exception) {
//                            Log.e("MainActivity", "Exception checking Firebase user: ${e.message}", e)
//                            authError = "Gagal memuat layanan autentikasi. Cek koneksi atau Google Play Services."
//                        }
//                    }
//
//                    if (authError != null) {
//                        LaunchedEffect(authError) {
//                            Toast.makeText(context, authError, Toast.LENGTH_LONG).show()
//                            context.startActivity(Intent(context, AuthActivity::class.java))
//                            (context as Activity).finish()
//                        }
//                    } else {
//                        when (isEncryptionReady) {
//                            null -> {
//                                Log.d("MainActivity", "Waiting for encryption key")
//                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                    CircularProgressIndicator(color = GreenPrimary)
//                                    Spacer(modifier = Modifier.height(16.dp))
//                                    Text(
//                                        text = "Memuat kunci enkripsi...",
//                                        color = Color(0xFF2C3E50),
//                                        fontSize = 16.sp
//                                    )
//                                }
//                            }
//                            true -> {
//                                Log.d("MainActivity", "Encryption ready, redirecting to NoteActivity")
//                                LaunchedEffect(Unit) {
//                                    context.startActivity(Intent(context, NoteActivity::class.java))
//                                    (context as Activity).finish()
//                                }
//                            }
//                            false -> {
//                                Log.e("MainActivity", "Encryption failed, logging out")
//                                LaunchedEffect(Unit) {
//                                    authViewModel.logout()
//                                    Toast.makeText(
//                                        context,
//                                        "Gagal memuat kunci enkripsi. Silakan login ulang.",
//                                        Toast.LENGTH_LONG
//                                    ).show()
//                                    context.startActivity(Intent(context, AuthActivity::class.java))
//                                    (context as Activity).finish()
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

//import android.content.Intent
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import com.example.liveinpeace.ui.auth.AuthActivity
//import com.example.liveinpeace.ui.note.NoteActivity
//import com.example.liveinpeace.viewModel.AuthViewModel
//import com.google.firebase.auth.FirebaseAuth
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//class MainActivity : AppCompatActivity() {
//
//    private val authViewModel: AuthViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user == null) {
//            // Pengguna belum login, arahkan ke AuthActivity
//            startActivity(Intent(this, AuthActivity::class.java))
//            finish()
//            return
//        }
//
//        // Pengguna sudah login, periksa status kunci enkripsi
//        CoroutineScope(Dispatchers.Main).launch {
//            authViewModel.isEncryptionReady.collectLatest { isReady ->
//                if (isReady) {
//                    // Kunci enkripsi siap, arahkan ke NoteActivity
//                    startActivity(Intent(this@MainActivity, NoteActivity::class.java))
//                    finish()
//                } else {
//                    // Kunci enkripsi tidak tersedia, logout dan arahkan ke AuthActivity
//                    authViewModel.logout()
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Gagal memuat kunci enkripsi. Silakan login ulang.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    startActivity(Intent(this@MainActivity, AuthActivity::class.java))
//                    finish()
//                }
//            }
//        }
//    }
//}
