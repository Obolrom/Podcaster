package io.obolonsky.quizzy.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CheckBoxComponent(
    uiElement: CheckBoxUiElement,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier,
) = Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Checkbox(
        checked = uiElement.isChecked,
        onCheckedChange = { isChecked ->
            onAction(ToggleCheckBoxAction(id = uiElement.id, isChecked = isChecked))
        },
    )
    Text(
        text = uiElement.label,
        style = MaterialTheme.typography.subtitle1,
    )
}