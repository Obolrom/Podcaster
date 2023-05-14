package io.obolonsky.quizzy.redux

import io.obolonsky.quizzy.data.UiElement

data class QuizScreenState(
    val title: String?,
    val template: QuizTemplate? = null,
    val uiElements: List<UiElement>? = null,
)

sealed class QuizScreenSideEffect {

    object NotAllRequiredFieldsAreFilled : QuizScreenSideEffect()
}

data class QuizTemplate(
    val fields: List<Field>,
) {

    data class Field(
        val id: String,
        val type: UiElementTypes,
        val labelKey: String,
        val subfields: List<Field>,
        val weight: Float? = null,
        val values: List<Value>? = null,
        val required: Boolean? = null,
    ) {

        data class Value(
            val id: String,
            val labelKey: String,
        )
    }
}

data class QuizTemplateInput(
    val fields: List<Field>,
) {

    data class Field(
        val id: String,
        val type: String,
        val label_key: String,
        val weight: Float? = null,
        val subfields: List<Field>? = null,
        val values: List<Value>? = null,
        val required: Boolean? = null,
    ) {

        data class Value(
            val id: String,
            val label_key: String,
        )
    }
}

enum class UiElementTypes {
    TEXT_LABEL,
    CHECKBOX,
    INPUT,
    ROW,
    RADIO,
}