package com.simple.meditrack.entities

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val image: String,

    val total: Long = 0,
    val current: Long = 0,

    val unit: Unit = Unit.TABLET // loại thuốc
) {

    enum class Unit {
        TABLET, // dạng viên
        LIQUID// dạng nước
    }
}