package io.obolonsky.quizzy.usecases

import android.content.Context
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.core.di.utils.JsonConverter
import io.obolonsky.quizzy.data.Localization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FeatureScope
class GetLocalizationsUseCase @Inject constructor(
    private val context: Context,
    private val jsonConverter: JsonConverter,
) {

    fun get(): Flow<Localization> = flow {
        val type = mutableMapOf<String, String>().javaClass
        val localizationMap = context.assets
            .open("strings/en.json")
            .readBytes()
            .let(::String)
        val translations = jsonConverter.fromJson(localizationMap, type)
        emit(Localization(translations))
    }
}