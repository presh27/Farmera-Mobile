package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleAuthRequest(
    @Json(name = "idToken") val idToken: String,
    @Json(name = "credential") val credential: String? = null
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String,
    @Json(name = "name") val name: String? = null,
    @Json(name = "avatarUrl") val avatarUrl: String? = null,
    @Json(name = "picture") val picture: String? = null
) {
    val displayName: String
        get() = name?.takeIf { it.isNotBlank() } ?: email.substringBefore("@")

    val profileImage: String?
        get() = avatarUrl ?: picture
}

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "ok") val ok: Boolean? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "sessionToken") val sessionToken: String? = null,
    @Json(name = "user") val user: UserProfile? = null,
    @Json(name = "error") val error: String? = null,
    @Json(name = "message") val message: String? = null
) {
    val resolvedToken: String?
        get() = token ?: sessionToken
}

@JsonClass(generateAdapter = true)
data class LogoutResponse(
    @Json(name = "ok") val ok: Boolean? = null,
    @Json(name = "message") val message: String? = null
)
