package com.simple.meditrack.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.meditrack.data.dao.RoomMedicine.Companion.toEntity
import com.simple.meditrack.data.dao.RoomMedicine.Companion.toRoom
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private const val TABLE_NAME = "medicine"

@Dao
interface MedicineDao {

    fun getListAllAsync(): Flow<List<Medicine>> = getRoomListAllAsync().map {

        it.map {
            it.toEntity()
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE 1=1")
    fun getRoomListAllAsync(): Flow<List<RoomMedicine>>


    fun getListByAsync(limit: Int): Flow<List<Medicine>> = getRoomListByAsync(limit = limit).map {

        it.map {
            it.toEntity()
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE 1=1 LIMIT :limit")
    fun getRoomListByAsync(limit: Int): Flow<List<RoomMedicine>>


    fun searchListByNameAsync(query: String, limit: Int): Flow<List<Medicine>> = searchRoomListByNameAsync(query = query, limit = limit).map {

        it.map {
            it.toEntity()
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE name LIKE :query LIMIT :limit")
    fun searchRoomListByNameAsync(query: String, limit: Int): Flow<List<RoomMedicine>>


    fun getListByIdAsync(id: String): Flow<List<Medicine>> = getRoomListByIdAsync(id = id).map {

        it.map {
            it.toEntity()
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE id COLLATE NOCASE == :id")
    fun getRoomListByIdAsync(id: String): Flow<List<RoomMedicine>>


    @Query("DELETE FROM $TABLE_NAME WHERE id COLLATE NOCASE == :id")
    fun delete(id: String)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()


    fun insertOrUpdate(item: Medicine) {

        insertOrUpdateRoom(item.toRoom())
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateRoom(room: RoomMedicine)


    fun insertOrUpdate(item: List<Medicine>) {

        insertOrUpdateRoom(item.map { it.toRoom() })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateRoom(rooms: List<RoomMedicine>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["id"]
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class RoomMedicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val image: String,

    val note: String = "",

    val quantity: Double = Medicine.UNLIMITED,

    val unit: Int = Medicine.Unit.TABLET.value // loại thuốc
) {
    companion object {

        fun Medicine.toRoom() = RoomMedicine(
            id = id,

            name = name,
            image = image,

            note = note,

            quantity = quantity,

            unit = unit
        )

        fun RoomMedicine.toEntity() = Medicine(
            id = id,

            name = name,
            image = image,

            note = note,

            quantity = quantity,

            unit = unit
        )
    }
}