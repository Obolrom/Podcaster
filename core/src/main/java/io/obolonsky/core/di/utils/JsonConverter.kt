package io.obolonsky.core.di.utils

import java.lang.reflect.Type

interface JsonConverter {

    @Throws
    fun <T> fromJson(json: String?, typeOfT: Type): T

    fun toJson(src: Any): String
}