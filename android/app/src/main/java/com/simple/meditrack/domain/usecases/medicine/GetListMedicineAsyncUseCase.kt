package com.simple.meditrack.domain.usecases.medicine

import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GetListMedicineAsyncUseCase(
    private val alarmRepository: AlarmRepository,
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(): Flow<List<Medicine>> {

        val alarms = alarmRepository.getAllAsync().firstOrNull().orEmpty()

        return medicineRepository.getAllAsync().map { medicines ->

            medicines.map { medicine ->

                medicine.countForNextDays = (0..100).toList().associateWith {

                    if (medicine.quantity != Medicine.UNLIMITED) getCountForNextDay(medicine, alarms, it)
                    else Medicine.UNLIMITED
                }.filter {

                    it.value == Medicine.UNLIMITED || it.value < medicine.quantity
                }
            }

            medicines
        }
    }

    private suspend fun getCountForNextDay(medicine: Medicine, alarms: List<Alarm>, date: Int): Double {

        return alarms.filter {


            it.item.map { it.medicineId }.contains(medicine.id)
        }.sumOf { alarm ->

            val count = alarmRepository.getCountAlarmInDate(alarm, date)

            val number = count * alarm.item.filter {

                it.medicineId == medicine.id
            }.sumOf {

                it.dosage
            }

            number
        }
    }
}