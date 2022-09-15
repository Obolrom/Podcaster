package io.obolonsky.core.di.depsproviders

import io.obolonsky.core.di.utils.JsonConverter

interface JsonConverterProvider {

    val jsonConverter: JsonConverter
}