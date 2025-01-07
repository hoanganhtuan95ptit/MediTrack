package com.simple.meditrack.domain.usecases.alarm

import com.simple.meditrack.domain.repositories.AlarmRepository

class DeleteAlarmUseCase(
    private val alarmRepository: AlarmRepository,
) {

    suspend fun execute(param: Param) {

        alarmRepository.delete(id = param.id)
    }

    data class Param(
        val id: String
    )
}