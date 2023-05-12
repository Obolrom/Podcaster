package io.obolonsky.quizzy.redux

data class QuizScreenState(
    val title: String?,
    val template: QuizTemplate? = null,
)

data class QuizTemplate(
    val type: UiElementTypes,
    val labelKey: String,
)

data class QuizTemplateInput(
    val type: String,
    val label_key: String,
)

enum class UiElementTypes {
    TEXT_LABEL,
}