package com.example.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // HTTPS only OkHttpClient without sensitive token logging
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .method(original.method, original.body)
            chain.proceed(requestBuilder.build())
        }
        .build()

    // Dedicated OkHttpClient for persistent Server-Sent Events (no read timeout)
    val sseHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // infinite read timeout for SSE stream
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(if (ApiConfig.BASE_URL.endsWith("/")) ApiConfig.BASE_URL else "${ApiConfig.BASE_URL}/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val authService: AuthApiService = retrofit.create(AuthApiService::class.java)
    val productService: ProductApiService = retrofit.create(ProductApiService::class.java)
    val cartService: CartApiService = retrofit.create(CartApiService::class.java)
}
