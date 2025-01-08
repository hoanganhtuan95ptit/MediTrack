package com.simple.meditrack.ui.alarm_add.image

import android.graphics.Typeface
import android.view.Gravity
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModels.BaseViewModel
import com.simple.adapter.SpaceViewItem
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.meditrack.ui.base.adapters.ImageViewItem
import com.simple.meditrack.ui.base.adapters.TextViewItem
import com.simple.meditrack.ui.view.Size
import com.simple.meditrack.ui.view.TextStyle
import com.simple.meditrack.utils.AppSize
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appSize
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.appTranslate

class ImagePickerViewModel : BaseViewModel() {

    @VisibleForTesting
    val size: LiveData<AppSize> = mediatorLiveData {

        appSize.collect {

            postDifferentValue(it)
        }
    }

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
    val images: LiveData<List<String>> = MediatorLiveData<List<String>>().apply {

        value = listOf(
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_0.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_1.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_2.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_3.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_4.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_5.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_6.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_7.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_8.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_9.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_10.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_11.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_12.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_13.png",
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_14.png",
        )
    }

    @VisibleForTesting
    val imageSelected: LiveData<String> = MediatorLiveData("")

    val viewItemList: LiveData<List<ViewItem>> = combineSources(size, images, translate, imageSelected) {

        val size = size.value ?: return@combineSources
        val translate = translate.value ?: return@combineSources

        val width = (size.width - DP.DP_12 * 2) / 3

        val list = arrayListOf<ViewItem>()

        TextViewItem(
            id = "TITLE",
            text = translate["Image Picker"].orEmpty(),
            textStyle = TextStyle(
                textSize = 20.0f,
                typeface = Typeface.BOLD,
                textGravity = Gravity.CENTER,

                )
        ).let {

            list.add(it)
            list.add(SpaceViewItem(height = DP.DP_16))
        }

        images.getOrEmpty().map {

            ImageViewItem(
                id = it,

                image = it,

                size = Size(
                    width = width,
                    height = width
                )
            )
        }.let {

            list.addAll(it)
        }

        postDifferentValue(list)
    }
}