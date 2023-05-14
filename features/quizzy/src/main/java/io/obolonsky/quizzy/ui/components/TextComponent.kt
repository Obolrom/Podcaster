package io.obolonsky.quizzy.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.obolonsky.quizzy.data.TextLabelUiElement

@Composable
fun TextComponent(
    uiElement: TextLabelUiElement,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = uiElement.label,
        style = MaterialTheme.typography.subtitle1,
    )
}