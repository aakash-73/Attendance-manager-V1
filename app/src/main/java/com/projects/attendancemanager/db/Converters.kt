package com.projects.attendancemanager.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bson.types.ObjectId

class Converters {

    private val gson = Gson()

    // Convert List<String> to JSON String
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value)
    }

    // Convert JSON String to List<String>
    @TypeConverter
    fun toStringList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Convert ObjectId to String
    @TypeConverter
    fun fromObjectId(id: ObjectId?): String? {
        return id?.toHexString()
    }

    // Convert String to ObjectId
    @TypeConverter
    fun toObjectId(id: String?): ObjectId? {
        return id?.let { ObjectId(it) }
    }
}
