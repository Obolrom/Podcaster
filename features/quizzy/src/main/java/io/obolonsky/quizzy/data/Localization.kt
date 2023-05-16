package io.obolonsky.quizzy.data

class Localization(
    private val translations: Map<String, String>,
) {

    fun getString(key: String): String {
        return translations[key] ?: key
    }
}