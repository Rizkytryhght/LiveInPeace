package com.example.liveinpeace.ui.navigation

import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.liveinpeace.R
import com.example.liveinpeace.ui.dashboard.HomeScreen
import com.example.liveinpeace.ui.features.FeatureListScreen
import com.example.liveinpeace.ui.note.NoteScreen
import com.example.liveinpeace.ui.profile.ProfileScreen

object NavDestinations {
    const val HOME = "home"
    const val NOTES = "notes"
    const val FEATURES = "features"
    const val PROFILE = "profile"
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestinations.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestinations.HOME) { HomeScreen(navController) }
            composable(NavDestinations.NOTES) { NoteScreen(navController) }
            composable(NavDestinations.FEATURES) { FeatureListScreen(navController) }
            composable(NavDestinations.PROFILE) { ProfileScreen(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavItem("Home", NavDestinations.HOME, Icons.Default.Home),
        NavItem("Catatan", NavDestinations.NOTES, Icons.Default.Note),
        NavItem("Ruang Tenang", NavDestinations.FEATURES, Icons.Default.Star),
        NavItem("Profil", NavDestinations.PROFILE, Icons.Default.Person)
    )
    BottomNavigation(
        backgroundColor = colorResource(R.color.white),
        contentColor = colorResource(R.color.nav_item_state)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                selectedContentColor = colorResource(R.color.primary_green),
                unselectedContentColor = Color.Gray,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class NavItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)