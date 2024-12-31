package com.simple.meditrack.domain.usecases.medicine

import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMedicineByIdAsyncUseCase(
    private val medicineRepository: MedicineRepository
) {

    suspend fun execute(param: Param): Flow<Medicine?> = medicineRepository.getByAsync(param.id)

    data class Param(
        val id: String
    )
}