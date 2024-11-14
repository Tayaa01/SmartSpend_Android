package tn.esprit.smartspend.model

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignInResponse(
    val token: String,
    val userId: String
)

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)

data class SignUpResponse(
    val message: String,
    val userId: String
)
