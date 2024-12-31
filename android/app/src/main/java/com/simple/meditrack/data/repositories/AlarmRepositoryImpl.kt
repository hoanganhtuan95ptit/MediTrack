package com.simple.meditrack.data.repositories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map

class AlarmRepositoryImpl : AlarmRepository {

    val list = MediatorLiveData(alarmFake)

    override fun getAllAsync(): Flow<List<Alarm>> = channelFlow {

        list.asFlow().launchCollect(this) {

            trySend(it)
        }

        awaitClose()
    }

    override fun getByAsync(id: String): Flow<Alarm> = channelFlow {

        list.asFlow().map { list ->

            list.filter { it.id == id }
        }.launchCollect(this) {

            it.firstOrNull()?.let {

                trySend(it)
            }
        }

        awaitClose()
    }

    override fun insertOrUpdate(alarm: Alarm) {

        val map = list.value.orEmpty().associateBy { it.id }.toMutableMap()

        map[alarm.id] = alarm

        list.postValue(map.values.toList())
    }
}

val alarmFake = listOf(

    Alarm(
        note = "Cố gắng để khỏi bệnh",
        name = "Uống thuốc buổi sáng",
        image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_5.png",

        hour = 8,
        minute = 0,
        step = 1,

        isActive = false,

        item = listOf(
            Alarm.MedicineItem(
                dosage = 1.0,
                medicineId = "3",
            )
        )
    ),
    Alarm(

        note = "Cố gắng để khỏi bệnh",
        name = "Uống thuốc buổi trưa",
        image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_7.png",

        hour = 12,
        minute = 0,
        step = 1,

        item = listOf(
            Alarm.MedicineItem(
                dosage = 0.5,
                medicineId = "2"
            ),
            Alarm.MedicineItem(
                dosage = 1.0,
                medicineId = "3"
            )
        )
    ),
    Alarm(

        note = "Cố gắng để khỏi bệnh",
        name = "Uống thuốc buổi tối",
        image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_0.png",

        hour = 20,
        minute = 0,
        step = 1,

        item = listOf(
            Alarm.MedicineItem(
                dosage = 0.5,
                medicineId = "1"
            ),
            Alarm.MedicineItem(
                dosage = 1.5,
                medicineId = "2"
            ),
            Alarm.MedicineItem(
                dosage = 1.0,
                medicineId = "3"
            )
        )
    )
)