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
            )
        }
        return QuizTemplate(
            fields = fields,
        )
    }
}