package tn.esprit.smartspend.model

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignInResponse(
    val access_token: String?
)


data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)

data class SignUpResponse(
    val name: String,
    val email: String,
    val password: String, // Hashed password
    val _id: String,
    val preferences: List<String>,
    val __v: Int
)
data class ForgotPasswordRequest(
    val email: String
)
data class ForgotPasswordResponse(
    val message: String
)
data class ResetPasswordRequest(
    val newPassword: String // Only new password in the body
)

data class ResetPasswordResponse(
    val message: String
)
data class Expense(
    val amount: Double,
    val description: String,
    val date: String,
    val category: String
)

data class Category(
    val _id: String,
    val name: String,
    val type: String
)
data class Recommendation(
    val recommendationText: String
)

