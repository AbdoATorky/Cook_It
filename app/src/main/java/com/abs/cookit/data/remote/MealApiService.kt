package com.abs.cookit.data.remote

import com.abs.cookit.data.model.CategoryResponse
import com.abs.cookit.data.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealResponse

}