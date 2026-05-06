package com.abs.cookit.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abs.cookit.data.model.Category
import com.abs.cookit.data.model.Meal
import com.abs.cookit.data.repository.MealRepository
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {
    private val repository = MealRepository()

    var categories by mutableStateOf<List<Category>>(emptyList())
    var meals by mutableStateOf<List<Meal>>(emptyList())
    var selectedCategory by mutableStateOf<Category?>(null)
    var isLoadingCategories by mutableStateOf(false)
    var isLoadingMeals by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            isLoadingCategories = true
            try {
                categories = repository.getCategories()
            } catch (e: Exception) {
                // Handle error
                errorMessage = "Category loading failed : ${e.message}"
            } finally {
                isLoadingCategories = false
            }
        }
    }

    fun onCategorySelected(category: Category) {
        if (selectedCategory == category) {
            selectedCategory = null
            meals = emptyList()
        } else {
            selectedCategory = category
            fetchMeals(category.name)
        }
    }

    private fun fetchMeals(categoryName: String) {
        viewModelScope.launch {
            isLoadingMeals = true
            try {
                meals = repository.getMealsByCategory(categoryName)
            } catch (e: Exception) {
                // Handle error
                errorMessage = "Food loading failed: ${e.message}"
            } finally {
                isLoadingMeals = false
            }
        }
    }
}