package com.simple.meditrack.ui.medicine_list

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.Event
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.toEvent
import com.simple.meditrack.R
import com.simple.meditrack.domain.usecases.medicine.GetListMedicineAsyncUseCase
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.ui.alarm_list.adapters.AlarmViewItem
import com.simple.meditrack.ui.base.adapters.EmptyViewItem
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.state.ResultState
import java.text.DecimalFormat

class MedicineListViewModel(
    private val getListMedicineAsyncUseCase: GetListMedicineAsyncUseCase
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

    val medicineState = mediatorLiveData<ResultState<List<Medicine>>> {

        postValue(ResultState.Start)

        getListMedicineAsyncUseCase.execute().collect {

            postValue(ResultState.Success(it))
        }
    }

    val medicineViewItem: LiveData<List<ViewItem>> = combineSources(theme, medicineState) {

        val theme = theme.value ?: return@combineSources
        val state = medicineState.value ?: return@combineSources

        if (state is ResultState.Start) {

            return@combineSources
        }

        if (state !is ResultState.Success) {

            return@combineSources
        }

        val list = arrayListOf<ViewItem>()

//        state.data.map {
//
//            val hour = DecimalFormat("00").format(it.hour)
//
//            AlarmViewItem(
//                id = it.id,
//                data = it,
//                name = it.name,
//                image = it.image,
//                description = it.note,
//                time = "$hour:${DecimalFormat("00").format(it.minute)}"
//            )
//        }.takeIf {
//
//            it.isNotEmpty()
//        }?.let {
//
//            list.add(SpaceViewItem(height = DP.DP_16))
//            list.addAll(it)
//            list.add(SpaceViewItem(height = DP.DP_100))
//        }

        if (list.isEmpty()) EmptyViewItem(

            imageRes = R.raw.anim_empty
        ).let {

            list.add(it)
        }

        postDifferentValue(list)
    }

    val medicineViewItemEvent: LiveData<Event<List<ViewItem>>> = medicineViewItem.toEvent()
}