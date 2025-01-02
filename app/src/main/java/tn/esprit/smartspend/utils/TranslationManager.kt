package tn.esprit.smartspend.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

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
            "sign_up" to "Don't have an account? Sign up",
            "dark_mode" to "Enable Dark Mode",
            "notifications" to "Enable Notifications",
            "linked_accounts" to "Manage Linked Accounts",
            "security" to "Security & Privacy",
            "help_support" to "Help & Support",
            "app_version" to "App ver",
            "close" to "Close",
            "settings" to "Settings",
            "balance" to "Balance",
            "expenses" to "Expenses",
            "income" to "Income",
            "spent" to "Spent",
            "exceeding_income" to "Exceeding Income!",
            "spending_overview" to "Spending Overview",
            "recent_expenses" to "Recent Expenses",
            "recent_incomes" to "Recent Incomes",
            "view_all" to "View All",
            "unknown" to "Unknown",
            "add_transaction" to "Add Transaction",
            "expense" to "Expense",
            "income" to "Income",
            "amount" to "Amount",
            "description" to "Description",
            "category" to "Category",
            "save" to "Save",
            "take_photo" to "Take Photo",
            "upload_photo" to "Upload Photo",
            "uploading" to "Uploading",
            "upload_wait" to "Please wait while the upload completes.",
            "camera_permission_denied" to "Camera permission denied",
            "storage_permission_denied" to "Storage permission denied",
            "upload_success" to "Photo uploaded successfully",
            "save_failed_expense" to "Failed to save expense",
            "save_failed_income" to "Failed to save income",
            "all_expenses" to "All Expenses",
            "loading" to "Loading...",
            "income_vs_expenses" to "Income vs Expenses (Last Week)",
            "expenses_by_category" to "Expenses by Category",
            "incomes_by_category" to "Incomes by Category",
            "no_expenses" to "No expenses found.",
            "no_incomes" to "No incomes found.",
            "pie_chart" to "Pie Chart",
            "bar_chart" to "Bar Chart",
            "user_token_not_found" to "User token not found!",
            "failed_to_fetch" to "Failed to fetch data.",
            "exception" to "Exception",
            "forgot_password_title" to "Forgot Password",
            "forgot_password_desc" to "Enter your email address below to reset your password.",
            "email_address" to "Email Address",
            "send_reset_code" to "Send Reset Code",
            "back_to_login" to "Back to Login",
            "invalid_email" to "Please enter a valid email address",
            "reset_password_title" to "Reset Password",
            "new_password" to "New Password",
            "submit" to "Submit",
            "cancel" to "Cancel",
            "enter_token" to "Enter the Token",
            "token" to "Token",
            "reset_success" to "Password reset successful!",
            "reset_email_sent" to "Password reset email sent!",
            "all_incomes" to "All Incomes",
            "login_title" to "Sign In",
            "login_email_hint" to "Email",
            "login_password_hint" to "Password",
            "login_button" to "Sign In",
            "login_remember_me" to "Remember Me",
            "login_forgot_password" to "Forgot password?",
            "login_no_account" to "Don't have an account? Sign up",
            "signup_title" to "Sign Up",
            "signup_name_hint" to "Name",
            "signup_email_hint" to "Email",
            "signup_password_hint" to "Password",
            "signup_confirm_password_hint" to "Confirm Password",
            "signup_button" to "Sign Up",
            "signup_have_account" to "Already have an account? Sign In",
            "signup_passwords_not_match" to "Passwords do not match!",
            "signup_success" to "Account created successfully!",
            "signup_failed" to "Sign-up failed!"
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
            "sign_up" to "Vous n'avez pas de compte ? Inscrivez-vous",
            "dark_mode" to "Activer le mode sombre",
            "notifications" to "Activer les notifications",
            "linked_accounts" to "Gérer les comptes liés",
            "security" to "Sécurité et confidentialité",
            "help_support" to "Aide et support",
            "app_version" to "Version",
            "close" to "Fermer",
            "settings" to "Paramètres",
            "balance" to "Solde",
            "expenses" to "Dépenses",
            "income" to "Revenus",
            "spent" to "Dépensé",
            "exceeding_income" to "Dépassement du revenu !",
            "spending_overview" to "Aperçu des dépenses",
            "recent_expenses" to "Dépenses récentes",
            "recent_incomes" to "Revenus récents",
            "view_all" to "Voir tout",
            "unknown" to "Inconnu",
            "add_transaction" to "Ajouter une transaction",
            "expense" to "Dépense",
            "income" to "Revenu",
            "amount" to "Montant",
            "description" to "Description",
            "category" to "Catégorie",
            "save" to "Enregistrer",
            "take_photo" to "Prendre une photo",
            "upload_photo" to "Télécharger une photo",
            "uploading" to "Téléchargement",
            "upload_wait" to "Veuillez patienter pendant le téléchargement.",
            "camera_permission_denied" to "Permission de caméra refusée",
            "storage_permission_denied" to "Permission de stockage refusée",
            "upload_success" to "Photo téléchargée avec succès",
            "save_failed_expense" to "Échec de l'enregistrement de la dépense",
            "save_failed_income" to "Échec de l'enregistrement du revenu",
            "all_expenses" to "Toutes les dépenses",
            "loading" to "Chargement...",
            "income_vs_expenses" to "Revenus vs Dépenses (Dernière semaine)",
            "expenses_by_category" to "Dépenses par catégorie",
            "incomes_by_category" to "Revenus par catégorie",
            "no_expenses" to "Aucune dépense trouvée.",
            "no_incomes" to "Aucun revenu trouvé.",
            "pie_chart" to "Graphique circulaire",
            "bar_chart" to "Graphique à barres",
            "user_token_not_found" to "Jeton d'utilisateur non trouvé !",
            "failed_to_fetch" to "Échec de la récupération des données.",
            "exception" to "Exception",
            "forgot_password_title" to "Mot de passe oublié",
            "forgot_password_desc" to "Entrez votre adresse e-mail ci-dessous pour réinitialiser votre mot de passe.",
            "email_address" to "Adresse e-mail",
            "send_reset_code" to "Envoyer le code",
            "back_to_login" to "Retour à la connexion",
            "invalid_email" to "Veuillez entrer une adresse e-mail valide",
            "reset_password_title" to "Réinitialiser le mot de passe",
            "new_password" to "Nouveau mot de passe",
            "submit" to "Envoyer",
            "cancel" to "Annuler",
            "enter_token" to "Entrez le jeton",
            "token" to "Jeton",
            "reset_success" to "Réinitialisation du mot de passe réussie !",
            "reset_email_sent" to "E-mail de réinitialisation envoyé !",
            "all_incomes" to "Tous les revenus",
            "login_title" to "Connexion",
            "login_email_hint" to "Email",
            "login_password_hint" to "Mot de passe",
            "login_button" to "Se connecter",
            "login_remember_me" to "Se souvenir de moi",
            "login_forgot_password" to "Mot de passe oublié ?",
            "login_no_account" to "Vous n'avez pas de compte ? Inscrivez-vous",
            "signup_title" to "Inscription",
            "signup_name_hint" to "Nom",
            "signup_email_hint" to "Email",
            "signup_password_hint" to "Mot de passe",
            "signup_confirm_password_hint" to "Confirmer le mot de passe",
            "signup_button" to "S'inscrire",
            "signup_have_account" to "Vous avez déjà un compte ? Connectez-vous",
            "signup_passwords_not_match" to "Les mots de passe ne correspondent pas !",
            "signup_success" to "Compte créé avec succès !",
            "signup_failed" to "Échec de l'inscription !"
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
        currentLanguage = prefs.getString(LANGUAGE_KEY, getDeviceLanguage()) ?: "en"
    }

    private fun getDeviceLanguage(): String {
        val deviceLanguage = Locale.getDefault().language
        return if (translations.containsKey(deviceLanguage)) deviceLanguage else "en"
    }

    private fun saveLanguagePreference(context: Context, language: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(LANGUAGE_KEY, language).apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}
