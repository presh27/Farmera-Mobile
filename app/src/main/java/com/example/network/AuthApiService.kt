package com.example.network

import com.example.model.AuthResponse
import com.example.model.GoogleAuthRequest
import com.example.model.LogoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @POST(ApiConfig.AUTH_GOOGLE)
    suspend fun googleAuth(
        @Body request: GoogleAuthRequest
    ): Response<AuthResponse>

    @GET(ApiConfig.AUTH_ME)
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<AuthResponse>

    @POST(ApiConfig.AUTH_LOGOUT)
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): Response<LogoutResponse>
}
