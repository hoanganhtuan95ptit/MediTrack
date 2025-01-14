package com.simple.meditrack.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModels.BaseViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.meditrack.domain.usecases.GetKeyTranslateAsyncUseCase
import com.simple.meditrack.utils.appTranslate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn

class MainViewModel(
    private val getKeyTranslateAsyncUseCase: GetKeyTranslateAsyncUseCase
) : BaseViewModel() {

    @VisibleForTesting
    val keyTranslateSync: LiveData<Map<String, String>> = mediatorLiveData {

        getKeyTranslateAsyncUseCase.execute().collect {

            appTranslate.tryEmit(it)
        }
    }


    @VisibleForTesting
    val bottomStatus = MutableLiveData<Map<String, BottomStatus>>(hashMapOf())

    val bottomInfo = combineSources<BottomInfo>(bottomStatus) {

        val info = BottomInfo(
            isShow = bottomStatus.value.orEmpty().any { it.value == BottomStatus.SHOW }
        )

        delay(100)

        postDifferentValueIfActive(info)
    }

    init {

        keyTranslateSync.asFlow().launchIn(viewModelScope)
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