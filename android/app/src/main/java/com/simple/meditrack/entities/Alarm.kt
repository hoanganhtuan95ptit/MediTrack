package com.simple.meditrack.entities

import androidx.annotation.Keep
import java.io.Serializable
import java.util.UUID

@Keep
data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val note: String,
    val name: String,
    val image: String,

    val step: Long = 1, // khoảng thời gian giữa các lần thông báo
    val hour: Int = 0, // giờ thông báo
    val minute: Int = 0, // phút thông báo

    val isActive: Boolean = true, // thông báo có đang được kích hoạt hay không

    val item: List<MedicineItem> = emptyList(),
) : Serializable {

    @Keep
    data class MedicineItem(
        val id: String = UUID.randomUUID().toString(),
        val dosage: Double,
        val medicine: Medicine
    ) : Serializable
}