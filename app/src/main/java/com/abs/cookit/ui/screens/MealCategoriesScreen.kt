package com.abs.cookit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.abs.cookit.R
import com.abs.cookit.data.model.Category
import com.abs.cookit.data.model.Meal
import com.abs.cookit.data.repository.MealRepository
import com.abs.cookit.ui.theme.CookItTheme
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

@Composable
fun MealCategoriesScreen(
    modifier: Modifier = Modifier,
    viewModel: MealViewModel = viewModel()
) {
    MealCategoriesContent(
        modifier = modifier,
        categories = viewModel.categories,
        meals = viewModel.meals,
        selectedCategory = viewModel.selectedCategory,
        isLoadingCategories = viewModel.isLoadingCategories,
        isLoadingMeals = viewModel.isLoadingMeals,
        onCategoryClick = { viewModel.onCategorySelected(it) }
    )
}

@Composable
fun MealCategoriesContent(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    meals: List<Meal>,
    selectedCategory: Category?,
    isLoadingCategories: Boolean,
    isLoadingMeals: Boolean,
    onCategoryClick: (Category) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Category List (Horizontal Row)
        if (isLoadingCategories && categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        isSelected = category == selectedCategory,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Meals Grid (Vertical Grid)
        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoadingMeals -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                selectedCategory == null -> {
                    Text(
                        text = stringResource(R.string.select_category_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .align(Alignment.Center)
                    )
                }

                meals.isEmpty() && !isLoadingMeals -> {
                    Text(
                        text = "No meals found for this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(meals) { meal ->
                            MealItem(meal = meal)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Responsive Category List Item
 * Uses existing Category model: name, thumbnail
 */
@Composable
fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = listOf(
        Color(0xFFFFD700),
        Color(0xFFFF8C00),
        Color(0xFFFF1493),
        Color(0xFF8A2BE2)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(gradientColors),
                    shape = CircleShape
                )
                .padding(4.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            AsyncImage(
                model = category.thumbnail,
                contentDescription = category.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            ),
        )
    }
}

/**
 * Responsive Meal List Item
 * Uses existing Meal model: name, thumbnail
 */
@Composable
fun MealItem(
    meal: Meal,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF3F4F9)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp, bottom = 24.dp, start = 12.dp, end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }

        AsyncImage(
            model = meal.thumbnail,
            contentDescription = meal.name,
            modifier = Modifier
                .size(100.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MealCategoriesScreenPreview() {
    CookItTheme {
        MealCategoriesContent(
            categories = listOf(
                Category(
                    "1",
                    "Beef",
                    "https://www.themealdb.com/images/category/beef.png",
                    "Description"
                ),
                Category(
                    "2",
                    "Chicken",
                    "https://www.themealdb.com/images/category/chicken.png",
                    "Description"
                )
            ),
            meals = emptyList(),
            selectedCategory = null,
            isLoadingCategories = false,
            isLoadingMeals = false,
            onCategoryClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
fun MealItemPreview() {
            MealItem(
                meal = Meal(
                    id = "1",
                    name = "Alfajores",
                    thumbnail = "https://www.themealdb.com/images/media/meals/adxc9k1619787919.jpg"
                )
            )


}


