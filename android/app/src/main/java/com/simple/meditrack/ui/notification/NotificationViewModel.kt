package com.simple.meditrack.ui.notification

import android.text.style.ForegroundColorSpan
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.ext.handler
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postValue
import com.simple.meditrack.R
import com.simple.meditrack.domain.usecases.alarm.CloseAlarmUseCase
import com.simple.meditrack.domain.usecases.alarm.GetAlarmByIdAsyncUseCase
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.ui.notification.adapters.NotificationMedicineViewItem
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.exts.with
import com.simple.state.ResultState
import com.simple.state.toSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val closeAlarmUseCase: CloseAlarmUseCase,
    private val getAlarmByIdAsyncUseCase: GetAlarmByIdAsyncUseCase
) : TransitionViewModel() {

    val id: LiveData<String> = mediatorLiveData { }

    val theme: LiveData<AppTheme> = mediatorLiveData {

        appTheme.collect {

            postDifferentValue(it)
        }
    }

    val alarmState: LiveData<ResultState<Alarm>> = combineSources(id) {

        postValue(ResultState.Start)

        getAlarmByIdAsyncUseCase.execute(GetAlarmByIdAsyncUseCase.Param(id.get())).collect {

            postDifferentValue(ResultState.Success(it!!))
        }
    }


    val titleInfo: LiveData<TitleInfo> = combineSources(alarmState) {

        alarmState.value?.toSuccess()?.data?.let {

            TitleInfo(
                note = it.note,
                title = it.name,
                image = it.image
            )
        }?.let {

            postDifferentValue(it)
        }
    }

    @VisibleForTesting
    val medicineSelected: LiveData<Map<String, MedicineState>> = combineSources(alarmState) {

        val state = alarmState.get()

        if (state !is ResultState.Success) {

            postValue(emptyMap())
        }

        state.toSuccess()?.data?.item.orEmpty().map {

            it.id to MedicineState.NONE
        }.toMap().let {

            postDifferentValue(it)
        }
    }

    val actionState: LiveData<ActionState> = combineSources(medicineSelected) {

        val medicineSelected = medicineSelected.value ?: return@combineSources

        if (value == null) {

            postValue(ActionState.NOT_VIEW)
        } else if (value == ActionState.VIEWED && medicineSelected.all { it.value == MedicineState.FOCUS }) {

            postValue(ActionState.CAN_DONE)
        }
    }

    val viewItemList: LiveData<List<ViewItem>> = combineSources(theme, actionState, medicineSelected, alarmState) {

        val theme = theme.get()
        val state = alarmState.get()
        val actionState = actionState.get()

        if (state !is ResultState.Success) {

            return@combineSources
        }

        val data = state.data
        val medicineSelectedMap = medicineSelected.value ?: return@combineSources

        val list = arrayListOf<ViewItem>()

        list.add(SpaceViewItem(height = DP.DP_16))

        data.item.map { item ->

            NotificationMedicineViewItem(
                id = item.id,
                data = item.medicine,

                name = item.medicine?.name.orEmpty().with(ForegroundColorSpan(theme.colorOnBackground)),
                desciption = if (item.medicine?.note.orEmpty().isNotBlank()) {
                    (item.dosage.toString() + " - " + item.medicine?.note.orEmpty()).with(item.dosage, ForegroundColorSpan(theme.colorOnBackground)).with(item.medicine?.note.orEmpty(), ForegroundColorSpan(theme.colorOnBackgroundVariant))
                } else {
                    item.dosage.toString().with(ForegroundColorSpan(theme.colorOnBackground))

                },

                actionRes = if (medicineSelectedMap[item.id] == MedicineState.FOCUS) {
                    R.drawable.ic_tick_circle_24dp
                } else {
                    R.drawable.ic_tick_24dp
                },
                actionShow = if (actionState != ActionState.NOT_VIEW) {
                    true
                } else {
                    false
                },

                background = Background(
                    strokeColor = if (medicineSelectedMap[item.id] == MedicineState.FOCUS) {
                        theme.colorAccent
                    } else {
                        theme.colorDivider
                    }
                )
            )
        }.let {

            list.addAll(it)
        }


        postDifferentValue(list)
    }

    val buttonInfo: LiveData<ButtonInfo> = combineSources(theme, actionState) {

        val theme = theme.get()
        val actionState = actionState.get()


        val title = if (actionState == ActionState.NOT_VIEW) {
            "Tôi đã nhìn thấy thông báo"
        } else if (actionState == ActionState.VIEWED) {
            "Vui lòng tích chọn các loại thuốc đã "
        } else {
            "Đã uống thuốc"
        }

        val info = ButtonInfo(
            title = (if (actionState == ActionState.VIEWED) {
                theme.colorOnBackgroundVariant
            } else {
                theme.colorOnPrimary
            }).let {
                title.with(ForegroundColorSpan(it))
            },
            isLocked = actionState == ActionState.VIEWED,
            completed = actionState == ActionState.DONE,
            outerColor = if (actionState == ActionState.VIEWED) {
                theme.colorBackgroundVariant
            } else {
                theme.colorPrimary
            }
        )

        postDifferentValue(info)
    }

    fun updateId(id: String) {

        this.id.postDifferentValue(id)
    }

    fun nextAction() = GlobalScope.launch(handler + Dispatchers.IO) {

        val currentAction = actionState.value ?: ActionState.NOT_VIEW

        val nextAction = if (currentAction == ActionState.CAN_DONE) {
            ActionState.DONE
        } else {
            ActionState.VIEWED
        }

        actionState.postDifferentValue(nextAction)

        if (nextAction == ActionState.DONE) {
            closeAlarmUseCase.execute(CloseAlarmUseCase.Param(id.value ?: return@launch))
        }
    }

    fun updateSelected(id: String) {

        val currentAction = actionState.value ?: ActionState.NOT_VIEW

        if (currentAction != ActionState.VIEWED) {
            return
        }

        val map = medicineSelected.value.orEmpty().toMutableMap()

        map[id] = MedicineState.FOCUS

        medicineSelected.postValue(map)
    }

    data class TitleInfo(
        val note: String,
        val title: String,
        val image: String
    )

    data class ButtonInfo(
        val title: CharSequence,
        val isLocked: Boolean,
        val completed: Boolean,
        val outerColor: Int
    )

    enum class ActionState {
        NOT_VIEW, VIEWED, CAN_DONE, DONE
    }

    enum class MedicineState {
        NONE, FOCUS
    }
}