package com.simple.meditrack.domain.usecases.medicine

import com.simple.meditrack.domain.repositories.MedicineRepository

class DeleteMedicineUseCase(
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(param: Param) {

        medicineRepository.delete(id = param.id)
    }

    data class Param(
        val id: String
    )
}