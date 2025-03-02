package com.simple.meditrack.di

import com.simple.meditrack.domain.usecases.GetKeyTranslateAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.CloseAlarmUseCase
import com.simple.meditrack.domain.usecases.alarm.DeleteAlarmUseCase
import com.simple.meditrack.domain.usecases.alarm.GetAlarmByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.GetListAlarmAsyncUseCase
import com.simple.meditrack.domain.usecases.alarm.InsertOrUpdateAlarmUseCase
import com.simple.meditrack.domain.usecases.medicine.DeleteMedicineUseCase
import com.simple.meditrack.domain.usecases.medicine.GetListMedicineAsyncUseCase
import com.simple.meditrack.domain.usecases.medicine.GetMedicineByIdAsyncUseCase
import com.simple.meditrack.domain.usecases.medicine.InsertOrUpdateMedicineUseCase
import com.simple.meditrack.domain.usecases.medicine.SearchMedicineUseCase
import org.koin.dsl.module

@JvmField
val usecaseModule = module {

    single { GetKeyTranslateAsyncUseCase(get()) }


    single { CloseAlarmUseCase(get(), get()) }

    single { GetListAlarmAsyncUseCase(get()) }

    single { GetAlarmByIdAsyncUseCase(get(), get()) }

    single { DeleteAlarmUseCase(get()) }

    single { InsertOrUpdateAlarmUseCase(get(), get()) }


    single { GetListMedicineAsyncUseCase(get(), get()) }

    single { SearchMedicineUseCase(get()) }

    single { GetMedicineByIdAsyncUseCase(get()) }

    single { DeleteMedicineUseCase(get()) }

    single { InsertOrUpdateMedicineUseCase(get()) }
}