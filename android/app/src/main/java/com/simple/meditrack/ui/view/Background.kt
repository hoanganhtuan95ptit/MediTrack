package com.simple.meditrack.ui.view

import android.view.ViewGroup

data class Size(
    val width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
)

data class Margin(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0
)

data class Padding(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0
)

data class TextStyle(
    val typeface: Int? = null,
    val textSize: Float? = null,
    val textGravity: Int? = null
)

data class Background(
    var backgroundColor: Int? = null,

    val cornerRadius: Int? = null,
    val cornerRadius_TL: Int? = null,
    val cornerRadius_TR: Int? = null,
    val cornerRadius_BL: Int? = null,
    val cornerRadius_BR: Int? = null,

    val strokeWidth: Int? = null,
    val strokeColor: Int? = null,
    val strokeDashGap: Int? = null,
    val strokeDashWidth: Int? = null
)