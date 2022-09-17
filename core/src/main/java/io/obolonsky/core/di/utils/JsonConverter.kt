package io.obolonsky.core.di.utils

interface JsonConverter {

    @Throws
    fun <T> fromJson(json: String?, classOfT: Class<T>): T

    fun toJson(src: Any): String
}