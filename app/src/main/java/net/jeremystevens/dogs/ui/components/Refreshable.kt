package net.jeremystevens.dogs.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

/**
 * The current PullToRefresh library offered by Material3 doesn't provide a callback
 * for when the refresh is started and doesn't offer a good way to hoist the internal
 * refresh indicator state to business logic.
 *
 * By maintaining our own "isRefreshed" state flag and propagating an event when the
 * PullToRefreshState changes we can work around this limitation without looking for
 * third party dependencies.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Refreshable(
    isRefreshing: Boolean,
    onRefreshTriggered: () -> Unit,
    content: @Composable () -> Unit,
) {

    // synchronise PullToRefresh UI with state
    val refreshState = rememberPullToRefreshState()
    if (!isRefreshing) {
        refreshState.endRefresh()
    } else if (!refreshState.isRefreshing) {
        refreshState.startRefresh()
    }

    // detect UI-initiated refresh
    if (refreshState.isRefreshing && !isRefreshing) {
        onRefreshTriggered()
    }

    Box(
        Modifier.nestedScroll(refreshState.nestedScrollConnection)
    ) {
        content()
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = refreshState,
        )
    }
}