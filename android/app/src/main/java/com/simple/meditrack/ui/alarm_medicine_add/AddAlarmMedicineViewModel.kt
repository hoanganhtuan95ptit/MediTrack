package com.simple.meditrack.ui.alarm_medicine_add

import android.text.InputType
import android.text.style.ForegroundColorSpan
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
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
import com.simple.meditrack.domain.usecases.medicine.GetMedicineByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.medicine.SearchMedicineUseCase
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Medicine.Companion.toUnit
import com.simple.meditrack.ui.base.adapters.CheckboxViewItem
import com.simple.meditrack.ui.base.adapters.ImageViewItem
import com.simple.meditrack.ui.base.adapters.InputViewItem
import com.simple.meditrack.ui.base.adapters.TextViewItem
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.ui.notification.adapters.NotificationMedicineViewItem
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.ui.view.Padding
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.meditrack.utils.exts.formatQuality
import com.simple.meditrack.utils.exts.parseQuality
import com.simple.meditrack.utils.exts.with

class AddAlarmMedicineViewModel(
    private val searchMedicineUseCase: SearchMedicineUseCase,
    private val getMedicineByIdAsyncUseCase: GetMedicineByIdAsyncUseCase
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


    val medicineItem: LiveData<Alarm.MedicineItem> = MediatorLiveData()

    val medicineId: LiveData<String> = combineSources(medicineItem) {

        val medicineItem = medicineItem.value ?: return@combineSources

        postDifferentValue(medicineItem.medicineId)
    }


    val title: LiveData<CharSequence> = combineSources(theme, translate, medicineId) {

        val theme = theme.value ?: return@combineSources
        val translate = translate.value ?: return@combineSources

        val text = if (medicineId.value.isNullOrBlank()) {

            translate["title_screen_add_medicine_item"].orEmpty()
        } else {

            translate["title_screen_update_medicine_item"].orEmpty()
        }

        postDifferentValue(text.with(ForegroundColorSpan(theme.colorOnBackground)))
    }

    val medicine: LiveData<Medicine> = combineSources(medicineId) {

        getMedicineByIdAsyncUseCase.execute(GetMedicineByIdAsyncUseCase.Param(medicineId.value ?: return@combineSources)).collect {

            val medicine = it ?: Medicine(
                id = "",
                name = "",
                image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_0.png",
            )

            postValue(medicine)
        }
    }

    val name: LiveData<String> = combineSources(medicine) {

        postDifferentValue(medicine.value?.name.orEmpty())
    }

    val unit: LiveData<Medicine.Unit> = combineSources(medicine) {

        val medicine = medicine.value ?: return@combineSources

        postValue(medicine.unit.toUnit())
    }

    val image: LiveData<String> = combineSources(medicine) {

        val medicine = medicine.value ?: return@combineSources

        postValue(medicine.image)
    }

    @VisibleForTesting
    val isLowOnMedication: LiveData<Boolean> = combineSources(medicine) {

        val medicine = medicine.value ?: return@combineSources

        postValue(medicine.quantity != Medicine.UNLIMITED)
    }


    val medicineSearch: LiveData<List<Medicine>> = combineSources(name) {

        searchMedicineUseCase.execute(SearchMedicineUseCase.Param(name.value ?: return@combineSources)).collect {

            postValue(it)
        }
    }

    val isMedicineSearchEnable: LiveData<Boolean> = MediatorLiveData(false)


    val viewItemList: LiveData<List<ViewItem>> = listenerSources(theme, translate, unit, image, medicineItem, isLowOnMedication, medicineSearch, isMedicineSearchEnable) {

        val unit = unit.value ?: return@listenerSources
        val theme = theme.value ?: return@listenerSources
        val translate = translate.value ?: return@listenerSources
        val isLowOnMedication = isLowOnMedication.value ?: return@listenerSources

        val medicineItem = medicineItem.value
        val medicine = medicine.value

        val list = arrayListOf<ViewItem>()

        ImageViewItem(
            id = Id.IMAGE,
            image = image.getOrEmpty()
        ).let {

            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        val name = medicine?.name ?: value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NAME }?.text?.toString().orEmpty()

        InputViewItem(
            id = Id.NAME,
            hint = translate["hint_enter_medicine_item_name"].orEmpty(),
            text = name,
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            )
        ).let {

            list.add(TextViewItem(id = "TITLE_" + Id.NAME, text = translate["title_enter_medicine_item_name"].orEmpty().with("(✶)", ForegroundColorSpan(theme.colorError))))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        if (isMedicineSearchEnable.value == true) medicineSearch.value?.map {

            NotificationMedicineViewItem(
                id = it.id,
                data = it,

                name = it.name,
                desciption = it.note,

                background = Background(
                    strokeColor = if (it.name.trim().lowercase() == name.trim().lowercase()) {
                        theme.colorAccent
                    } else {
                        theme.colorDivider
                    }
                )
            )
        }?.let {

            list.addAll(it)
        }

        TextViewItem(
            id = Id.UNIT,
            data = unit,
            text = translate[unit.name].orEmpty(),
            image = TextViewItem.Image(
                end = R.drawable.ic_arrow_down_24dp
            ),
            padding = Padding(
                left = DP.DP_16,
                top = DP.DP_16,
                right = DP.DP_16,
                bottom = DP.DP_16
            ),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            ),
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.UNIT, text = translate["title_enter_medicine_item_unit"].orEmpty().with("(✶)", ForegroundColorSpan(theme.colorError))))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        InputViewItem(
            id = Id.DOSAGE,
            hint = translate["hint_enter_medicine_item_dosage"].orEmpty(),
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.DOSAGE }?.text?.toString() ?: medicineItem?.dosage?.formatQuality().orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.DOSAGE, text = translate["title_enter_medicine_item_dosage"].orEmpty().with("(✶)", ForegroundColorSpan(theme.colorError))))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        InputViewItem(
            id = Id.NOTE,
            hint = translate["hint_enter_medicine_item_note"].orEmpty(),
            inputType = InputType.TYPE_CLASS_TEXT,
            text = medicine?.note ?: value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NOTE }?.text?.toString().orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.NOTE, text = translate["title_enter_medicine_item_note"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        CheckboxViewItem(
            id = "CHECK_${Id.QUANTITY}",
            text = translate["message_receive_notification_medicine_item"].orEmpty(),
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
            hint = translate["hint_enter_medicine_item_quantity"].orEmpty(),
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            text = if (medicine != null && medicine.quantity == Medicine.UNLIMITED) {
                value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.QUANTITY }?.text?.toString().orEmpty()
            } else {
                medicine?.quantity?.formatQuality() ?: value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.QUANTITY }?.text?.toString().orEmpty()
            },
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.QUANTITY, text = translate["title_enter_medicine_item_quantity"].orEmpty().with("(✶)", ForegroundColorSpan(theme.colorError))))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        list.add(SpaceViewItem(height = DP.DP_350))

        postDifferentValueIfActive(list)
    }


    @VisibleForTesting
    val refreshButtonInfo: LiveData<Long> = MediatorLiveData(0)

    val buttonInfo: LiveData<ButtonInfo> = combineSources(theme, translate, medicineId, viewItemList, refreshButtonInfo) {

        val theme = theme.value ?: return@combineSources
        val translate = translate.getOrEmpty()
        val viewItemList = viewItemList.getOrEmpty()
        val isLowOnMedication = isLowOnMedication.value ?: false

        val inputs = viewItemList.filterIsInstance<InputViewItem>()

        val name = inputs.find { it.id == Id.NAME }?.text
        val dosage = inputs.find { it.id == Id.DOSAGE }?.text
        val quantity = inputs.find { it.id == Id.QUANTITY }?.text?.toString()?.parseQuality() ?: Medicine.UNLIMITED


        val isNameBlank = name.isNullOrBlank()
        val isDosageBlank = dosage.isNullOrBlank()
        val isQuantityBlank = isLowOnMedication && quantity <= 0.0


        val isClicked = !isNameBlank && !isDosageBlank && !isQuantityBlank

        val info = ButtonInfo(
            title = if (isNameBlank) {
                translate["message_please_enter_medicine_item_name"].orEmpty()
            } else if (isDosageBlank) {
                translate["message_please_enter_medicine_item_dosage"].orEmpty()
            } else if (isQuantityBlank) {
                translate["message_please_enter_medicine_item_quantity"].orEmpty()
            } else if (medicineId.value.isNullOrBlank()) {
                translate["action_add_medicine_item"].orEmpty()
            } else {
                translate["action_update_medicine_item"].orEmpty()
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

    fun updateSearch(text: CharSequence) {

        if (name.postDifferentValue(text.toString())) {

            medicine.postValue(null)
        }
    }

    fun updateMedicine(it: Alarm.MedicineItem) {

        this.medicineItem.postDifferentValue(it)
    }

    fun updateMedicine(it: Medicine) {

        this.medicine.postDifferentValue(it)
    }

    fun refreshButtonInfo() {

        refreshButtonInfo.postDifferentValue(System.currentTimeMillis())
    }

    fun updateSearchEnable(b: Boolean) {

        isMedicineSearchEnable.postDifferentValue(b)
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