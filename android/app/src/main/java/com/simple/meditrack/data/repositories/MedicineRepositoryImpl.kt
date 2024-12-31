package com.simple.meditrack.data.repositories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.meditrack.domain.repositories.MedicineRepository
import com.simple.meditrack.entities.Medicine
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class MedicineRepositoryImpl : MedicineRepository {

    val list = MediatorLiveData(medicineFake)

    override suspend fun search(query: String): Flow<List<Medicine>> = channelFlow {

        list.asFlow().launchCollect(this) {

            trySend(it)
        }

        awaitClose()
    }

    override suspend fun getBy(id: String): Medicine? {

        return list.asFlow().map {

            it.firstOrNull {
                it.id == id
            }
        }.firstOrNull()
    }

    override suspend fun getByAsync(id: String): Flow<Medicine?> = list.asFlow().map {

        it.firstOrNull {
            it.id == id
        }
    }

    override suspend fun insertOrUpdate(medicine: Medicine) {

        val map = list.value.orEmpty().associateBy { it.id }.toMutableMap()

        map[medicine.id] = medicine

        list.postValue(map.values.toList())
    }
}

val medicineFake = listOf(
    Medicine(
        id = "3",
        name = "Viên trắng tròn",
        image = "",
        unit = Medicine.Unit.TABLET.value
    ), Medicine(
        id = "2",
        name = "Viên trong vỉ",
        image = "",
        unit = Medicine.Unit.TABLET.value
    ), Medicine(
        id = "1",
        name = "Viên vàng tròn",
        image = "",
        note = "Nhai nuốt",
        unit = Medicine.Unit.TABLET.value,
        quantity = 100.0
    )
)