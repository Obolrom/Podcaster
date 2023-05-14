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

@Composable
fun RadioGroupComponent(
    uiElement: RadioGroupUiElement,
    onAction: (UiAction) -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier = modifier.selectableGroup()) {
    uiElement.values.forEach { radioButton ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable {
                    onAction(SelectRadioButtonAction(
                        id = uiElement.id,
                        selectedButtonId = radioButton.id),
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = (radioButton.id == uiElement.selectedId),
                onClick = null,
            )
            Text(
                text = radioButton.label,
                fontSize = 22.sp,
            )
        }
    }
}