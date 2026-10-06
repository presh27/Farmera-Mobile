package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.UserProfile

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "farmera_auth_session_store"
        private const val KEY_SESSION_TOKEN = "key_backend_session_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_AVATAR = "key_user_avatar"
    }

    fun saveSession(token: String, user: UserProfile?) {
        prefs.edit().apply {
            putString(KEY_SESSION_TOKEN, token)
            if (user != null) {
                putString(KEY_USER_ID, user.id)
                putString(KEY_USER_EMAIL, user.email)
                putString(KEY_USER_NAME, user.name)
                putString(KEY_USER_AVATAR, user.profileImage)
            }
            apply()
        }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_SESSION_TOKEN, null)?.takeIf { it.isNotBlank() }
    }

    fun getUser(): UserProfile? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val name = prefs.getString(KEY_USER_NAME, null)
        val avatar = prefs.getString(KEY_USER_AVATAR, null)
        return UserProfile(
            id = id,
            email = email,
            name = name,
            avatarUrl = avatar
        )
    }

    fun hasSession(): Boolean {
        return getToken() != null
    }

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_SESSION_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_NAME)
            remove(KEY_USER_AVATAR)
            apply()
        }
    }
}
