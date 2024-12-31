package com.simple.meditrack.ui.alarm_list

import androidx.lifecycle.LiveData
import com.simple.adapter.entities.ViewItem
import com.simple.ai.english.ui.base.transition.TransitionViewModel
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.meditrack.domain.usecases.alarm.GetListAlarmAsyncUseCase
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.alarm_list.adapters.AlarmViewItem
import com.simple.state.ResultState
import java.text.DecimalFormat

class AlarmListViewModel(
    private val getListAlarmAsyncUseCase: GetListAlarmAsyncUseCase
) : TransitionViewModel() {

    private val loading = listOf<ViewItem>()

    val alarmState = mediatorLiveData<ResultState<List<Alarm>>> {

        postValue(ResultState.Start)

        getListAlarmAsyncUseCase.execute().collect {

            postValue(ResultState.Success(it))
        }
    }

    val alarmViewItem: LiveData<List<ViewItem>> = combineSources(alarmState) {

        val state = alarmState.value ?: return@combineSources

        if (state is ResultState.Start) {

//            postDifferentValue(loading)
            return@combineSources
        }

        if (state !is ResultState.Success) {

            return@combineSources
        }

        val list = arrayListOf<ViewItem>()

        state.data.map {

            AlarmViewItem(
                data = it,
                name = it.name,
                image = it.image,
                description = it.note,
                time = "${DecimalFormat("00").format(it.hour)}:${DecimalFormat("00").format(it.minute)}"
            )
        }.let {

            list.addAll(it)
        }

        postDifferentValue(list)
    }
}