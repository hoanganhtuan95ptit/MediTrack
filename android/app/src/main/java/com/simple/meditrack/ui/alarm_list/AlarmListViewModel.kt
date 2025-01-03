package com.simple.meditrack.ui.alarm_list

import android.text.style.ForegroundColorSpan
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.Event
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.toEvent
import com.simple.meditrack.domain.usecases.alarm.GetListAlarmAsyncUseCase
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.alarm_list.adapters.AlarmViewItem
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.meditrack.utils.exts.with
import com.simple.state.ResultState
import java.text.DecimalFormat

class AlarmListViewModel(
    private val getListAlarmAsyncUseCase: GetListAlarmAsyncUseCase
) : TransitionViewModel() {

    private val loading = listOf<ViewItem>()

    @VisibleForTesting
    val theme: LiveData<AppTheme> = mediatorLiveData {

        appTheme.collect {

            postDifferentValue(it)
        }
    }

    @VisibleForTesting
    val translate: LiveData<Map<String, String>> = mediatorLiveData {

        appTranslate.collect {

            postDifferentValue(it)
        }
    }

    val alarmState = mediatorLiveData<ResultState<List<Alarm>>> {

        postValue(ResultState.Start)

        getListAlarmAsyncUseCase.execute().collect {

            postValue(ResultState.Success(it))
        }
    }

    val alarmViewItem: LiveData<List<ViewItem>> = combineSources(theme, alarmState) {

        val theme = theme.value ?: return@combineSources
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

            val hour = DecimalFormat("00").format(it.hour)

            AlarmViewItem(
                id = it.id,
                data = it,
                name = it.name,
                image = it.image,
                description = it.note,
                time = "$hour:${DecimalFormat("00").format(it.minute)}"
            )
        }.let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.addAll(it)
        }

        postDifferentValue(list)
    }

    val alarmViewItemEvent: LiveData<Event<List<ViewItem>>> = alarmViewItem.toEvent()
}