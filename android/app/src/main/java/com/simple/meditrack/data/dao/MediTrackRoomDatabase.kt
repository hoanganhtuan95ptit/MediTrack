package com.simple.meditrack.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase

const val versionDao = 1

@Database(entities = [RoomAlarm::class, RoomMedicine::class], version = versionDao, exportSchema = false)
abstract class MediTrackRoomDatabase : RoomDatabase() {

    abstract fun providerAlarmDao(): AlarmDao

    abstract fun providerMedicineDao(): MedicineDao
}