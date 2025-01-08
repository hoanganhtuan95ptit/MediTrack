package com.simple.meditrack.data.dao

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.simple.core.utils.extentions.toJson
import com.simple.core.utils.extentions.toListOrEmpty
import com.simple.meditrack.data.dao.RoomAlarm.Companion.toEntity
import com.simple.meditrack.data.dao.RoomAlarm.Companion.toRoom
import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private const val TABLE_NAME = "alarm"

@Dao
interface AlarmDao {

    fun getListAllAsync(): Flow<List<Alarm>> = getRoomListAllAsync().map {

        it.map {
            it.toEntity()
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE 1=1")
    fun getRoomListAllAsync(): Flow<List<RoomAlarm>>


    fun getListByIdAsync(id: String): Flow<List<Alarm>> = getRoomListByIdAsync(id = id).map {

        it.map {
            it.toEntity()
        }
    }

    @Query("SELECT * FROM $TABLE_NAME WHERE id COLLATE NOCASE == :id")
    fun getRoomListByIdAsync(id: String): Flow<List<RoomAlarm>>


    @Query("DELETE FROM $TABLE_NAME WHERE id COLLATE NOCASE == :id")
    fun delete(id: String)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()


    fun insertOrUpdate(item: Alarm) {

        insertOrUpdateRoom(item.toRoom())
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateRoom(room: RoomAlarm)


    fun insertOrUpdate(item: List<Alarm>) {

        insertOrUpdateRoom(item.map { it.toRoom() })
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateRoom(rooms: List<RoomAlarm>)
}

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["id"]
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class RoomAlarm(
    val id: String = UUID.randomUUID().toString(),
    val idInt: Int = (System.currentTimeMillis() / 1000).toInt(),

    val note: String,
    val name: String,
    val image: String,

    val step: Long = 1, // khoảng thời gian giữa các lần thông báo

    val hour: Int = 0, // giờ thông báo
    val minute: Int = 0, // phút thông báo

    val isActive: Boolean = true, // thông báo có đang được kích hoạt hay không

    val item: String = "",

    val createTime: Long = System.currentTimeMillis()
) {
    companion object {

        fun Alarm.toRoom() = RoomAlarm(
            id = id,
            idInt = idInt,

            note = note,
            name = name,
            image = image,

            step = step,

            hour = hour,
            minute = minute,

            isActive = isActive,

            item = item.map { it.copy() }.toJson(),

            createTime = createTime
        )

        fun RoomAlarm.toEntity() = Alarm(
            id = id,
            idInt = idInt,

            note = note,
            name = name,
            image = image,

            step = step,

            hour = hour,
            minute = minute,

            isActive = isActive,

            item = item.toListOrEmpty<Alarm.MedicineItem>(),

            createTime = createTime
        )
    }
}