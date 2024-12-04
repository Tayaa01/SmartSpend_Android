package tn.esprit.smartspend.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("SmartSpendPrefs", Context.MODE_PRIVATE)

    // Save token to SharedPreferences
    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", token)
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
        editor.apply()
    }
}
