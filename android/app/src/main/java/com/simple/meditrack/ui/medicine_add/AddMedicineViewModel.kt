package com.simple.meditrack.ui.medicine_add

import android.text.InputType
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.ext.handler
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.listenerSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.meditrack.Id
import com.simple.meditrack.R
import com.simple.meditrack.domain.usecases.medicine.DeleteMedicineUseCase
import com.simple.meditrack.domain.usecases.medicine.GetMedicineByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.medicine.InsertOrUpdateMedicineUseCase
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Medicine.Companion.toUnit
import com.simple.meditrack.ui.base.adapters.CheckboxViewItem
import com.simple.meditrack.ui.base.adapters.ImageViewItem
import com.simple.meditrack.ui.base.adapters.InputViewItem
import com.simple.meditrack.ui.base.adapters.TextViewItem
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.ui.view.Padding
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate
import com.simple.meditrack.utils.exts.formatQuality
import com.simple.meditrack.utils.exts.parseQuality
import com.simple.meditrack.utils.exts.with
import com.simple.state.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class AddMedicineViewModel(
    private val deleteMedicineUseCase: DeleteMedicineUseCase,
    private val getMedicineByIdAsyncUseCase: GetMedicineByIdAsyncUseCase,
    private val insertOrUpdateMedicineUseCase: InsertOrUpdateMedicineUseCase
) : TransitionViewModel() {

    @VisibleForTesting
    val theme: LiveData<AppTheme> = mediatorLiveData {

        appTheme.collect {

            postDifferentValue(it)
        }
    }

    val translate: LiveData<Map<String, String>> = mediatorLiveData {

        appTranslate.collect {

            postDifferentValue(it)
        }
    }

    @VisibleForTesting
    val medicineId: LiveData<String> = MediatorLiveData()

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

    val title: LiveData<CharSequence> = combineSources(theme, translate, medicine) {

        val theme = theme.value ?: return@combineSources
        val translate = translate.value ?: return@combineSources

        val medicine = medicine.value ?: return@combineSources

        val text = if (medicine.id.isBlank()) {

            translate["title_screen_add_medicine"].orEmpty()
        } else {

            translate["title_screen_update_medicine"].orEmpty()
        }

        postDifferentValue(text.with(ForegroundColorSpan(theme.colorOnBackground)))
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

    val viewItemList: LiveData<List<ViewItem>> = listenerSources(theme, translate, unit, image, isLowOnMedication) {

        val unit = unit.value ?: return@listenerSources
        val theme = theme.value ?: return@listenerSources
        val translate = translate.value ?: return@listenerSources
        val isLowOnMedication = isLowOnMedication.value ?: return@listenerSources

        val medicine = medicine.value

        val list = arrayListOf<ViewItem>()

        ImageViewItem(
            id = Id.IMAGE,
            image = image.getOrEmpty()
        ).let {

            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        val name = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NAME }?.text?.toString() ?: medicine?.name.orEmpty()

        InputViewItem(
            id = Id.NAME,
            hint = translate["hint_enter_medicine_name"].orEmpty(),
            text = name,
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            )
        ).let {

            list.add(TextViewItem(id = "TITLE_" + Id.NAME, text = translate["title_enter_medicine_name"].orEmpty().with("(✶)", ForegroundColorSpan(theme.colorError))))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        TextViewItem(
            id = Id.UNIT,
            data = unit,
            text = translate[unit.name.lowercase()].orEmpty(),
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
            list.add(TextViewItem(id = "TITLE_" + Id.UNIT, text = translate["title_enter_medicine_unit"].orEmpty().with("(✶)", ForegroundColorSpan(theme.colorError))))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        InputViewItem(
            id = Id.NOTE,
            hint = translate["hint_enter_medicine_note"].orEmpty(),
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS,
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.NOTE }?.text?.toString() ?: medicine?.note.orEmpty(),
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.NOTE, text = translate["title_enter_medicine_note"].orEmpty()))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        CheckboxViewItem(
            id = "CHECK_${Id.QUANTITY}",
            text = translate["message_receive_notification_medicine"].orEmpty(),
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
            hint = translate["hint_enter_medicine_quantity"].orEmpty(),
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            text = value?.filterIsInstance<InputViewItem>()?.find { it.id == Id.QUANTITY }?.text?.toString() ?: medicine?.quantity?.takeIf { it > 0 }?.formatQuality() ?: "0",
            background = Background(
                strokeColor = theme.colorDivider,
                strokeWidth = DP.DP_2,
                cornerRadius = DP.DP_16
            )
        ).let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.add(TextViewItem(id = "TITLE_" + Id.QUANTITY, text = translate["title_enter_medicine_quantity"].orEmpty().with("(✶)", ForegroundColorSpan(theme.colorError))))
            list.add(SpaceViewItem(height = DP.DP_8))
            list.add(it)
        }

        list.add(SpaceViewItem(height = DP.DP_350))

        postDifferentValueIfActive(list)
    }


    @VisibleForTesting
    val refreshButtonInfo: LiveData<Long> = MediatorLiveData(0)

    val deleteAlarmState: LiveData<ResultState<Medicine>> = MediatorLiveData()

    val insertOrUpdateState: LiveData<ResultState<Medicine>> = MediatorLiveData()

    val buttonInfo: LiveData<ButtonInfo> = listenerSources(theme, translate, medicine, viewItemList, refreshButtonInfo) {

        val theme = theme.value ?: return@listenerSources
        val translate = translate.getOrEmpty()

        val medicine = medicine.value ?: return@listenerSources

        val viewItemList = viewItemList.getOrEmpty()
        val isLowOnMedication = isLowOnMedication.value ?: false

        val name = viewItemList.filterIsInstance<InputViewItem>().find { it.id == Id.NAME }?.text
        val note = viewItemList.filterIsInstance<InputViewItem>().find { it.id == Id.NOTE }?.text
        val image = viewItemList.filterIsInstance<ImageViewItem>().find { it.id == Id.IMAGE }?.image
        val quantity = viewItemList.filterIsInstance<InputViewItem>().find { it.id == Id.QUANTITY }?.text?.toString()?.parseQuality() ?: Medicine.UNLIMITED


        val isNameBlank = name.isNullOrBlank()
        val isQuantityBlank = isLowOnMedication && quantity <= 0.0

        val isChange = name != medicine.name
                || note != medicine.note
                || image != medicine.image
                || quantity != medicine.quantity

        val isClicked = !isNameBlank && !isQuantityBlank && isChange

        val action0 = ActionInfo(
            text = if (isNameBlank) {
                translate["message_please_enter_medicine_name"].orEmpty()
            } else if (isQuantityBlank) {
                translate["message_please_enter_medicine_quantity"].orEmpty()
            } else if (medicineId.value.isNullOrBlank()) {
                translate["action_add_medicine"].orEmpty()
            } else {
                translate["action_update_medicine"].orEmpty()
            },

            isShow = true,
            isClicked = isClicked,

            background = Background(
                backgroundColor = if (isClicked) {
                    theme.colorPrimary
                } else {
                    theme.colorBackgroundVariant
                }
            )
        )

        val action1 = ActionInfo(
            text = if (medicine.id.isNotBlank()) {
                translate["action_delete_medicine"].orEmpty().with(ForegroundColorSpan(theme.colorError))
            } else {
                translate[""].orEmpty()
            },
            isShow = medicine.id.isNotBlank(),
            isClicked = true
        )

        val info = ButtonInfo(
            action0 = action0,
            action1 = action1
        )

        postDifferentValueIfActive(info)
    }

    fun updateUnit(unit: Medicine.Unit) {

        this.unit.postDifferentValue(unit)
    }

    fun updateImage(imagePath: String) {

        image.postDifferentValue(imagePath)
    }

    fun updateMedicineId(it: String) {

        this.medicineId.postDifferentValue(it)
    }

    fun refreshButtonInfo() {

        refreshButtonInfo.postDifferentValue(System.currentTimeMillis())
    }

    fun switchLowOnMedication() {

        isLowOnMedication.postDifferentValue(!(isLowOnMedication.value ?: false))
    }

    fun deleteAlarm() = GlobalScope.launch(handler + Dispatchers.IO) {

        val medicine = medicine.value ?: return@launch

        deleteAlarmState.postDifferentValue(ResultState.Start)

        runCatching {

            deleteMedicineUseCase.execute(DeleteMedicineUseCase.Param(medicine.id))

            deleteAlarmState.postDifferentValue(ResultState.Success(medicine))
        }.getOrElse {

            deleteAlarmState.postDifferentValue(ResultState.Failed(it))
        }
    }

    fun insertOrUpdateMedicine() = viewModelScope.launch(handler + Dispatchers.IO) {


        val viewItemList = viewItemList.getOrEmpty()

        val texts = viewItemList.filterIsInstance<TextViewItem>()
        val inputs = viewItemList.filterIsInstance<InputViewItem>()

        val medicine = Medicine(
            id = medicine.value?.id?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
            name = inputs.find { it.id == Id.NAME }?.text?.toString().orEmpty(),
            image = image.getOrEmpty(),
            unit = texts.find { it.id == Id.UNIT }?.data.asObject<Medicine.Unit>().value,
            note = inputs.find { it.id == Id.NOTE }?.text?.toString().orEmpty(),
            quantity = inputs.find { it.id == Id.QUANTITY }?.text?.toString().orEmpty().toDoubleOrNull() ?: Medicine.UNLIMITED
        )

        insertOrUpdateState.postDifferentValue(ResultState.Start)

        runCatching {

            insertOrUpdateMedicineUseCase.execute(InsertOrUpdateMedicineUseCase.Param(medicine))

            insertOrUpdateState.postDifferentValue(ResultState.Success(medicine))
        }.getOrElse {

            insertOrUpdateState.postDifferentValue(ResultState.Failed(it))
        }
    }

    data class ButtonInfo(
        val action0: ActionInfo,
        val action1: ActionInfo
    )

    data class ActionInfo(
        val text: CharSequence,

        val isShow: Boolean,
        val isClicked: Boolean,

        val background: Background? = null
    )
}