package com.example.madproject.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object History : Screen("history")
    data object Analytics : Screen("analytics")
    data object Settings : Screen("settings")
    data object AddEdit : Screen("add_edit?expenseId={expenseId}") {
        fun createRoute(expenseId: Long?): String {
            return if (expenseId != null && expenseId > 0) {
                "add_edit?expenseId=$expenseId"
            } else {
                "add_edit"
            }
        }
    }
}
