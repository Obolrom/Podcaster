package io.obolonsky.quizzy.mappers

import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.quizzy.redux.*

class QuizTemplateMapper : Mapper<QuizTemplateInput, QuizTemplate> {

    override fun map(input: QuizTemplateInput): QuizTemplate {
        val fields = input.fields.map { field ->
            QuizTemplate.Field(
                id = field.id,
                type = UiElementTypes.valueOf(field.type),
                labelKey = field.label_key,
                weight = field.weight,
                subfields = field.subfields?.map { subfield ->
                    QuizTemplate.Field(
                        id = subfield.id,
                        type = UiElementTypes.valueOf(subfield.type),
                        labelKey = subfield.label_key,
                        weight = subfield.weight,
                        subfields = emptyList(),
                        required = subfield.required,
                        paddings = subfield.paddings?.let { paddings ->
                            QuizTemplate.Field.Paddings(
                                start = paddings.start,
                                end = paddings.end,
                                top = paddings.top,
                                bottom = paddings.bottom,
                            )
                        },
                        values = subfield.values?.map {
                            QuizTemplate.Field.Value(
                                id = it.id,
                                labelKey = it.label_key,
                            )
                        },
                    )
                }.orEmpty(),
                values = field.values?.map {
                    QuizTemplate.Field.Value(
                        id = it.id,
                        labelKey = it.label_key,
                    )
                },
                required = field.required,
                paddings = field.paddings?.let { paddings ->
                    QuizTemplate.Field.Paddings(
                        start = paddings.start,
                        end = paddings.end,
                        top = paddings.top,
                        bottom = paddings.bottom,
                    )
                },
            )
        }
        val triggers = input.triggers.map { trigger ->
            QuizTemplate.Trigger(
                fieldId = trigger.field_id,
                actionType = ActionType.valueOf(trigger.action_type),
                conditions = trigger.conditions.map { condition ->
                    QuizTemplate.Trigger.Condition(
                        conditionType = ConditionType.valueOf(condition.condition_type),
                        value = condition.value,
                    )
                },
                operations = trigger.operations.map { operation ->
                    QuizTemplate.Trigger.Operation(
                        fieldId = operation.field_id,
                        operationType = OperationType.valueOf(operation.operation_type),
                        value = operation.value,
                    )
                },
            )
        }

        return QuizTemplate(
            fields = fields,
            triggers = triggers,
        )
    }
}