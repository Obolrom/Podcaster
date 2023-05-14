package io.obolonsky.quizzy.ui.components

sealed interface UiAction {

    val id: String
}

data class ToggleCheckBoxAction(
    override val id: String,
    val isChecked: Boolean,
) : UiAction

data class InputChangedAction(
    override val id: String,
    val newValue: String,
) : UiAction

data class SelectRadioButtonAction(
    override val id: String,
    val selectedButtonId: String,
) : UiAction