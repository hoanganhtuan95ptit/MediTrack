package com.simple.meditrack.entities

import androidx.annotation.Keep

@Keep
data class MedicineNotification(
    val note: String,
    val name: String,
    val image: String,

    val step: Int,
    val time: String,

    val item: List<MedicineItem> = emptyList(),
) {

    @Keep
    data class MedicineItem(
        val note: String,
        val dosage: String,
        val medicine: Medicine
    )
}