package com.example.madproject.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.madproject.di.AppContainer
import com.example.madproject.ui.screens.AddEditExpenseScreen
import com.example.madproject.ui.screens.AnalyticsScreen
import com.example.madproject.ui.screens.HistoryScreen
import com.example.madproject.ui.screens.HomeScreen
import com.example.madproject.ui.screens.SettingsScreen
import com.example.madproject.ui.screens.SplashScreen
import com.example.madproject.ui.viewmodel.AppViewModelFactory

@Composable
fun AppNavHost(container: AppContainer) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showFab = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Analytics.route,
        Screen.Settings.route
    )

    val factory = remember { AppViewModelFactory(container) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(onClick = {
                    navController.navigate(Screen.AddEdit.createRoute(null))
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(factory = factory)
            }
            composable(Screen.History.route) {
                HistoryScreen(factory = factory, onEdit = { id ->
                    navController.navigate(Screen.AddEdit.createRoute(id))
                })
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen(factory = factory)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(factory = factory, snackbarHostState = snackbarHostState)
            }
            composable(
                route = Screen.AddEdit.route,
                arguments = listOf(navArgument("expenseId") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: -1L
                AddEditExpenseScreen(
                    factory = factory,
                    expenseId = expenseId,
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Analytics.route,
        Screen.Settings.route
    )

    AnimatedVisibility(visible = showBar) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomBarItem(Screen.Home, Icons.Filled.Home, navController)
                BottomBarItem(Screen.History, Icons.Filled.History, navController)
                BottomBarItem(Screen.Analytics, Icons.Filled.Analytics, navController)
                BottomBarItem(Screen.Settings, Icons.Filled.Settings, navController)
            }
        }
    }
}

@Composable
private fun BottomBarItem(screen: Screen, icon: androidx.compose.ui.graphics.vector.ImageVector, navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selected = currentRoute == screen.route

    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = Modifier
            .clickable {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = screen.route.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = color
        )
    }
}
