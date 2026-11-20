package com.example.biztrack

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object PasswordHasher {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 32

    private val random = SecureRandom()

    fun generateSalt(): String {
        val salt = ByteArray(16)
        random.nextBytes(salt)

        return Base64.encodeToString(
            salt,
            Base64.NO_WRAP
        )
    }

    fun hashPassword(password: String, salt: String): String {
        val saltBytes = Base64.decode(salt, Base64.NO_WRAP)

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(
            SecretKeySpec(
                password.toByteArray(StandardCharsets.UTF_8),
                "HmacSHA256"
            )
        )

        val block = saltBytes + ByteBuffer.allocate(4).putInt(1).array()

        var u = mac.doFinal(block)
        val result = u.copyOf()

        repeat(ITERATIONS - 1) {
            u = mac.doFinal(u)

            for (i in result.indices) {
                result[i] = (result[i].toInt() xor u[i].toInt()).toByte()
            }
        }

        return Base64.encodeToString(
            result.copyOf(KEY_LENGTH),
            Base64.NO_WRAP
        )
    }

    fun verifyPassword(
        password: String,
        salt: String,
        expectedHash: String
    ): Boolean {
        val actualHash = hashPassword(password, salt)

        return MessageDigest.isEqual(
            Base64.decode(actualHash, Base64.NO_WRAP),
            Base64.decode(expectedHash, Base64.NO_WRAP)
        )
    }
}