package com.simple.meditrack.data.repositories

import com.simple.coreapp.utils.ext.launchCollect
import com.simple.meditrack.data.dao.MedicineDao
import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MedicineRepositoryImpl(
    private val medicineDao: MedicineDao
) : MedicineRepository {

    override suspend fun getAllAsync(): Flow<List<Medicine>> {

        return medicineDao.getListAllAsync()
    }

    override suspend fun search(query: String): Flow<List<Medicine>> = channelFlow {

        val limit = 10

        if (query.isBlank()) {

            medicineDao.getListByAsync(limit = limit)
        } else {

            medicineDao.searchListByNameAsync(query = query, limit = limit)
        }.launchCollect(this) {

            trySend(it)
        }

        awaitClose()
    }

    override suspend fun getBy(id: String): Medicine? {

        return medicineDao.getListByIdAsync(id = id).first().firstOrNull()
    }

    override suspend fun getByAsync(id: String): Flow<Medicine?> {

        return medicineDao.getListByIdAsync(id = id).map {

            it.firstOrNull()
        }
    }

    override suspend fun delete(id: String) {

        medicineDao.delete(id = id)
    }

    override suspend fun insertOrUpdate(medicine: Medicine) {

        medicineDao.insertOrUpdate(medicine)
    }
}