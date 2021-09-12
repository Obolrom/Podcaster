package io.obolonsky.podcaster.data.mappers

interface Mapper<InputType, OutputType> {

    fun map(input: InputType): OutputType

}