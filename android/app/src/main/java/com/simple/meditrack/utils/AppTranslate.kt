package com.simple.meditrack.utils

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow


val appTranslate by lazy {

    MutableSharedFlow<Map<String, String>>(replay = 1, extraBufferCapacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND).apply {

        val map = KeyTranslateMap()

        map[""]

        tryEmit(map)
    }
}


private class KeyTranslateMap : HashMap<String, String>() {

    override fun get(key: String): String {
        return super.get(key) ?: key
    }
}
