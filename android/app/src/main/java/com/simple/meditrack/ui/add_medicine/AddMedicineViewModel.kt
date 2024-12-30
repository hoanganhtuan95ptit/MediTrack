package com.simple.meditrack.ui.add_medicine

import android.text.InputType
import android.text.style.ForegroundColorSpan
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.ai.english.ui.base.transition.TransitionViewModel
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.listenerSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.coreapp.utils.extentions.postValue
import com.simple.meditrack.Id
import com.simple.meditrack.R
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Medicine.Companion.toUnit
import com.simple.meditrack.ui.base.adapters.CheckboxViewItem
import com.simple.meditrack.ui.base.adapters.InputViewItem
import com.simple.meditrack.ui.base.adapters.TextViewItem
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.ui.view.Padding
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.meditrack.utils.exts.with

class AddMedicineViewModel : TransitionViewModel() {

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

        postDifferentValue(translate["title_add_medicine"].orEmpty().with(ForegroundColorSpan(theme.colorOnBackground)))
    }


    val medicineItem: LiveData<Alarm.MedicineItem> = MediatorLiveData()


    val unit: LiveData<Medicine.Unit> = combineSources<Medicine.Unit>(medicineItem) {

        val medicine = medicineItem.value ?: return@combineSources

        postValue(medicine.medicine.unit.toUnit())
    }.apply {

        postValue(Medicine.Unit.TABLET)
    }

    @VisibleForTesting
    val isLowOnMedication: LiveData<Boolean> = combineSources<Boolean>(medicineItem) {

        val medicine = medicineItem.value ?: return@combineSources

        postValue(medicine.medicine.quantity != Medicine.UNLIMITED)
    }.apply {

        postValue(false)
    }

    val viewItemList: LiveData<List<ViewItem>> = listenerSources(unit, theme, translate, medicineItem, isLowOnMedication) {

        val unit = unit.value ?: return@listenerSources
        val theme = theme.value ?: return@listenerSources
        val translate = translate.value ?: return@listenerSources
        val isLowOnMedication = isLowOnMedication.value ?: return@listenerSources

        val medicine = medicineItem.value

        val list = arrayListOf<ViewItem>()

        InputViewItem(
            id = Id.NAME,
            hint = translate["Nhập tên thuốc"].orEmpty(),
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NAME }?.text?.toString() ?: medicine?.medicine?.name.orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 16
            )
        ).let {

            list.add(TextViewItem(id = "TITLE_" + Id.NAME, text = translate["Tên thuốc (*)"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        TextViewItem(
            id = Id.UNIT,
            data = unit,
            text = translate[unit.name].orEmpty(),
            image = TextViewItem.Image(
                end = R.drawable.ic_arrow_right_24dp
            ),
            padding = Padding(
                left = DP.DP_16,
                top = DP.DP_16,
                right = DP.DP_16,
                bottom = DP.DP_16
            ),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 16
            ),
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.UNIT, text = translate["Loại thuốc (*)"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        InputViewItem(
            id = Id.DOSAGE,
            hint = translate["Nhập liều lượng dùng"].orEmpty(),
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.DOSAGE }?.text?.toString() ?: medicine?.dosage?.toString().orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.DOSAGE, text = translate["Liều lượng dùng (*)"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        InputViewItem(
            id = Id.NOTE,
            hint = translate["Nhập ghi chú"].orEmpty(),
            inputType = InputType.TYPE_CLASS_TEXT,
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NOTE }?.text?.toString()?: medicine?.medicine?.note.orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.NOTE, text = translate["Ghi chú"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        CheckboxViewItem(
            id = "CHECK_${Id.QUANTITY}",
            text = translate["Nhận thông báo khi gần hết thuốc"].orEmpty(),
            image = if (isLowOnMedication) {
                R.drawable.ic_tick_circle_24dp
            } else {
                R.drawable.ic_tick_24dp
            }
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(it)
        }

        if (isLowOnMedication) InputViewItem(
            id = Id.QUANTITY,
            hint = translate["Nhập số lượng thuốc"].orEmpty(),
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.QUANTITY }?.text?.toString()?: medicine?.medicine?.quantity?.toString().orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = 1,
                cornerRadius = 16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.QUANTITY, text = translate["Số lượng (*)"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
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
        val isLowOnMedication = isLowOnMedication.value ?: false

        val texts = viewItemList.filterIsInstance<TextViewItem>()
        val inputs = viewItemList.filterIsInstance<InputViewItem>()

        val name = inputs.find { it.id == Id.NAME }?.text
        val dosage = inputs.find { it.id == Id.DOSAGE }?.text
        val quantity = inputs.find { it.id == Id.QUANTITY }?.text.toString().toDoubleOrNull() ?: Medicine.UNLIMITED


        val isNameBlank = name.isNullOrBlank()
        val isDosageBlank = dosage.isNullOrBlank()
        val isQuantityBlank = isLowOnMedication && quantity <= 0.0


        val isClicked = !isNameBlank && !isDosageBlank && !isQuantityBlank

        val info = ButtonInfo(
            title = if (isNameBlank) {
                translate["Vui lòng nhập tên thuốc"].orEmpty()
            } else if (isDosageBlank) {
                translate["Vui lòng nhập liều lượng dùng"].orEmpty()
            } else if (isQuantityBlank) {
                translate["Vui lòng nhập số lượng thuốc"].orEmpty()
            } else {
                translate["Thêm thuốc"].orEmpty()
            },
            isClicked = isClicked,
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

    fun updateUnit(unit: Medicine.Unit) {

        this.unit.postDifferentValue(unit)
    }

    fun updateMedicine(it: Alarm.MedicineItem) {

        this.medicineItem.postDifferentValue(it)
    }

    fun refreshButtonInfo() {

        refreshButtonInfo.postDifferentValue(System.currentTimeMillis())
    }

    fun switchLowOnMedication() {

        isLowOnMedication.postDifferentValue(!(isLowOnMedication.value ?: false))
    }

    data class ButtonInfo(
        val title: String,
        val isClicked: Boolean,
        val background: Background,
    )
}