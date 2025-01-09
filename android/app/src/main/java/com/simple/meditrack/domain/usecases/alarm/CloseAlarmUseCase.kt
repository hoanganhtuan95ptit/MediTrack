package com.simple.meditrack.domain.usecases.alarm

import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull

class CloseAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(param: Param) {

        val alarm = alarmRepository.getByAsync(param.alarmId).firstOrNull() ?: return

        alarm.item.forEach {

            val medicine = medicineRepository.getBy(it.medicineId) ?: return@forEach

            if (medicine.quantity == Medicine.UNLIMITED) return@forEach

            medicine.quantity -= it.dosage

            medicineRepository.insertOrUpdate(medicine)
        }
    }

    data class Param(
        val alarmId: String
    )
}