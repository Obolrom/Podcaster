package io.obolonsky.quizzy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.obolonsky.quizzy.data.MultiselectSelectToggleAction
import io.obolonsky.quizzy.data.MultiselectUiElement
import io.obolonsky.quizzy.data.UiAction

@Composable
fun MultiselectComponent(
    uiElement: MultiselectUiElement,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier.selectableGroup()) {
    uiElement.values.forEach { selectElement ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    onAction(MultiselectSelectToggleAction(
                        id = uiElement.id,
                        selectedId = selectElement.id,
                    ))
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = uiElement.selectedIds.contains(selectElement.id),
                onClick = null,
            )
            Text(
                text = selectElement.label,
                fontSize = 22.sp,
            )
        }
    }
}