package com.simple.meditrack.domain.usecases.alarm

import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAlarmByIdAsyncUseCase(
    private val alarmRepository: AlarmRepository,
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(param: Param): Flow<Alarm?> = alarmRepository.getByAsync(param.id).map {

        it?.item?.map {

            it.medicine = medicineRepository.getBy(it.medicineId)
        }

        it
    }

    data class Param(
        val id: String
    )
}