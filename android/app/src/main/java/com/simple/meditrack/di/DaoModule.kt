package com.simple.meditrack.di

import androidx.room.Room
import com.simple.meditrack.data.dao.MIGRATION_1_2
import com.simple.meditrack.data.dao.MediTrackRoomDatabase
import org.koin.dsl.module


@JvmField
val daoModule = module {

    single {
        Room.databaseBuilder(get(), MediTrackRoomDatabase::class.java, "alarm_database")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    single {
        get<MediTrackRoomDatabase>().providerAlarmDao()
    }

    single {
        get<MediTrackRoomDatabase>().providerMedicineDao()
    }
}