package net.jeremystevens.dogs

import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

sealed class Route {
    open val routeId: String by lazy { this.javaClass.simpleName }
    open val navOptions: NavOptionsBuilder.() -> Unit = { }

    data object Back : Route()
    data object BreedsList : Route()

    data class BreedPhotos(val breedId: String) : Route() {
        override val routeId by lazy { "${BreedPhotos::class.java.simpleName}/$breedId" }

        companion object {
            val baseRouteId: String by lazy { "${BreedPhotos::class.java.simpleName}/{$routeBreedId}" }
            const val routeBreedId = "breedId"
        }
    }
}

typealias NavListener = (Route) -> Unit

/**
 * This is a helper class that can be provided through Dagger to allow navigation.
 * The Route classes define all the available destinations and arguments in a clean and parameter-safe manner.
 */
@ActivityRetainedScoped
class NavigationDispatcher @Inject constructor() {
    private var listener: NavListener? = null

    fun navigateTo(route: Route) {
        listener?.invoke(route)
    }

    fun setNavListener(listener: NavListener) {
        this.listener = listener
    }
}