package net.jeremystevens.dogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import net.jeremystevens.dogs.features.breedslist.BreedsListScreen
import net.jeremystevens.dogs.features.dogpics.DogPicsScreen
import net.jeremystevens.dogs.ui.theme.DogsTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationDispatcher: NavigationDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            navigationDispatcher.setNavListener { route ->
                when (route) {
                    is Route.Back -> navController.popBackStack()
                    else -> navController.navigate(route.routeId, route.navOptions)
                }
            }
            DogsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Route.BreedsList.routeId
                    ) {
                        composable(Route.BreedsList.routeId) {
                            BreedsListScreen(hiltViewModel())
                        }
                        composable(
                            Route.BreedPhotos.baseRouteId,
                            arguments = listOf(
                                navArgument(Route.BreedPhotos.routeBreedId) {
                                    type = NavType.StringType
                                },
                            )
                        ) {
                            DogPicsScreen(hiltViewModel())
                        }
                    }
                }
            }
        }
    }
}
