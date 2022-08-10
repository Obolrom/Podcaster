package io.obolonsky.core.di.utils

interface Mapper<I : Any, O : Any> {

    fun map(input: I): O
}