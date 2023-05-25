package io.obolonsky.quizzy.usecases

import android.content.Context
import arrow.core.Option
import arrow.core.raise.option
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.core.di.utils.JsonConverter
import io.obolonsky.quizzy.mappers.QuizTemplateMapper
import io.obolonsky.quizzy.redux.QuizTemplate
import io.obolonsky.quizzy.redux.QuizTemplateInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@FeatureScope
class GetTemplateUseCase @Inject constructor(
    private val context: Context,
    private val jsonConverter: JsonConverter,
) {

    private val mapper = QuizTemplateMapper()

    fun getTemplate(assetPath: String): Option<QuizTemplate> = option {
        val templateJson = context.assets
            .open(assetPath)
            .use { inputStream -> inputStream.readBytes() }
            .let(::String)
        ensureNotNull(templateJson)
    }
        .map { templateJson -> jsonConverter.fromJson(templateJson, QuizTemplateInput::class.java) }
        .map(mapper::map)

    fun get(assetPath: String): Flow<QuizTemplate> {
        return flow {
            val templateJson = context.assets
                .open(assetPath)
                .readBytes()
                .let(::String)
            val template = jsonConverter.fromJson(templateJson, QuizTemplateInput::class.java)
            emit(template)
        }
            .map(mapper::map)
    }
}