package tn.esprit.smartspend.utils

import android.content.Context
import android.content.SharedPreferences

object TranslationManager {
    private const val PREFERENCE_NAME = "language_preferences"
    private const val LANGUAGE_KEY = "selected_language"

    // Supported languages
    private val translations = mapOf(
        "en" to mapOf(
            "app_name" to "SmartSpend",
            "language" to "Language",
            "change_language" to "Change Language",
            "profile_title" to "Profile",
            "privacy_policy" to "Privacy Policy",
            "logout" to "Logout",
            "email" to "Email",
            "password" to "Password",
            "remember_me" to "Remember Me",
            "forgot_password" to "Forgot Password?",
            "sign_in" to "Sign In",
            "sign_up" to "Don't have an account? Sign up"
        ),
        "fr" to mapOf(
            "app_name" to "SmartSpend",
            "language" to "Langue",
            "change_language" to "Changer la langue",
            "profile_title" to "Profil",
            "privacy_policy" to "Politique de confidentialité",
            "logout" to "Se déconnecter",
            "email" to "Email",
            "password" to "Mot de passe",
            "remember_me" to "Se souvenir de moi",
            "forgot_password" to "Mot de passe oublié ?",
            "sign_in" to "Se connecter",
            "sign_up" to "Vous n'avez pas de compte ? Inscrivez-vous"
        )


    )

    // Default language
    private var currentLanguage = "en"

    fun getTranslation(key: String): String {
        return translations[currentLanguage]?.get(key) ?: key
    }

    fun setLanguage(context: Context, language: String) {
        currentLanguage = language
        saveLanguagePreference(context, language)
    }

    fun loadLanguagePreference(context: Context) {
        val prefs = getSharedPreferences(context)
        currentLanguage = prefs.getString(LANGUAGE_KEY, "en") ?: "en"
    }

    private fun saveLanguagePreference(context: Context, language: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(LANGUAGE_KEY, language).apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}
