package com.simple.meditrack.utils.exts

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.extentions.Event
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> LiveData<Event<T>>.launchCollect(
    lifecycleOwner: LifecycleOwner,

    start: CoroutineStart = CoroutineStart.DEFAULT,
    context: CoroutineContext = EmptyCoroutineContext,

    collector: suspend (data: T, anim: Boolean) -> Unit
) {

    lifecycleOwner.lifecycleScope.launch(start = start, context = context) {

        val data = value?.getContentIfNotHandled() ?: value?.peekContent() ?: return@launch

        collector(data, false)
    }

    asFlow().launchCollect(lifecycleOwner = lifecycleOwner, start = start, context = context) {

        val data = value?.getContentIfNotHandled() ?: return@launchCollect

        collector(data, true)
    }
}