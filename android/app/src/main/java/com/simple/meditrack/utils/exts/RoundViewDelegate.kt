package com.simple.meditrack.utils.exts

import com.simple.coreapp.ui.view.round.RoundViewDelegate
import com.simple.meditrack.ui.view.Background

fun RoundViewDelegate.setBackground(_background: Background? = null){

    val background = _background?: return

    background.backgroundColor?.let {
        this.backgroundColor = it
    }

    background.cornerRadius?.let {
        this.cornerRadius = it
    }

    background.cornerRadius_TL?.let {
        this.cornerRadius_TL = it
    }

    background.cornerRadius_TR?.let {
        this.cornerRadius_TR = it
    }

    background.cornerRadius_BL?.let {
        this.cornerRadius_BL = it
    }

    background.cornerRadius_BR?.let {
        this.cornerRadius_BR = it
    }

    background.strokeWidth?.let {
        this.strokeWidth = it
    }

    background.strokeColor?.let {
        this.strokeColor = it
    }

    background.strokeDashGap?.let {
        this.setStrokeDashGap(it)
    }

    background.strokeDashWidth?.let {
        this.setStrokeDashWidth(it)
    }
}