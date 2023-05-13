package io.obolonsky.quizzy.ui.components

import io.obolonsky.quizzy.redux.UiElementTypes

sealed interface UiElement {

    val type: UiElementTypes

    val id: String

    val label: String
}

data class TextLabelUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
) : UiElement

data class CheckBoxUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    val isChecked: Boolean,
) : UiElement

data class InputUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    val value: String,
) : UiElement

data class RowUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    val subcomponents: List<UiElement>,
) : UiElement