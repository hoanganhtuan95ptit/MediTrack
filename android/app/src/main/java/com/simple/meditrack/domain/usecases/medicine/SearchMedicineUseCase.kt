package com.simple.meditrack.domain.usecases.medicine

import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.flow.Flow

class SearchMedicineUseCase(
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(param: Param): Flow<List<Medicine>> = medicineRepository.search(param.query)

    data class Param(
        val query: String
    )
}