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
