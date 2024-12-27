package com.simple.meditrack.domain.usecases

import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.flow.Flow

class GetListNotificationAsyncUseCase(
    private val alarmRepository: AlarmRepository
) {

    suspend fun execute(): Flow<List<Alarm>> = alarmRepository.getAllAsync()
}