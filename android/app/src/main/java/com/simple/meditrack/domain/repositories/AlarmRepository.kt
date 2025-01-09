package com.simple.meditrack.domain.repositories

import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    suspend fun getAllAsync(): Flow<List<Alarm>>

    suspend fun getByAsync(id: String): Flow<Alarm?>

    suspend fun delete(id: String)

    suspend fun insertOrUpdate(alarm: Alarm)

    suspend fun getCountAlarmInDate(alarm: Alarm, date: Int): Int

}