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
    val _id: String, // ID of the expense
    val amount: Double, // Expense amount
    val description: String, // Description of the expense
    val date: String, // Date of the expense
    val user: String // User ID associated with the expense
)
