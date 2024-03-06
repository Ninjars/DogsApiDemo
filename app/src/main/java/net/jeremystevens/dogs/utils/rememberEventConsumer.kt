package net.jeremystevens.dogs.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.util.function.Consumer

@Composable
fun <T> rememberEventConsumer(consumer: Consumer<T>) =
    remember<(T) -> Unit>(consumer) { { consumer.accept(it) } }