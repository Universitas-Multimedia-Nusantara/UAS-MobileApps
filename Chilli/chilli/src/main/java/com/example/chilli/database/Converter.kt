package com.example.chilli.database
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Timestamp

class Converter {
    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toList(string: String?): List<String>? {
        return string?.split(",")?.map { it.trim() }
    }
}

class UserConverter{
    @TypeConverter
    fun fromListToJson(list: List<Map<String, String>>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonToList(json: String?): List<Map<String, String>>? {
        val type = object : TypeToken<List<Map<String, String>>>() {}.type
        return Gson().fromJson(json, type)
    }
}

class TimestampConverter {
    @TypeConverter
    fun fromTimestampToLong(timestamp: Timestamp): Long {
        return timestamp.time
    }

    @TypeConverter
    fun fromLongToTimestamp(value: Long): Timestamp {
        return Timestamp(value)
    }
}

