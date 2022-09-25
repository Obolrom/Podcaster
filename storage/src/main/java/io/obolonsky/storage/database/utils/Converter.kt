package io.obolonsky.storage.database.utils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import io.obolonsky.core.di.utils.JsonConverter

@ProvidedTypeConverter
class Converter(
    private val jsonConverter: JsonConverter,
) {

    @TypeConverter
    fun fromString(value: String?): List<String> {
        return jsonConverter.fromJson(value, ArrayList<String>().javaClass)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        return jsonConverter.toJson(list)
    }
}