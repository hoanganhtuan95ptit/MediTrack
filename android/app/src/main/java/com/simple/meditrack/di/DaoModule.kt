package com.simple.meditrack.di

import androidx.room.Room
import com.simple.meditrack.data.dao.AlarmDao
import com.simple.meditrack.data.dao.MediTrackRoomDatabase
import com.simple.meditrack.data.dao.MedicineDao
import com.simple.meditrack.data.repositories.AlarmRepositoryImpl
import com.simple.meditrack.data.repositories.MedicineRepositoryImpl
import com.simple.meditrack.domain.repositories.AlarmRepository
import com.simple.meditrack.domain.repositories.MedicineRepository
import org.koin.dsl.module


@JvmField
val daoModule = module {

    single {
        Room.databaseBuilder(get(), MediTrackRoomDatabase::class.java, "alarm_database")
            .build()
    }

    single {
        get<MediTrackRoomDatabase>().providerAlarmDao()
    }

    single {
        get<MediTrackRoomDatabase>().providerMedicineDao()
    }
}