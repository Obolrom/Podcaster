package io.obolonsky.quizzy.mappers

import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.quizzy.redux.QuizTemplate
import io.obolonsky.quizzy.redux.QuizTemplateInput
import io.obolonsky.quizzy.redux.UiElementTypes

class QuizTemplateMapper : Mapper<QuizTemplateInput, QuizTemplate> {

    override fun map(input: QuizTemplateInput): QuizTemplate {
        return QuizTemplate(
            type = UiElementTypes.valueOf(input.type),
            labelKey = input.label_key,
        )
    }
}