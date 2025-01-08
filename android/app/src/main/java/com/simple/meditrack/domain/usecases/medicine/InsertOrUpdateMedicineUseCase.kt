package com.simple.meditrack.domain.usecases.medicine

import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Medicine

class InsertOrUpdateMedicineUseCase(
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(param: Param) {
        medicineRepository.insertOrUpdate(param.medicine)
    }

    data class Param(
        val medicine: Medicine
    )
}