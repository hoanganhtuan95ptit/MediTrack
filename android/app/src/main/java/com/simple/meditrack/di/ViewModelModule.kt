package com.simple.meditrack.di

import com.simple.meditrack.ui.base.transition.TransitionGlobalViewModel
import com.simple.meditrack.ui.add_alarm.AddAlarmViewModel
import com.simple.meditrack.ui.add_alarm.image.ImagePickerViewModel
import com.simple.meditrack.ui.add_medicine.AddMedicineViewModel
import com.simple.meditrack.ui.add_medicine.unit.ChooseUnitViewModel
import com.simple.meditrack.ui.alarm_list.AlarmListViewModel
import com.simple.meditrack.ui.base.PageViewModel
import com.simple.meditrack.ui.notification.NotificationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@JvmField
val viewModelModule = module {

    viewModel {
        NotificationViewModel(get())
    }

    viewModel {
        TransitionGlobalViewModel()
    }

    viewModel {
        AlarmListViewModel(get())
    }

    viewModel {
        AddMedicineViewModel(get(), get())
    }

    viewModel {
        AddAlarmViewModel(get(), get())
    }

    viewModel {
        ChooseUnitViewModel()
    }

    viewModel {
        ImagePickerViewModel()
    }

    viewModel {
        PageViewModel()
    }
}