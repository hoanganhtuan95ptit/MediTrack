package com.simple.meditrack.di

import com.simple.meditrack.domain.usecases.alarm.DeleteAlarmUseCase
import com.simple.meditrack.domain.usecases.alarm.GetAlarmByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.GetListAlarmAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.InsertOrUpdateAlarmUseCase
import com.simple.meditrack.domain.usecases.medicine.GetMedicineByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.medicine.SearchMedicineUseCase
import org.koin.dsl.module

@JvmField
val usecaseModule = module {

    single { DeleteAlarmUseCase(get()) }

    single { GetListAlarmAsyncUseCase(get()) }

    single { GetAlarmByIdAsyncUseCase(get(), get()) }

    single { InsertOrUpdateAlarmUseCase(get(), get()) }


    single { SearchMedicineUseCase(get()) }

    single { GetMedicineByIdAsyncUseCase(get()) }
}