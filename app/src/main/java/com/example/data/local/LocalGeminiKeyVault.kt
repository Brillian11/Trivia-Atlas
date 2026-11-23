package com.example.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Stores the user's personal Gemini API key strictly on this device.
 * - Uses Android Keystore (AES-256-GCM) so ciphertext is tied to device hardware/TEE.
 * - Persists inside a backup-excluded MODE_PRIVATE SharedPreferences file (never synced to cloud/backups).
 */
class LocalGeminiKeyVault(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        VAULT_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveKey(rawKey: String?) {
        val cleaned = rawKey?.trim().orEmpty()
        if (cleaned.isEmpty()) {
            prefs.edit()
                .remove(PREF_CIPHER_PAYLOAD)
                .remove(PREF_FALLBACK_PAYLOAD)
                .apply()
            return
        }

        val encrypted = encryptWithKeystore(cleaned)
        prefs.edit().apply {
            if (encrypted != null) {
                putString(PREF_CIPHER_PAYLOAD, encrypted)
                remove(PREF_FALLBACK_PAYLOAD)
            } else {
                // Only used if AndroidKeyStore is absent (e.g. host JVM Robolectric environment)
                putString(PREF_FALLBACK_PAYLOAD, obfuscateForJvmTest(cleaned))
                remove(PREF_CIPHER_PAYLOAD)
            }
        }.apply()
    }

    fun getKey(): String? {
        val cipherPayload = prefs.getString(PREF_CIPHER_PAYLOAD, null)
        if (!cipherPayload.isNullOrBlank()) {
            val decrypted = decryptWithKeystore(cipherPayload)
            if (!decrypted.isNullOrBlank()) return decrypted
        }
        val fallbackPayload = prefs.getString(PREF_FALLBACK_PAYLOAD, null)
        if (!fallbackPayload.isNullOrBlank()) {
            return deobfuscateForJvmTest(fallbackPayload)?.takeIf { it.isNotBlank() }
        }
        return null
    }

    fun hasKey(): Boolean = !getKey().isNullOrBlank()

    private fun getOrCreateKeystoreSecretKey(): SecretKey? = runCatching {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEYSTORE_ALIAS, null) as? SecretKey)?.let { return@runCatching it }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        val spec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }.getOrNull()

    private fun encryptWithKeystore(plaintext: String): String? = runCatching {
        val secretKey = getOrCreateKeystoreSecretKey() ?: return@runCatching null
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val cipherBytes = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
        val ivB64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val cipherB64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
        "$ivB64:$cipherB64"
    }.getOrNull()

    private fun decryptWithKeystore(payload: String): String? = runCatching {
        val parts = payload.split(":", limit = 2)
        if (parts.size != 2) return@runCatching null
        val secretKey = getOrCreateKeystoreSecretKey() ?: return@runCatching null
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val cipherBytes = Base64.decode(parts[1], Base64.NO_WRAP)
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        String(cipher.doFinal(cipherBytes), StandardCharsets.UTF_8)
    }.getOrNull()

    private fun obfuscateForJvmTest(plain: String): String {
        val bytes = plain.toByteArray(StandardCharsets.UTF_8)
        val xored = ByteArray(bytes.size) { i ->
            (bytes[i].toInt() xor ((0x5A + i * 7) and 0xFF)).toByte()
        }
        return Base64.encodeToString(xored, Base64.NO_WRAP)
    }

    private fun deobfuscateForJvmTest(encoded: String): String? = runCatching {
        val xored = Base64.decode(encoded, Base64.NO_WRAP)
        val plain = ByteArray(xored.size) { i ->
            (xored[i].toInt() xor ((0x5A + i * 7) and 0xFF)).toByte()
        }
        String(plain, StandardCharsets.UTF_8)
    }.getOrNull()

    companion object {
        const val VAULT_PREFS_NAME = "trivia_atlas_local_vault"
        private const val PREF_CIPHER_PAYLOAD = "gemini_key_gcm"
        private const val PREF_FALLBACK_PAYLOAD = "gemini_key_local"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEYSTORE_ALIAS = "trivia_atlas_gemini_key_v1"
        private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"

        fun redactSecret(input: String?, secret: String?): String {
            val raw = input.orEmpty()
            val cleanedSecret = secret?.trim().orEmpty()
            if (cleanedSecret.isEmpty() || !raw.contains(cleanedSecret)) return raw
            return raw.replace(cleanedSecret, "[REDACTED]")
        }
    }
}
