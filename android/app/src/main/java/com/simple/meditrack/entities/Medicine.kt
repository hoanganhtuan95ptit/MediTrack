package com.simple.meditrack.entities

import androidx.annotation.Keep

@Keep
data class Medicine(
    val id: String,
    val name: String,
    val image: String,

    val total: Long = 0,
    val current: Long = 0,

    val unit: Unit = Unit.TABLET
) {

    enum class Unit {
        TABLET, // dạng viên
        LIQUID// dạng nước
    }
}