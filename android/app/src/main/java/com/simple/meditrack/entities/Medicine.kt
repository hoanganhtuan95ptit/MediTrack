package com.simple.meditrack.entities

import androidx.annotation.Keep
import java.io.Serializable
import java.util.UUID

@Keep
data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val image: String,

    val note: String = "",

    var quantity: Double = UNLIMITED,

    var unit: Int = Unit.TABLET.value, // loại thuốc

    var createTime: Long = System.currentTimeMillis()
) : Serializable {

    var countForNextDays: Map<Int, Double> = hashMapOf()

    enum class Unit(val value: Int) {
        TABLET(0), // dạng viên
        LIQUID(1)// dạng nước
    }

    companion object {

        const val UNLIMITED = -1.0

        fun Int.toUnit() = Unit.entries.find { it.value == this }
    }
}