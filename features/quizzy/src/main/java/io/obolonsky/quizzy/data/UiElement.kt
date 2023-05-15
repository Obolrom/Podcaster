package io.obolonsky.quizzy.data

import io.obolonsky.quizzy.redux.UiElementTypes

sealed interface UiElement {

    val type: UiElementTypes

    val id: String

    val label: String

    val weight: Float?

    val paddings: Paddings?
}

interface Requireable {

    val id: String

    val required: Boolean?
}

data class Paddings(
    val start: Int,
    val top: Int,
    val end: Int,
    val bottom: Int,
)

data class TextLabelUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    override val paddings: Paddings?,
) : UiElement

data class CheckBoxUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    override val required: Boolean? = null,
    override val paddings: Paddings?,
    val isChecked: Boolean,
) : UiElement, Requireable

data class InputUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    override val required: Boolean? = null,
    override val paddings: Paddings?,
    val value: String,
) : UiElement, Requireable

data class RowUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    override val paddings: Paddings?,
    val subcomponents: List<UiElement>,
) : UiElement

data class RadioGroupUiElement(
    override val type: UiElementTypes,
    override val id: String,
    override val label: String,
    override val weight: Float? = null,
    override val required: Boolean? = null,
    override val paddings: Paddings?,
    val values: List<RadioButtonUiElement>,
    val selectedId: String,
) : UiElement, Requireable {

    data class RadioButtonUiElement(
        val id: String,
        val label: String,
    )
}