package com.simple.meditrack.ui.notification

import android.text.style.ForegroundColorSpan
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.ai.english.ui.base.transition.TransitionViewModel
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.meditrack.R
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.MedicineNotification
import com.simple.meditrack.ui.notification.adapters.MedicineViewItem
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.exts.with
import com.simple.state.ResultState
import com.simple.state.toSuccess
import kotlinx.coroutines.delay

class NotificationViewModel : TransitionViewModel() {

    val theme: LiveData<AppTheme> = mediatorLiveData {

        appTheme.collect {

            postDifferentValue(it)
        }
    }

    @VisibleForTesting
    val notificationState: LiveData<ResultState<MedicineNotification>> = mediatorLiveData {

        postValue(ResultState.Start)

        delay(500)

        postDifferentValue(ResultState.Success(notificationFake))
    }


    val titleInfo: LiveData<TitleInfo> = combineSources(notificationState) {

        notificationState.value?.toSuccess()?.data?.let {

            TitleInfo(
                note = it.note,
                title = it.name,
                image = R.drawable.img_reminder
            )
        }?.let {

            postDifferentValue(it)
        }
    }

    @VisibleForTesting
    val medicineSelected: LiveData<Map<String, MedicineState>> = combineSources(notificationState) {

        val state = notificationState.get()

        if (state !is ResultState.Success) {

            postValue(emptyMap())
        }

        state.toSuccess()?.data?.item.orEmpty().map {

            it.medicine.id to MedicineState.NONE
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

    val viewItemList: LiveData<List<ViewItem>> = combineSources(theme, actionState, medicineSelected, notificationState) {

        val theme = theme.get()
        val state = notificationState.get()
        val actionState = actionState.get()

        if (state !is ResultState.Success) {

            return@combineSources
        }

        val data = state.data
        val medicineSelectedMap = medicineSelected.value ?: return@combineSources

        val list = arrayListOf<ViewItem>()

        list.add(SpaceViewItem(height = DP.DP_16))

        data.item.map { item ->

            MedicineViewItem(
                id = item.medicine.id,

                name = item.medicine.name.with(ForegroundColorSpan(theme.colorOnBackground)),
                desciption = if (item.note.isNotBlank()) {
                    (item.dosage + " - " + item.note).with(item.dosage, ForegroundColorSpan(theme.colorOnBackground)).with(item.note, ForegroundColorSpan(theme.colorOnBackgroundVariant))
                } else {
                    item.dosage.with(ForegroundColorSpan(theme.colorOnBackground))

                },

                actionRes = if (medicineSelectedMap[item.medicine.id] == MedicineState.FOCUS) {
                    R.drawable.ic_tick_circle_24dp
                } else {
                    R.drawable.ic_tick_24dp
                },
                actionShow = if (actionState != ActionState.NOT_VIEW) {
                    true
                } else {
                    false
                },

                backgroundColor = if (medicineSelectedMap[item.medicine.id] == MedicineState.FOCUS) {
                    theme.colorAccent
                } else {
                    theme.colorDivider
                }
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

    fun nextAction() {

        val currentAction = actionState.value ?: ActionState.NOT_VIEW

        val nextAction = if (currentAction == ActionState.CAN_DONE) {
            ActionState.DONE
        } else {
            ActionState.VIEWED
        }

        actionState.postDifferentValue(nextAction)
    }

    fun updateSelected(id: String) {

        val currentAction = actionState.value ?: ActionState.NOT_VIEW

        if (currentAction != ActionState.VIEWED) {
            return
        }

        val map = medicineSelected.value.orEmpty().toMutableMap()

        map[id] = MedicineState.FOCUS

        medicineSelected.postDifferentValue(map)
    }

    data class TitleInfo(
        val note: String,
        val title: String,
        val image: Int
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

    private val notificationFake = MedicineNotification(

        note = "Cố gắng để khỏi bệnh",
        name = "Uống thuốc buổi tối",
        image = "",

        time = "8",
        step = 1,

        item = listOf(
            MedicineNotification.MedicineItem(
                note = "Nhai nuốt",
                dosage = "1/2 viên",
                medicine = Medicine(
                    id = "1",
                    name = "Viên vàng tròn",
                    image = ""
                )
            ),
            MedicineNotification.MedicineItem(
                note = "",
                dosage = "1.5 viên",
                medicine = Medicine(
                    id = "2",
                    name = "Viên trong vỉ",
                    image = ""
                )
            ),
            MedicineNotification.MedicineItem(
                note = "",
                dosage = "1 viên",
                medicine = Medicine(
                    id = "3",
                    name = "Viên trắng tròn",
                    image = ""
                )
            )
        )
    )
}