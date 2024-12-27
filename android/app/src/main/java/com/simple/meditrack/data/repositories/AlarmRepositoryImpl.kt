package com.simple.meditrack.data.repositories

import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class AlarmRepositoryImpl : AlarmRepository {

    override fun getAllAsync(): Flow<List<Alarm>> = channelFlow {

        trySend(alarmFake)

        awaitClose()
    }

    override fun getAsync(id: String): Flow<Alarm> = channelFlow {

        alarmFake.find {

            it.id == id
        }?.let {

            trySend(it)
        }

        awaitClose()
    }
}

val alarmFake = listOf(

    Alarm(
        note = "Cố gắng để khỏi bệnh",
        name = "Uống thuốc buổi sáng",
        image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder.png",

        hour = 8,
        minute = 0,
        step = 1,

        isActive = false,

        item = listOf(
            Alarm.MedicineItem(
                note = "",
                dosage = 1.0,
                medicine = Medicine(
                    id = "3",
                    name = "Viên trắng tròn",
                    image = "",
                    unit = Medicine.Unit.TABLET
                )
            )
        )
    ),
    Alarm(

        note = "Cố gắng để khỏi bệnh",
        name = "Uống thuốc buổi trưa",
        image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_2.png",

        hour = 12,
        minute = 0,
        step = 1,

        item = listOf(
            Alarm.MedicineItem(
                note = "",
                dosage = 0.5,
                medicine = Medicine(
                    id = "2",
                    name = "Viên trong vỉ",
                    image = "",
                    unit = Medicine.Unit.TABLET
                )
            ),
            Alarm.MedicineItem(
                note = "",
                dosage = 1.0,
                medicine = Medicine(
                    id = "3",
                    name = "Viên trắng tròn",
                    image = "",
                    unit = Medicine.Unit.TABLET
                )
            )
        )
    ),
    Alarm(

        note = "Cố gắng để khỏi bệnh",
        name = "Uống thuốc buổi tối",
        image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder.png",

        hour = 20,
        minute = 0,
        step = 1,

        item = listOf(
            Alarm.MedicineItem(
                note = "Nhai nuốt",
                dosage = 0.5,
                medicine = Medicine(
                    id = "1",
                    name = "Viên vàng tròn",
                    image = "",
                    unit = Medicine.Unit.TABLET
                )
            ),
            Alarm.MedicineItem(
                note = "",
                dosage = 1.5,
                medicine = Medicine(
                    id = "2",
                    name = "Viên trong vỉ",
                    image = "",
                    unit = Medicine.Unit.TABLET
                )
            ),
            Alarm.MedicineItem(
                note = "",
                dosage = 1.0,
                medicine = Medicine(
                    id = "3",
                    name = "Viên trắng tròn",
                    image = "",
                    unit = Medicine.Unit.TABLET
                )
            )
        )
    )
)