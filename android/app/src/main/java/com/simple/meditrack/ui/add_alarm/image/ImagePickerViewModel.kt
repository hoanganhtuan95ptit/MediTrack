package com.simple.meditrack.ui.add_alarm.image

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModels.BaseViewModel
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.mediatorLiveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.meditrack.ui.base.adapters.ImageViewItem
import com.simple.meditrack.ui.view.Size
import com.simple.meditrack.utils.AppSize
import com.simple.meditrack.utils.AppTheme
import com.simple.meditrack.utils.appSize
import com.simple.meditrack.utils.appTheme

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
            "https://raw.githubusercontent.com/hoanganhtuan95ptit/MediTrack/refs/heads/main/android/app/src/main/res/drawable/img_reminder_8.png"
        )
    }

    @VisibleForTesting
    val imageSelected: LiveData<String> = MediatorLiveData("")

    val viewItemList: LiveData<List<ViewItem>> = combineSources(size, images, imageSelected) {

        val size = size.value ?: return@combineSources

        val width = (size.width - DP.DP_12 * 2) / 3

        val list = arrayListOf<ViewItem>()

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