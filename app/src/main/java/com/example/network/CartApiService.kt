package com.example.network

import com.example.model.AddCartItemRequest
import com.example.model.ApiCartResponse
import com.example.model.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CartApiService {

    @GET(ApiConfig.CART)
    suspend fun getCart(
        @Header("Authorization") authorization: String
    ): Response<ApiCartResponse>

    @POST(ApiConfig.CART_ITEMS)
    suspend fun addItem(
        @Header("Authorization") authorization: String,
        @Body request: AddCartItemRequest
    ): Response<ApiCartResponse>

    @PATCH(ApiConfig.CART_ITEM_DETAIL)
    suspend fun updateItem(
        @Header("Authorization") authorization: String,
        @Path("itemId") itemId: String,
        @Body request: UpdateCartItemRequest
    ): Response<ApiCartResponse>

    @DELETE(ApiConfig.CART_ITEM_DETAIL)
    suspend fun removeItem(
        @Header("Authorization") authorization: String,
        @Path("itemId") itemId: String
    ): Response<ApiCartResponse>

    @DELETE(ApiConfig.CART)
    suspend fun clearCart(
        @Header("Authorization") authorization: String
    ): Response<ApiCartResponse>
}
