package com.simple.meditrack.data.repositories

import com.simple.coreapp.utils.ext.launchCollect
import com.simple.meditrack.data.dao.AlarmDao
import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.entities.Alarm
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun getAllAsync(): Flow<List<Alarm>> = channelFlow {

        alarmDao.getListByAsync().launchCollect(this) {

            trySend(it.sortedBy { it.minute }.sortedBy { it.hour })
        }

        awaitClose()
    }

    override fun getByAsync(id: String): Flow<Alarm?> = channelFlow {

        alarmDao.getListByIdAsync(id = id).launchCollect(this) {

            trySend(it.firstOrNull())
        }

        awaitClose()
    }

    override fun delete(id: String) {

        alarmDao.delete(id = id)
    }

    override fun insertOrUpdate(alarm: Alarm) {

        alarmDao.insertOrUpdate(alarm)
    }
}