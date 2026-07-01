package com.shreyash.antitheft.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PinManager(context: Context) {

    private val prefs: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isPinSet(): Boolean = prefs.contains(PIN_HASH_KEY)

    fun setPin(pin: String) {
        val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putString(PIN_HASH_KEY, hash)
            .putString(PIN_SALT_KEY, salt.joinToString("") { "%02x".format(it) })
            .putInt(FAILED_ATTEMPTS_KEY, 0)
            .putLong(LOCKOUT_UNTIL_KEY, 0)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        if (isLockedOut()) return false
        val storedHash = prefs.getString(PIN_HASH_KEY, null) ?: return false
        val saltHex = prefs.getString(PIN_SALT_KEY, null) ?: return false
        val salt = saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val valid = hashPin(pin, salt) == storedHash
        if (valid) {
            prefs.edit().putInt(FAILED_ATTEMPTS_KEY, 0).apply()
        } else {
            val attempts = getFailedAttempts() + 1
            prefs.edit().putInt(FAILED_ATTEMPTS_KEY, attempts).apply()
            if (attempts >= MAX_ATTEMPTS) {
                val lockoutUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MILLIS
                prefs.edit().putLong(LOCKOUT_UNTIL_KEY, lockoutUntil).apply()
            }
        }
        return valid
    }

    fun getFailedAttempts(): Int = prefs.getInt(FAILED_ATTEMPTS_KEY, 0)

    fun getRemainingAttempts(): Int = (MAX_ATTEMPTS - getFailedAttempts()).coerceAtLeast(0)

    fun isLockedOut(): Boolean {
        val lockoutUntil = prefs.getLong(LOCKOUT_UNTIL_KEY, 0)
        if (lockoutUntil == 0L) return false
        if (System.currentTimeMillis() >= lockoutUntil) {
            prefs.edit()
                .putInt(FAILED_ATTEMPTS_KEY, 0)
                .putLong(LOCKOUT_UNTIL_KEY, 0)
                .apply()
            return false
        }
        return true
    }

    fun getRemainingLockoutMillis(): Long {
        val lockoutUntil = prefs.getLong(LOCKOUT_UNTIL_KEY, 0)
        return (lockoutUntil - System.currentTimeMillis()).coerceAtLeast(0)
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (!verifyPin(oldPin)) return false
        setPin(newPin)
        return true
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return hash.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_NAME = "pin_prefs"
        private const val PIN_HASH_KEY = "pin_hash"
        private const val PIN_SALT_KEY = "pin_salt"
        private const val FAILED_ATTEMPTS_KEY = "failed_attempts"
        private const val LOCKOUT_UNTIL_KEY = "lockout_until"
        private const val PBKDF2_ITERATIONS = 100_000
        const val MAX_ATTEMPTS = 5
        const val LOCKOUT_DURATION_MILLIS = 30_000L
    }
}
