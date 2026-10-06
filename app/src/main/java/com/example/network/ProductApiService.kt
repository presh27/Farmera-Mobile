package com.example.network

import com.example.model.ApiProductDetailResponse
import com.example.model.ApiProductListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {

    @GET(ApiConfig.PRODUCTS)
    suspend fun getProducts(): Response<ApiProductListResponse>

    @GET(ApiConfig.PRODUCT_DETAIL)
    suspend fun getProductBySlug(
        @Path("slug") slug: String
    ): Response<ApiProductDetailResponse>
}
