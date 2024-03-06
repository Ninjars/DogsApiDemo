package net.jeremystevens.dogs.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays an error message in a lil popup at the bottom of the screen,
 * which will last until a null message is supplied.
 */
@Composable
fun BoxScope.ErrorPopup(errorMessage: String?) {
    AnimatedVisibility(
        visible = errorMessage != null,
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        ErrorContent(errorMessage)
    }
}

@Composable
private fun ErrorContent(errorMessage: String?) {
    // Track last non-null error message so it can continue displaying
    // that message as the view animates out after error is cleared.
    var currentError by remember { mutableStateOf(errorMessage) }
    if (errorMessage != null) {
        currentError = errorMessage
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
            Text(
                text = currentError ?: "",
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
