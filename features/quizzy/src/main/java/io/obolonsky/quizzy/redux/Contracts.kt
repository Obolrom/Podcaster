package io.obolonsky.quizzy.redux

import io.obolonsky.quizzy.data.UiElement

data class QuizScreenState(
    val title: String?,
    val template: QuizTemplate? = null,
    val uiElements: List<UiElement>? = null,
    val triggers: List<QuizTemplate.Trigger>? = null,
)

sealed class QuizScreenSideEffect {

    object NotAllRequiredFieldsAreFilled : QuizScreenSideEffect()
}

data class QuizTemplate(
    val fields: List<Field>,
    val triggers: List<Trigger>,
) {

    data class Field(
        val id: String,
        val type: UiElementTypes,
        val labelKey: String,
        val subfields: List<Field>,
        val weight: Float? = null,
        val values: List<Value>? = null,
        val required: Boolean? = null,
        val paddings: Paddings? = null,
    ) {

        data class Value(
            val id: String,
            val labelKey: String,
        )

        data class Paddings(
            val start: Int,
            val end: Int,
            val top: Int,
            val bottom: Int,
        )
    }

    data class Trigger(
        val fieldId: String,
        val actionType: ActionType,
        val conditions: List<Condition>,
        val operations: List<Operation>,
    ) {

        // TODO: now we can check only with static data
        data class Condition(
            val conditionFieldId: String,
            val conditionType: ConditionType,
            val value: Any,
        )

        data class Operation(
            val fieldId: String,
            val operationType: OperationType,
            val value: String?,
        )
    }
}

data class QuizTemplateInput(
    val fields: List<Field>,
    val triggers: List<Trigger>,
) {

    data class Field(
        val id: String,
        val type: String,
        val label_key: String,
        val weight: Float? = null,
        val subfields: List<Field>? = null,
        val values: List<Value>? = null,
        val required: Boolean? = null,
        val paddings: Paddings? = null,
    ) {

        data class Value(
            val id: String,
            val label_key: String,
        )

        data class Paddings(
            val start: Int,
            val end: Int,
            val top: Int,
            val bottom: Int,
        )
    }

    data class Trigger(
        val field_id: String,
        val action_type: String,
        val conditions: List<Condition>,
        val operations: List<Operation>,
    ) {

        // TODO: now we can check only with static data
        data class Condition(
            val condition_field_id: String,
            val condition_type: String,
            val value: Any,
        )

        data class Operation(
            val field_id: String,
            val operation_type: String,
            val value: String?,
        )
    }
}

enum class UiElementTypes {
    TEXT_LABEL,
    CHECKBOX,
    INPUT,
    ROW,
    RADIO,
    MULTISELECT,
}

enum class ActionType {
    CHANGE,
}

enum class ConditionType {
    EQUALS,
}

enum class OperationType {
    SET_VALUE,
}