package com.simple.meditrack.ui.alarm_list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.Event
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.toEvent
import com.simple.meditrack.R
import com.simple.meditrack.domain.usecases.alarm.GetListAlarmAsyncUseCase
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.alarm_list.adapters.AlarmViewItem
import com.simple.meditrack.ui.base.adapters.EmptyViewItem
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.meditrack.utils.exts.formatTime
import com.simple.state.ResultState

class AlarmListViewModel(
    private val getListAlarmAsyncUseCase: GetListAlarmAsyncUseCase
) : TransitionViewModel() {

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


    val screenInfo: LiveData<ScreenInfo> = combineSources(theme, translate) {

        val translate = translate.getOrEmpty()

        val info = ScreenInfo(
            header = translate["title_screen_list_alarm"].orEmpty(),
            action = translate["action_add_alarm"].orEmpty()
        )

        postDifferentValue(info)
    }


    val alarmState = mediatorLiveData<ResultState<List<Alarm>>> {

        postValue(ResultState.Start)

        getListAlarmAsyncUseCase.execute().collect {

            postValue(ResultState.Success(it))
        }
    }

    @VisibleForTesting
    val alarmViewItem: LiveData<List<ViewItem>> = combineSources(theme, alarmState) {

        val theme = theme.value ?: return@combineSources
        val state = alarmState.value ?: return@combineSources

        if (state is ResultState.Start) {

            return@combineSources
        }

        if (state !is ResultState.Success) {

            return@combineSources
        }

        val list = arrayListOf<ViewItem>()

        state.data.map {

            AlarmViewItem(
                id = it.id,
                data = it,
                name = it.name,
                image = it.image,
                description = it.note,
                time = "${it.hour.formatTime()}:${it.minute.formatTime()}"
            )
        }.takeIf {

            it.isNotEmpty()
        }?.let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.addAll(it)
            list.add(SpaceViewItem(height = DP.DP_350))
        }

        if (list.isEmpty()) EmptyViewItem(

            imageRes = R.raw.anim_empty
        ).let {

            list.add(it)
        }

        postDifferentValue(list)
    }

    val alarmViewItemEvent: LiveData<Event<List<ViewItem>>> = alarmViewItem.toEvent()

    data class ScreenInfo(
        val header: CharSequence,
        val action: CharSequence
    )
}