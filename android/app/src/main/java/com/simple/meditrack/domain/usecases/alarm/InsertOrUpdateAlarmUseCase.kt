package com.simple.meditrack.domain.usecases.alarm

import android.util.Log
import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Alarm

class InsertOrUpdateAlarmUseCase(
    private val alarmRepository: AlarmRepository,
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(param: Param) {

        param.alarm.item.mapNotNull {

            Log.d("tuanha", "execute: ${it.medicine}")
            it.medicine
        }.map {

            medicineRepository.insertOrUpdate(it)
        }

        alarmRepository.insertOrUpdate(param.alarm)
    }

    data class Param(
        val alarm: Alarm
    )
}