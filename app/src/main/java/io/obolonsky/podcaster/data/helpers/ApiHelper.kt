package io.obolonsky.podcaster.data.helpers

interface ApiHelper<ResultType> {

    fun load(): ResultType
}