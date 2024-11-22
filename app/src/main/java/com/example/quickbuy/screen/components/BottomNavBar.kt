import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.quickbuy.ui.theme.Manatee
import com.example.quickbuy.ui.theme.blue
import com.example.quickbuy.ui.theme.blue50
import com.example.quickbuy.utils.BottomNavBarConstants

@Composable
fun BottomNavBar(navController: NavController) {
    Column {
        Divider(
            color = if (isSystemInDarkTheme()) blue50 else Color.Black,
            thickness = 2.dp
        )
        NavigationBar(
            modifier = Modifier.height(96.dp),
            containerColor = Manatee  // Make bottom bar background transparent
        ) {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            BottomNavBarConstants.BottomNavItem.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { startRoute ->
                                popUpTo(startRoute) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(imageVector = item.icon, contentDescription = item.label)
                    },
                    label = { Text(text = item.label) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = if (currentRoute == item.route) blue.copy(0.8f) else Color.Transparent,
                        selectedIconColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        selectedTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        unselectedIconColor = if (isSystemInDarkTheme()) Color.Black else Color.White,
                        unselectedTextColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                    )
                )
            }
        }
    }
}
