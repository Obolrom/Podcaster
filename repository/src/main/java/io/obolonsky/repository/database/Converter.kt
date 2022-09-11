package io.obolonsky.repository.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import io.obolonsky.core.di.utils.JsonConverter
import java.lang.reflect.Type
import javax.inject.Inject

@ProvidedTypeConverter
class Converter @Inject constructor(
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