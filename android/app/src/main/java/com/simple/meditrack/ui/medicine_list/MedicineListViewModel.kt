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
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.toEvent
import com.simple.meditrack.R
import com.simple.meditrack.domain.usecases.medicine.GetListMedicineAsyncUseCase
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Medicine.Companion.toUnit
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
        }.map {


            val expiresInDays = it.countForNextDays.keys.lastOrNull() ?: 0

            val warning = if (expiresInDays <= 0) {
                Pair(translate["Thuốc này chỉ đủ dùng trong hôm nay"].orEmpty(), theme.colorError)
            } else if (expiresInDays <= 3) {
                Pair(translate["Thuốc này chỉ đủ dùng trong $expiresInDays ngày nữa"].orEmpty(), theme.colorError)
            } else {
                Pair(translate["Thuốc này đủ dùng trong $expiresInDays ngày nữa"].orEmpty(), theme.colorPrimary)
            }

            val note = if (it.note.isBlank() && it.quantity == Medicine.UNLIMITED) {
                translate["Không giới hạn"] + " " + translate[it.unit.toUnit()?.name.orEmpty()].orEmpty()
            } else if (it.note.isBlank() && it.quantity != Medicine.UNLIMITED) {
                translate["Còn"] + " " + it.quantity.formatQuality() + " " + translate[it.unit.toUnit()?.name.orEmpty()].orEmpty()
            } else if (it.note.isNotBlank() && it.quantity == Medicine.UNLIMITED) {
                it.note + " - " + translate["Không giới hạn"] + " " + translate[it.unit.toUnit()?.name.orEmpty()].orEmpty()
            } else if (it.note.isNotBlank() && it.quantity != Medicine.UNLIMITED) {
                it.note + " - " + translate["Còn"] + " " + it.quantity.formatQuality() + " " + translate[it.unit.toUnit()?.name.orEmpty()].orEmpty()
            } else {
                ""
            }

            val description = if (note.isBlank() && warning.first.isBlank()) {
                ""
            } else if (note.isBlank() && warning.first.isNotBlank()) {
                warning.first.with(ForegroundColorSpan(warning.second))
            } else if (warning.first.isBlank()) {
                note
            } else {
                (note + " - " + warning.first).with(warning.first, ForegroundColorSpan(warning.second))
            }


            MedicineViewItem(
                id = it.id,

                image = it.image,

                name = it.name,
                description = description,
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

    val medicineViewItemEvent: LiveData<Event<List<ViewItem>>> = medicineViewItem.toEvent()
}