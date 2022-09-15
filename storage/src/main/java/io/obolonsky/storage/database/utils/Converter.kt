package io.obolonsky.storage.database.utils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import io.obolonsky.core.di.utils.JsonConverter
import java.lang.reflect.Type

@ProvidedTypeConverter
class Converter(
    private val jsonConverter: JsonConverter,
) {

    @TypeConverter
    fun fromString(value: String?): List<String> {
        val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
        return jsonConverter.fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        return jsonConverter.toJson(list)
    }
}