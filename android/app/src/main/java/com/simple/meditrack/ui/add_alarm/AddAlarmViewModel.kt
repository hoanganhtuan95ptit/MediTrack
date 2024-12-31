package com.simple.meditrack.ui.add_alarm

import android.text.InputType
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.ai.english.ui.base.transition.TransitionViewModel
import com.simple.core.utils.extentions.orZero
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.ext.handler
import com.simple.coreapp.utils.extentions.Event
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.listenerSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.coreapp.utils.extentions.toEvent
import com.simple.meditrack.Id
import com.simple.meditrack.R
import com.simple.meditrack.domain.usecases.alarm.GetAlarmByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.InsertOrUpdateAlarmUseCase
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Medicine.Companion.toUnit
import com.simple.meditrack.ui.add_alarm.adapters.AlarmMedicineViewItem
import com.simple.meditrack.ui.add_alarm.adapters.AlarmTimeViewItem
import com.simple.meditrack.ui.base.adapters.InputViewItem
import com.simple.meditrack.ui.base.adapters.TextViewItem
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.ui.view.Padding
import com.simple.meditrack.ui.view.Size
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.meditrack.utils.exts.with
import com.simple.state.ResultState
import com.simple.state.isStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.UUID

class AddAlarmViewModel(
    private val getAlarmByIdAsyncUseCase: GetAlarmByIdAsyncUseCase,
    private val insertOrUpdateAlarmUseCase: InsertOrUpdateAlarmUseCase
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

    val title: LiveData<CharSequence> = combineSources(theme, translate) {

        val theme = theme.value ?: return@combineSources
        val translate = translate.value ?: return@combineSources

        postDifferentValue(translate["title_add_alarm"].orEmpty().with(ForegroundColorSpan(theme.colorOnBackground)))
    }

    val alarmId: LiveData<String> = MediatorLiveData()

    val alarm: LiveData<Alarm> = combineSources(alarmId) {

        getAlarmByIdAsyncUseCase.execute(GetAlarmByIdAsyncUseCase.Param(id = alarmId.value ?: return@combineSources)).collect {

            postDifferentValue(it)
        }
    }

    val hour: LiveData<Int> = MediatorLiveData(0)

    val minute: LiveData<Int> = MediatorLiveData(0)

    val image: LiveData<String> = MediatorLiveData("https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_0.png")

    val medicineMap: LiveData<Map<String, Alarm.MedicineItem>> = MediatorLiveData(hashMapOf())

    val viewItemList: LiveData<List<ViewItem>> = combineSources(theme, translate, hour, minute, image, medicineMap) {

        val theme = theme.value ?: return@combineSources
        val translate = translate.value ?: return@combineSources

        val hour = hour.value ?: return@combineSources
        val minute = minute.value ?: return@combineSources
        val medicineMap = medicineMap.value ?: return@combineSources

        val list = arrayListOf<ViewItem>()

        AlarmTimeViewItem(
            id = Id.TIME,
            time = "${DecimalFormat("00").format(hour)}:${DecimalFormat("00").format(minute)}".with(ForegroundColorSpan(theme.colorOnSurface)),
            image = image.value.orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 8
            ),
        ).let {

            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        InputViewItem(
            id = Id.NAME,
            hint = translate["Nhập tên thông báo"].orEmpty(),
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NAME }?.text?.toString().orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 8
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.NAME, text = translate["Tên thông báo (*)"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        InputViewItem(
            id = Id.NOTE,
            hint = translate["Nhập ghi chú"].orEmpty(),
            inputType = InputType.TYPE_CLASS_TEXT,
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NOTE }?.text?.toString().orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 8
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.NOTE, text = translate["Ghi chú"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        list.add(SpaceViewItem(height = DP.DP_16))
        list.add(TextViewItem(id = "TITLE_" + Id.MEDICINE, text = translate["Thuốc (*)"].orEmpty()))
        list.add(SpaceViewItem(height = DP.DP_8))

        medicineMap.map {

            AlarmMedicineViewItem(
                id = it.key,
                data = it.value,
                text = it.value.medicine?.name.orEmpty(),
                description = it.value.dosage.toString() + " " + translate[it.value.medicine?.unit?.toUnit()?.name.orEmpty()].orEmpty() + " " + it.value.medicine?.note.orEmpty(),
                background = Background(
                    cornerRadius = 8
                )
            )
        }.apply {

            list.addAll(this)
        }

        TextViewItem(
            id = Id.ADD_MEDICINE,
            text = translate["Thêm thuốc"].orEmpty(),
            image = TextViewItem.Image(
                end = R.drawable.ic_add_circle_24dp
            ),
            size = Size(
                width = ViewGroup.LayoutParams.WRAP_CONTENT,
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            ),
            padding = Padding(
                top = DP.DP_8,
                left = DP.DP_16,
                right = DP.DP_16,
                bottom = DP.DP_8
            ),
            background = Background(
                backgroundColor = theme.colorPrimaryVariant,
                cornerRadius = 8
            )
        ).let {

            list.add(it)
        }

        postDifferentValueIfActive(list)
    }

    val viewItemListEvent: LiveData<Event<List<ViewItem>>> = viewItemList.toEvent()


    @VisibleForTesting
    val refreshButtonInfo: LiveData<Long> = MediatorLiveData(0)

    val insertOrUpdateState: LiveData<ResultState<Alarm>> = MediatorLiveData()

    val buttonInfo: LiveData<ButtonInfo> = listenerSources(theme, translate, medicineMap, viewItemList, refreshButtonInfo, insertOrUpdateState) {

        val theme = theme.value ?: return@listenerSources

        val translate = translate.getOrEmpty()
        val medicineMap = medicineMap.getOrEmpty()
        val viewItemList = viewItemList.getOrEmpty()

        val inputs = viewItemList.filterIsInstance<InputViewItem>()

        val name = inputs.find { it.id == Id.NAME }?.text


        val isNameBlank = name.isNullOrBlank()
        val isMedicineBlank = medicineMap.isEmpty()

        val isLoading = insertOrUpdateState.value.isStart()
        val isClicked = !isNameBlank && !isMedicineBlank && !isLoading

        val info = ButtonInfo(
            title = if (isNameBlank) {
                translate["Vui lòng nhập tên thông báo"].orEmpty()
            } else if (isMedicineBlank) {
                translate["Vui lòng thêm thuốc"].orEmpty()
            } else {
                translate["Thêm thông báo"].orEmpty()
            },
            isClicked = isClicked,
            isLoading = isLoading,
            background = Background(
                backgroundColor = if (isClicked) {
                    theme.colorPrimary
                } else {
                    theme.colorBackgroundVariant
                }
            )
        )

        postDifferentValueIfActive(info)
    }

    fun refreshButtonInfo() {

        refreshButtonInfo.postDifferentValue(System.currentTimeMillis())
    }

    fun updateTime(hour: Int, minute: Int) {

        this.hour.postDifferentValue(hour)
        this.minute.postDifferentValue(minute)
    }

    fun updateMedicine(medicine: Alarm.MedicineItem) {

        val map = medicineMap.value.orEmpty().toMutableMap()

        map[medicine.id] = medicine

        medicineMap.postDifferentValue(map)
    }

    fun removeMedicine(item: AlarmMedicineViewItem) {

        val map = medicineMap.value.orEmpty().toMutableMap()

        map.remove(item.id)

        medicineMap.postDifferentValue(map)
    }

    fun insertOrUpdateAlarm() = viewModelScope.launch(handler + Dispatchers.IO) {

        val viewItemList = viewItemList.getOrEmpty()

        val inputs = viewItemList.filterIsInstance<InputViewItem>()

        val alarm = Alarm(
            id = alarmId.value ?: UUID.randomUUID().toString(),
            note = inputs.find { it.id == Id.NOTE }?.text?.toString().orEmpty(),
            name = inputs.find { it.id == Id.NAME }?.text?.toString().orEmpty(),
            image = image.value.orEmpty(),
            hour = hour.value.orZero(),
            minute = minute.value.orZero(),
            item = medicineMap.value?.values?.toList().orEmpty()
        )

        insertOrUpdateState.postDifferentValue(ResultState.Start)

        runCatching {

            insertOrUpdateAlarmUseCase.execute(InsertOrUpdateAlarmUseCase.Param(alarm))

            insertOrUpdateState.postDifferentValue(ResultState.Success(alarm))
        }.getOrElse {

            insertOrUpdateState.postDifferentValue(ResultState.Failed(it))
        }
    }

    data class ButtonInfo(
        val title: String,
        val isClicked: Boolean,
        val isLoading: Boolean,
        val background: Background,
    )
}