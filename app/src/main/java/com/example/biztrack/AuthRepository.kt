package com.example.biztrack

import android.content.Context
import androidx.room.withTransaction
import com.example.biztrack.data.local.BizTrackDatabase
import com.example.biztrack.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

class AuthRepository(context: Context) {

    private val database = BizTrackDatabase.getDatabase(context)
    private val userDao = database.userDao()
    private val sessionManager = SessionManager(context)

    suspend fun register(
        fullName: String,
        businessName: String,
        email: String,
        password: String
    ): String? = withContext(Dispatchers.IO) {

        val cleanName = fullName.trim()
        val cleanBusiness = businessName.trim()
        val cleanEmail = email.trim().lowercase(Locale.ROOT)

        if (
            cleanName.isBlank() ||
            cleanBusiness.isBlank() ||
            cleanEmail.isBlank() ||
            password.length < 8
        ) {
            return@withContext null
        }

        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)

        val user = UserEntity(
            id = UUID.randomUUID().toString(),
            fullName = cleanName,
            businessName = cleanBusiness,
            email = cleanEmail,
            passwordHash = hash,
            passwordSalt = salt
        )

        try {
            database.withTransaction {

                val isFirstUser = userDao.getUserCount() == 0

                userDao.insertUser(user)

                if (isFirstUser) {
                    database.transactionDao()
                        .claimLegacyTransactions(user.id)

                    database.productDao()
                        .claimLegacyProducts(user.id)

                    database.budgetDao()
                        .claimLegacyBudgets(user.id)
                }
            }

            sessionManager.saveUserSession(user.id)
            user.id

        } catch (e: Exception) {
            null
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Boolean = withContext(Dispatchers.IO) {

        val cleanEmail = email.trim().lowercase(Locale.ROOT)

        val user = userDao.getUserByEmail(cleanEmail)
            ?: return@withContext false

        val validPassword = PasswordHasher.verifyPassword(
            password,
            user.passwordSalt,
            user.passwordHash
        )

        if (validPassword) {
            sessionManager.saveUserSession(user.id)
        }

        validPassword
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun getCurrentUserId(): String? {
        return sessionManager.getCurrentUserId()
    }

    suspend fun getCurrentUser(): UserEntity? {
        val userId = getCurrentUserId() ?: return null
        return userDao.getUserById(userId)
    }
}