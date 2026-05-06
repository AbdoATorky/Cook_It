package com.abs.cookit.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abs.cookit.R
import com.abs.cookit.data.model.Category
import com.abs.cookit.data.model.Meal
import com.abs.cookit.ui.MealViewModel


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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
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


@Preview(showBackground = true)
@Composable
fun MealCategoriesScreenPreview() {
    MealCategoriesContent(
        categories = listOf(
            Category(
                "1",
                "Beef",
                "",
                "Description"
            ),
            Category(
                "2",
                "Chicken",
                "",
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




