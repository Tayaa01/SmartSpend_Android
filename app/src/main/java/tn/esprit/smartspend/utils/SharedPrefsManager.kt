package tn.esprit.smartspend.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("SmartSpendPrefs", Context.MODE_PRIVATE)

    // Save token to SharedPreferences
    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", token)
        editor.putLong("TOKEN_EXPIRATION_TIME", System.currentTimeMillis() + 12 * 60 * 60 * 1000) // 12 hours in milliseconds
        editor.apply()
    }

    // Get token from SharedPreferences
    fun getToken(): String? {
        return sharedPreferences.getString("ACCESS_TOKEN", null)
    }

    // Clear token from SharedPreferences (for example, on logout)
    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("ACCESS_TOKEN")
        editor.remove("TOKEN_EXPIRATION_TIME")
        editor.apply()
    }
    fun setLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    fun getLanguage(): String? {
        return sharedPreferences.getString("language", null)
    }


    // Save Remember Me preference
    fun saveRememberMe(rememberMe: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("REMEMBER_ME", rememberMe)
        editor.apply()
    }

    // Get Remember Me preference
    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean("REMEMBER_ME", false)
    }

    // Check if the token is expired
    fun isTokenExpired(): Boolean {
        val expirationTime = sharedPreferences.getLong("TOKEN_EXPIRATION_TIME", 0)
        return System.currentTimeMillis() > expirationTime
    }
}