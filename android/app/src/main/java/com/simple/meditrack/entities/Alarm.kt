package com.simple.meditrack.entities

import androidx.annotation.Keep
import java.io.Serializable
import java.util.UUID

@Keep
data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val idInt: Int = (System.currentTimeMillis() / 1000).toInt(),

    val note: String,
    val name: String,
    val image: String,

    val step: Long = 1, // khoảng thời gian giữa các lần thông báo
    val hour: Int = 0, // giờ thông báo
    val minute: Int = 0, // phút thông báo

    val isActive: Boolean = true, // thông báo có đang được kích hoạt hay không

    val item: List<MedicineItem> = emptyList(),

    val createTime: Long = System.currentTimeMillis()
) : Serializable {

    @Keep
    data class MedicineItem(
        val id: String = UUID.randomUUID().toString(),
        val dosage: Double,

        val medicineId: String = "",
    ) : Serializable {

        var medicine: Medicine? = null
    }
}