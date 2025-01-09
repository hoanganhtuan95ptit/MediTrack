package com.simple.meditrack.utils.exts

import java.text.DecimalFormat

fun Double.formatQuality() = DecimalFormat("#.##").format(this)
