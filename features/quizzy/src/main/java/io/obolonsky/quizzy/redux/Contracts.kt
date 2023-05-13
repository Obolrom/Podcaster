package io.obolonsky.quizzy.redux

import io.obolonsky.quizzy.ui.components.UiElement

data class QuizScreenState(
    val title: String?,
    val template: QuizTemplate? = null,
    val uiElements: List<UiElement>? = null,
)

data class QuizTemplate(
    val fields: List<Field>,
) {

    data class Field(
        val id: String,
        val type: UiElementTypes,
        val labelKey: String,
    )
}

data class QuizTemplateInput(
    val fields: List<Field>,
) {

    data class Field(
        val id: String,
        val type: String,
        val label_key: String,
    )
}

enum class UiElementTypes {
    TEXT_LABEL,
    CHECKBOX,
    INPUT,
}