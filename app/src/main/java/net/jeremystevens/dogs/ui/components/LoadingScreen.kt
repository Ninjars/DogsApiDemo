package net.jeremystevens.dogs.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import net.jeremystevens.dogs.R
import net.jeremystevens.dogs.ui.theme.DogsTheme

@Composable
fun LoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.breeds_loading_message),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        CircularProgressIndicator()
    }
}

@Preview(apiLevel = 33, showBackground = true)
@Composable
private fun BreedPhotosScreenPreview() {
    DogsTheme {
        LoadingScreen()
    }
}
