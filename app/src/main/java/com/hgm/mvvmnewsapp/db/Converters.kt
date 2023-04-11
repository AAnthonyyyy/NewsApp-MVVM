package com.hgm.mvvmnewsapp.db

import androidx.room.TypeConverter
import com.hgm.mvvmnewsapp.models.Source

/**
 * 类型转换器：由于 Room 无法识别自定义的类型，所有创建转换器帮助
 */
class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(id = name, name = name)
    }
}