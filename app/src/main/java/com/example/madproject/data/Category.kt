package com.example.madproject.data

import androidx.compose.ui.graphics.Color
import com.example.madproject.ui.theme.CategoryBills
import com.example.madproject.ui.theme.CategoryFood
import com.example.madproject.ui.theme.CategoryHealth
import com.example.madproject.ui.theme.CategoryShopping
import com.example.madproject.ui.theme.CategoryTravel

enum class Category(val label: String, val color: Color) {
    Food("Food", CategoryFood),
    Travel("Travel", CategoryTravel),
    Bills("Bills", CategoryBills),
    Shopping("Shopping", CategoryShopping),
    Health("Health", CategoryHealth)
}

// completed