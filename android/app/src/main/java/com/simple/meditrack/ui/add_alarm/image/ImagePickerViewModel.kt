package com.simple.meditrack.ui.add_alarm.image

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModels.BaseViewModel
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.extentions.combineSources
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.meditrack.ui.add_alarm.image.adapters.ImageViewItem

class ImagePickerViewModel : BaseViewModel() {

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


    val viewItemList: LiveData<List<ViewItem>> = combineSources(images, imageSelected) {

        val list = arrayListOf<ViewItem>()

        images.getOrEmpty().map {

            ImageViewItem(
                id = it,
                data = it,

                image = it
            )
        }.let {

            list.addAll(it)
        }

        postDifferentValue(list)
    }
}