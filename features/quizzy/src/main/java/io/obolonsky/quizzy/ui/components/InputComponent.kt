package io.obolonsky.quizzy.ui.components

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.obolonsky.quizzy.data.InputChangedAction
import io.obolonsky.quizzy.data.InputUiElement
import io.obolonsky.quizzy.data.UiAction

@Composable
fun InputComponent(
    uiElement: InputUiElement,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier,
        value = uiElement.value,
        label = @Composable {
            Text(text = uiElement.label)
        },
        onValueChange = {
            onAction(InputChangedAction(uiElement.id, newValue = it))
        },
    )
}