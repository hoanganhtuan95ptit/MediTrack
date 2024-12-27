package com.simple.meditrack.domain.usecases

import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.flow.Flow

class GetAlarmByIdAsyncUseCase(
    private val alarmRepository: AlarmRepository
) {

    suspend fun execute(param: Param): Flow<Alarm> = alarmRepository.getAsync(param.id)

    data class Param(
        val id: String
    )
}