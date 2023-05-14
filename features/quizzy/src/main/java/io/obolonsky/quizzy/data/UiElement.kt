package io.obolonsky.quizzy.data

import io.obolonsky.quizzy.redux.UiElementTypes

sealed interface UiElement {

    val type: UiElementTypes

    val id: String

    val label: String

    val weight: Float?
}

interface Requireable {

    val id: String

    val required: Boolean?
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
    override val required: Boolean? = null,
    val isChecked: Boolean,
) : UiElement, Requireable

data class InputUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    override val required: Boolean? = null,
    val value: String,
) : UiElement, Requireable

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
    override val required: Boolean? = null,
    val values: List<RadioButtonUiElement>,
    val selectedId: String,
) : UiElement, Requireable {

    data class RadioButtonUiElement(
        val id: String,
        val label: String,
    )
}