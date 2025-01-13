package com.simple.meditrack.utils.exts

import java.text.DecimalFormat

fun Double.formatQuality() = DecimalFormat("#.##").format(this)

fun String.parseQuality() = DecimalFormat("#.##").parse(this)?.toDouble()

fun Int.formatTime() = DecimalFormat("00").format(this)
