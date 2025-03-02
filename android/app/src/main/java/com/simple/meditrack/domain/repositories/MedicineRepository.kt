package com.simple.meditrack.domain.repositories

import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {

    suspend fun getAllAsync(): Flow<List<Medicine>>

    suspend fun search(query: String): Flow<List<Medicine>>

    suspend fun getBy(id: String): Medicine?

    suspend fun getByAsync(id: String): Flow<Medicine?>

    suspend fun delete(id: String)

    suspend fun insertOrUpdate(medicine: Medicine)
}