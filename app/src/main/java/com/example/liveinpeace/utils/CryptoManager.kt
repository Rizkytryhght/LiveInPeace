//package com.example.liveinpeace.utils
//
//import android.util.Base64
//import android.util.Log
//import java.nio.charset.StandardCharsets
//import java.security.SecureRandom
//import javax.crypto.Cipher
//import javax.crypto.SecretKey
//import javax.crypto.SecretKeyFactory
//import javax.crypto.spec.GCMParameterSpec
//import javax.crypto.spec.PBEKeySpec
//import javax.crypto.spec.SecretKeySpec
//
//class CryptoManager {
//    companion object {
//        private const val TRANSFORMATION = "AES/GCM/NoPadding"
//        private const val KEY_SIZE = 256
//        private const val IV_SIZE = 12
//        private const val TAG_SIZE = 16
//        private const val PBKDF2_ITERATIONS = 10000
//        private const val TAG = "CryptoManager"
//
//        /**
//         * Generate encryption key dari user email + password
//         * Key ini akan selalu sama untuk user yang sama
//         */
//        fun deriveKeyFromPassword(email: String, password: String): SecretKey? {
//            return try {
//                if (email.isBlank() || password.isBlank()) {
//                    Log.e("CryptoManager", "Email or password is blank: email='$email', passwordLength=${password.length}")
//                    return null
//                }
//                val salt = email.toByteArray(StandardCharsets.UTF_8)
//                Log.d("CryptoManager", "Generating key: email='$email', saltLength=${salt.size}")
//                val keySpec = PBEKeySpec(password.toCharArray(), salt, 100000, 256)
//                val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
//                val keyBytes = keyFactory.generateSecret(keySpec).encoded
//                SecretKeySpec(keyBytes, "AES").also {
//                    Log.d("CryptoManager", "Key generated successfully")
//                }
//            } catch (e: Exception) {
//                Log.e("CryptoManager", "Failed to derive key: ${e.message}", e)
//                null
//            }
//        }
//
//        /**
//         * Encrypt string data menggunakan AES-GCM
//         */
//        fun encrypt(data: String, secretKey: SecretKey): String? {
//            return try {
//                val cipher = Cipher.getInstance(TRANSFORMATION)
//
//                // Generate random IV untuk setiap enkripsi
//                val iv = ByteArray(IV_SIZE)
//                SecureRandom().nextBytes(iv)
//                val gcmSpec = GCMParameterSpec(TAG_SIZE * 8, iv)
//
//                cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
//                val encryptedData = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
//
//                // Gabungkan IV + encrypted data
//                val result = ByteArray(IV_SIZE + encryptedData.size)
//                System.arraycopy(iv, 0, result, 0, IV_SIZE)
//                System.arraycopy(encryptedData, 0, result, IV_SIZE, encryptedData.size)
//
//                Base64.encodeToString(result, Base64.DEFAULT)
//            } catch (e: Exception) {
//                Log.e(TAG, "Error encrypting data", e)
//                null
//            }
//        }
//
//        /**
//         * Decrypt string data menggunakan AES-GCM
//         */
//        fun decrypt(encryptedData: String, secretKey: SecretKey): String? {
//            return try {
//                val data = Base64.decode(encryptedData, Base64.DEFAULT)
//
//                // Extract IV dan encrypted data
//                val iv = ByteArray(IV_SIZE)
//                val encrypted = ByteArray(data.size - IV_SIZE)
//                System.arraycopy(data, 0, iv, 0, IV_SIZE)
//                System.arraycopy(data, IV_SIZE, encrypted, 0, encrypted.size)
//
//                val cipher = Cipher.getInstance(TRANSFORMATION)
//                val gcmSpec = GCMParameterSpec(TAG_SIZE * 8, iv)
//                cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
//
//                val decryptedData = cipher.doFinal(encrypted)
//                String(decryptedData, StandardCharsets.UTF_8)
//            } catch (e: Exception) {
//                Log.e(TAG, "Error decrypting data", e)
//                null
//            }
//        }
//
//        /**
//         * Encrypt Note object - hanya title dan content yang dienkripsi
//         */
//        fun encryptNote(note: com.example.liveinpeace.data.Note, secretKey: SecretKey): com.example.liveinpeace.data.Note? {
//            return try {
//                val encryptedTitle = encrypt(note.title, secretKey)
//                val encryptedContent = encrypt(note.content, secretKey)
//
//                if (encryptedTitle != null && encryptedContent != null) {
//                    note.copy(
//                        title = encryptedTitle,
//                        content = encryptedContent
//                        // id, date, time, tag tetap tidak dienkripsi
//                    )
//                } else {
//                    Log.e(TAG, "Failed to encrypt note title or content")
//                    null
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error encrypting note", e)
//                null
//            }
//        }
//
//        /**
//         * Decrypt Note object - hanya title dan content yang didekripsi
//         */
//        fun decryptNote(encryptedNote: com.example.liveinpeace.data.Note, secretKey: SecretKey): com.example.liveinpeace.data.Note? {
//            return try {
//                val decryptedTitle = decrypt(encryptedNote.title, secretKey)
//                val decryptedContent = decrypt(encryptedNote.content, secretKey)
//
//                if (decryptedTitle != null && decryptedContent != null) {
//                    encryptedNote.copy(
//                        title = decryptedTitle,
//                        content = decryptedContent
//                    )
//                } else {
//                    Log.e(TAG, "Failed to decrypt note title or content")
//                    null
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error decrypting note", e)
//                null
//            }
//        }
//
//        /**
//         * Decrypt list of notes
//         */
//        fun decryptNotes(encryptedNotes: List<com.example.liveinpeace.data.Note>, secretKey: SecretKey): List<com.example.liveinpeace.data.Note> {
//            return encryptedNotes.mapNotNull { encryptedNote ->
//                decryptNote(encryptedNote, secretKey)
//            }
//        }
//    }
//}