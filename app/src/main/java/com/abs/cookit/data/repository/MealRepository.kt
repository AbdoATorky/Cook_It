package com.abs.cookit.data.repository

import com.abs.cookit.data.model.Category
import com.abs.cookit.data.model.Meal
import com.abs.cookit.data.remote.RetrofitClient

class MealRepository {
    private val api = RetrofitClient.api

    suspend fun getCategories(): List<Category> {
        return api.getCategories().categories
    }

    suspend fun getMealsByCategory(category: String): List<Meal> {
        return api.getMealsByCategory(category).meals ?: emptyList()
    }

}