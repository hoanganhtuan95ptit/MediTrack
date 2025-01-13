package com.simple.meditrack.data.dao

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simple.meditrack.data.dao.KeyTranslateRoom.Companion.toEntity
import com.simple.meditrack.data.dao.KeyTranslateRoom.Companion.toRoom
import com.simple.meditrack.entities.KeyTranslate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TABLE_NAME = "key_translate"

@Dao
interface KeyTranslateDao {

    suspend fun insert(vararg entity: KeyTranslate) {

        insert(rooms = entity.map { it.toRoom() })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rooms: List<KeyTranslateRoom>)


    fun getAllAsync(langCode: String): Flow<List<KeyTranslate>> = getAllRoomAsync(langCode = langCode).map { rooms ->

        rooms.map {
            it.toEntity()
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE langCode = :langCode")
    fun getAllRoomAsync(langCode: String): Flow<List<KeyTranslateRoom>>
}

@Keep
@Entity(tableName = TABLE_NAME, primaryKeys = ["key", "langCode"])
open class KeyTranslateRoom(
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "langCode") val langCode: String,
) {

    companion object {

        fun KeyTranslateRoom.toEntity() = KeyTranslate(
            key = key,
            value = value,
            langCode = langCode
        )

        fun KeyTranslate.toRoom() = KeyTranslateRoom(
            key = key,
            value = value,
            langCode = langCode
        )
    }
}