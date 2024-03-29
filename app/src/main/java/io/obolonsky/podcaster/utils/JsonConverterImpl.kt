package io.obolonsky.podcaster.utils

import com.google.gson.Gson
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.core.di.utils.JsonConverter
import javax.inject.Inject

@ApplicationScope
class JsonConverterImpl @Inject constructor() : JsonConverter {

    private val gson = Gson()

    override fun <T> fromJson(json: String?, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }

    override fun toJson(src: Any): String {
        return gson.toJson(src)
    }
}