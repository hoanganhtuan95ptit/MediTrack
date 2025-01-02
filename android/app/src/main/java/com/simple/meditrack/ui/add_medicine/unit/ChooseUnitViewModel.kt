package com.simple.meditrack.ui.add_medicine.unit

import android.view.Gravity
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.meditrack.Id
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.ui.base.adapters.TextViewItem
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.ui.view.Margin
import com.simple.meditrack.ui.view.Padding
import com.simple.meditrack.ui.view.Size
import com.simple.meditrack.ui.view.TextStyle
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate

class ChooseUnitViewModel : TransitionViewModel() {

    val unit: LiveData<Medicine.Unit> = MutableLiveData(Medicine.Unit.TABLET)

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

    val viewItemList: LiveData<List<ViewItem>> = combineSources(unit, theme, translate) {

        val unit = unit.value ?: return@combineSources
        val theme = theme.value ?: return@combineSources
        val translate = translate.value ?: return@combineSources

        val list = arrayListOf<ViewItem>()

        TextViewItem(
            id = Id.NAME,
            text = translate["Chọn loại thuốc"].orEmpty(),
            textStyle = TextStyle(
                textSize = 20f,
                textGravity = Gravity.CENTER
            )
        ).let {

            list.add(it)
        }

        Medicine.Unit.entries.map {

            it.toViewItem(unit, theme, translate)
        }.takeIf {

            it.isNotEmpty()
        }?.let {

            list.add(SpaceViewItem(height = DP.DP_16))
            list.addAll(it)
            list.add(SpaceViewItem(height = DP.DP_100))
        }


        postDifferentValue(list)
    }

    fun updateUnit(it: Medicine.Unit) {
        unit.postDifferentValue(it)
    }

    private fun Medicine.Unit.toViewItem(unitSelected: Medicine.Unit, theme: AppTheme, translate: Map<String, String>) = TextViewItem(
        id = Id.UNIT + "_" + this.name,
        data = this,
        text = translate[this.name].orEmpty(),
        size = Size(
            width = ViewGroup.LayoutParams.WRAP_CONTENT,
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        ),
        margin = Margin(
            left = DP.DP_4,
            top = DP.DP_16,
            right = DP.DP_4,
            bottom = DP.DP_16
        ),
        padding = Padding(
            left = DP.DP_16,
            top = DP.DP_8,
            right = DP.DP_16,
            bottom = DP.DP_8
        ),
        textStyle = TextStyle(
            textSize = 12f
        ),
        background = Background(
            strokeColor = if (this == unitSelected) {
                theme.colorPrimary
            } else {
                theme.colorDivider
            },
            strokeWidth = 1,
            cornerRadius = 150
        ),
    )
}