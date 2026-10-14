package com.app.shoply.data.remote.api

import com.app.shoply.data.remote.model.ProductSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductSearchService {

    @GET("search")
    suspend fun searchProducts(
        @Query("query") query: String,
        @Query("page") page: Int
    ): ProductSearchResponse
}