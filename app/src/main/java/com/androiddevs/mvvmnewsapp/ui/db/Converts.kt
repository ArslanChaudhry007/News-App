package com.androiddevs.mvvmnewsapp.ui.db

import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.ui.model.Source

class Converts {
    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}