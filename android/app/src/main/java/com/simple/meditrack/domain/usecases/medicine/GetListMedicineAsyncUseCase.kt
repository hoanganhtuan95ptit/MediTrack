package com.simple.meditrack.domain.usecases.medicine

import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.flow.Flow

class GetListMedicineAsyncUseCase(
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(): Flow<List<Medicine>> = medicineRepository.getAllAsync()
}