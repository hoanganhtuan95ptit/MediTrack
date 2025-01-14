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

    override suspend fun getAllAsync(): Flow<List<Alarm>> = channelFlow {

        alarmDao.getListAllAsync().launchCollect(this) {

            trySend(it.sortedBy { it.minute }.sortedBy { it.hour })
        }

        awaitClose()
    }

    override suspend fun getByAsync(id: String): Flow<Alarm?> = channelFlow {

        alarmDao.getListByIdAsync(id = id).launchCollect(this) {

            trySend(it.firstOrNull())
        }

        awaitClose()
    }

    override suspend fun delete(id: String) {

        alarmDao.delete(id = id)
    }

    override suspend fun insertOrUpdate(alarm: Alarm) {

        alarmDao.insertOrUpdate(alarm)
    }

    override suspend fun getCountAlarmInDate(alarm: Alarm, date: Int): Int {

        val currentTime = System.currentTimeMillis()

        val dateEnd = currentTime + date * DATE
        val countDate = (currentTime - alarm.createTime) / DATE
        val countStep = countDate / alarm.step + 1

        var count = 0

        while (dateEnd > alarm.createTime + (countStep + count) * alarm.step * DATE) {

            count++
        }

        return count
    }

    companion object {

        private const val DATE = 24 * 60 * 60 * 100
    }
}