package com.simple.meditrack.di

import com.simple.meditrack.ui.MainViewModel
import com.simple.meditrack.ui.alarm_add.AddAlarmViewModel
import com.simple.meditrack.ui.alarm_add.image.ImagePickerViewModel
import com.simple.meditrack.ui.alarm_medicine_add.AddAlarmMedicineViewModel
import com.simple.meditrack.ui.unit.ChooseUnitViewModel
import com.simple.meditrack.ui.alarm_list.AlarmListViewModel
import com.simple.meditrack.ui.base.PageViewModel
import com.simple.meditrack.ui.base.transition.TransitionGlobalViewModel
import com.simple.meditrack.ui.medicine_add.AddMedicineViewModel
import com.simple.meditrack.ui.medicine_list.MedicineListViewModel
import com.simple.meditrack.ui.notification.NotificationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@JvmField
val viewModelModule = module {

    viewModel {
        MainViewModel()
    }

    viewModel {
        PageViewModel()
    }

    viewModel {
        NotificationViewModel(get(), get())
    }

    viewModel {
        TransitionGlobalViewModel()
    }


    viewModel {
        ChooseUnitViewModel()
    }

    viewModel {
        ImagePickerViewModel()
    }


    viewModel {
        AlarmListViewModel(get())
    }

    viewModel {
        AddAlarmViewModel(get(), get(), get())
    }

    viewModel {
        AddAlarmMedicineViewModel(get(), get())
    }


    viewModel {
        MedicineListViewModel(get())
    }

    viewModel {
        AddMedicineViewModel(get(), get(), get())
    }
}