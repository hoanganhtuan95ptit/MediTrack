package com.simple.meditrack.ui.medicine_list

import android.text.style.ForegroundColorSpan
import android.util.Log
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
import com.simple.meditrack.domain.usecases.medicine.GetListMedicineAsyncUseCase
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Medicine.Companion.toUnit
import com.simple.meditrack.ui.alarm_list.AlarmListViewModel
import com.simple.meditrack.ui.base.adapters.EmptyViewItem
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.ui.medicine_list.adapters.MedicineViewItem
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.meditrack.utils.exts.formatQuality
import com.simple.meditrack.utils.exts.with
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

    val screenInfo: LiveData<ScreenInfo> = combineSources(theme, translate) {

        val translate = translate.getOrEmpty()

        val info = ScreenInfo(
            header = translate["title_screen_list_medicine"].orEmpty(),
            action = translate["action_add_medicine"].orEmpty()
        )

        postDifferentValue(info)
    }


    @VisibleForTesting
    val medicineState = mediatorLiveData<ResultState<List<Medicine>>> {

        postValue(ResultState.Start)

        getListMedicineAsyncUseCase.execute().collect {

            postValue(ResultState.Success(it))
        }
    }

    @VisibleForTesting
    val medicineViewItem: LiveData<List<ViewItem>> = combineSources(theme, translate, medicineState) {

        val theme = theme.value ?: return@combineSources
        val translate = translate.value ?: return@combineSources

        val state = medicineState.value ?: return@combineSources

        if (state is ResultState.Start) {

            return@combineSources
        }

        if (state !is ResultState.Success) {

            return@combineSources
        }

        val list = arrayListOf<ViewItem>()

        state.data.sortedBy {

            it.createTime
        }.sortedBy {

            it.countForNextDays.keys.lastOrNull()
        }.map { medicine ->

            medicine.toViewItem(theme, translate)
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

    val medicineViewItemEvent: LiveData<Event<List<ViewItem>>> = medicineViewItem.toEvent()


    private fun Medicine.toViewItem(theme: AppTheme, translate: Map<String, String>): ViewItem {

        val descriptionList = arrayListOf<Pair<String, Int>>()


        (if (quantity == Medicine.UNLIMITED) {
            translate["quality_unlimited"].orEmpty().replace("\$unit", translate["unit_" + unit.toUnit()?.name.orEmpty().lowercase()].orEmpty())
        } else if (quantity <= 0) {
            null
        } else {
            translate["quality_number"].orEmpty().replace("\$quality", quantity.formatQuality()).replace("\$unit", translate["unit_" + unit.toUnit()?.name.orEmpty().lowercase()].orEmpty())
        })?.let {
            descriptionList.add(Pair(it, theme.colorOnSurfaceVariant))
        }

        note.ifBlank {
            null
        }?.let {
            descriptionList.add(Pair(it, theme.colorOnSurfaceVariant))
        }

        val expiresInDays = countForNextDays.keys.lastOrNull() ?: 0

        (if (quantity != Medicine.UNLIMITED && quantity <= 0) {
            Pair(translate["message_quality_is_out"].orEmpty(), theme.colorError)
        } else if (expiresInDays <= 0) {
            Pair(translate["message_quality_is_only_enough_for_today"].orEmpty(), theme.colorError)
        } else if (expiresInDays <= 3) {
            Pair(translate["message_quality_is_only_enough_for_x_day"].orEmpty().replace("\$day", "$expiresInDays"), theme.colorError)
        } else {
            Pair(translate["message_quality_is_enough_for_x_day"].orEmpty().replace("\$day", "$expiresInDays"), theme.colorPrimary)
        }).let {
            descriptionList.add(it)
        }

        val description = descriptionList.let { pairs ->

            val text = pairs.joinToString(separator = " - ") {
                it.first
            }

            var charSequence: CharSequence = text

            pairs.forEach {
                charSequence = charSequence.with(it.first, ForegroundColorSpan(it.second))
            }

            charSequence
        }

        return MedicineViewItem(
            id = id,

            image = image,

            name = name,
            description = description,
        )
    }

    data class ScreenInfo(
        val header: CharSequence,
        val action: CharSequence
    )
}