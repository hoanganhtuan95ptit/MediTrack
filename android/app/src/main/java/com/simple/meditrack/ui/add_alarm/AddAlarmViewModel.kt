package com.simple.meditrack.ui.add_alarm

import android.text.InputType
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.ai.english.ui.base.transition.TransitionViewModel
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.meditrack.Id
import com.simple.meditrack.R
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
import java.text.DecimalFormat

class AddAlarmViewModel : TransitionViewModel() {

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
            hint = translate["Nhập ghí chú"].orEmpty(),
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
                text = it.value.medicine.name,
                description = it.value.dosage.toString() + " " + translate[it.value.medicine.unit.toUnit()?.name.orEmpty()].orEmpty() + " " + it.value.medicine.note,
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


    @VisibleForTesting
    val refreshButtonInfo: LiveData<Long> = MediatorLiveData(0)

    val buttonInfo: LiveData<ButtonInfo> = combineSources(theme, translate, viewItemList, refreshButtonInfo) {

        val theme = theme.value ?: return@combineSources
        val translate = translate.getOrEmpty()
        val viewItemList = viewItemList.getOrEmpty()

        val texts = viewItemList.filterIsInstance<TextViewItem>()
        val inputs = viewItemList.filterIsInstance<InputViewItem>()

        val name = inputs.find { it.id == Id.NAME }?.text
//
//
//        val isNameBlank = name.isNullOrBlank()
//        val isDosageBlank = dosage.isNullOrBlank()
//        val isQuantityBlank = isLowOnMedication && quantity <= 0.0
//
//
//        val isClicked = !isNameBlank && !isDosageBlank && !isQuantityBlank
//        Log.d("tuanha", "quantity:$quantity isQuantityBlank:$isQuantityBlank isClicked:$isClicked")
//
//        val info = ButtonInfo(
//            title = if (isNameBlank) {
//                translate["Vui lòng nhập tên thuốc"].orEmpty()
//            } else if (isDosageBlank) {
//                translate["Vui lòng nhập liều lượng dùng"].orEmpty()
//            } else if (isQuantityBlank) {
//                translate["Vui lòng nhập số lượng thuốc"].orEmpty()
//            } else {
//                translate["Thêm thuốc"].orEmpty()
//            },
//            isClicked = isClicked,
//            background = Background(
//                backgroundColor = if (isClicked) {
//                    theme.colorPrimary
//                } else {
//                    theme.colorBackgroundVariant
//                }
//            )
//        )
//
//        postDifferentValueIfActive(info)
    }

    fun updateUnit(unit: Medicine.Unit) {

//        this.unit.postDifferentValue(unit)
    }

    fun refreshButtonInfo() {

        refreshButtonInfo.postDifferentValue(System.currentTimeMillis())
    }

    fun switchLowOnMedication() {

//        isLowOnMedication.postDifferentValue(!(isLowOnMedication.value ?: false))
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

    data class ButtonInfo(
        val title: String,
        val isClicked: Boolean,
        val background: Background,
    )
}