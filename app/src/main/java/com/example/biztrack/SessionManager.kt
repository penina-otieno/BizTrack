package com.example.biztrack

import android.content.Context

class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "biztrack_session",
        Context.MODE_PRIVATE
    )

    fun saveUserSession(userId: String) {
        preferences.edit()
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    fun getCurrentUserId(): String? {
        return preferences.getString(KEY_USER_ID, null)
    }

    fun isLoggedIn(): Boolean {
        return getCurrentUserId() != null
    }

    fun clearSession() {
        preferences.edit()
            .remove(KEY_USER_ID)
            .apply()
    }

    companion object {
        private const val KEY_USER_ID = "current_user_id"
    }
}