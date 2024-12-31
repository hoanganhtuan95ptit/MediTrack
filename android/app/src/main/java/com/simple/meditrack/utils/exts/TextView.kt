package com.simple.meditrack.utils.exts

import android.util.TypedValue
import android.widget.TextView
import com.simple.meditrack.ui.view.TextStyle

fun TextView.setTextStyle(textStyle: TextStyle? = null) {

    textStyle ?: return

    textStyle.typeface?.let {
        this.setTypeface(null, it)
    }

    textStyle.textSize?.let {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
    }

    textStyle.textGravity?.let {
        this.gravity = it
    }
}