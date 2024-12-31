package com.simple.meditrack.domain.repositories

import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun getAllAsync(): Flow<List<Alarm>>

    fun getByAsync(id: String): Flow<Alarm>

    fun insertOrUpdate(alarm: Alarm)
}