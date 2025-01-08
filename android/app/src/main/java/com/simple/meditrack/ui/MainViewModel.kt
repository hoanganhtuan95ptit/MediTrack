package com.simple.meditrack.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModels.BaseViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import kotlinx.coroutines.delay

class MainViewModel : BaseViewModel() {

    @VisibleForTesting
    val bottomStatus = MutableLiveData<Map<String, BottomStatus>>(hashMapOf())

    val bottomInfo = combineSources<BottomInfo>(bottomStatus) {

        val info = BottomInfo(
            isShow = bottomStatus.value.orEmpty().any { it.value == BottomStatus.SHOW }
        )

        delay(100)

        postDifferentValueIfActive(info)
    }

    fun updateBottomStatus(id: String, status: BottomStatus) {

        val map = bottomStatus.value.orEmpty().toMutableMap()

        map[id] = status

        bottomStatus.postDifferentValue(map)
    }

    data class BottomInfo(
        val isShow: Boolean
    )

    enum class BottomStatus {

        SHOW, HIDE
    }
}