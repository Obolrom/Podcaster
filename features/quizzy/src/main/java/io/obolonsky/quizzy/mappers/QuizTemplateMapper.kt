package io.obolonsky.quizzy.mappers

import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.quizzy.redux.QuizTemplate
import io.obolonsky.quizzy.redux.QuizTemplateInput
import io.obolonsky.quizzy.redux.UiElementTypes

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
        return QuizTemplate(
            fields = fields,
        )
    }
}