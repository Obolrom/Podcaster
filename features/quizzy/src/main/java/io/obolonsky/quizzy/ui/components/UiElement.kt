package io.obolonsky.quizzy.ui.components

import io.obolonsky.quizzy.redux.UiElementTypes

sealed interface UiElement {

    val type: UiElementTypes

    val id: String

    val label: String

    val weight: Float?
}

data class TextLabelUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
) : UiElement

data class CheckBoxUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    val isChecked: Boolean,
) : UiElement

data class InputUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    val value: String,
) : UiElement

data class RowUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    val subcomponents: List<UiElement>,
) : UiElement

data class RadioGroupUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    val values: List<RadioButtonUiElement>,
    val selectedId: String,
) : UiElement {

    data class RadioButtonUiElement(
        val id: String,
        val label: String,
    )
}