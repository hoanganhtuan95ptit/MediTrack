package com.simple.meditrack.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

const val versionDao = 2

@Database(entities = [RoomAlarm::class, RoomMedicine::class], version = versionDao, exportSchema = false)
abstract class MediTrackRoomDatabase : RoomDatabase() {

    abstract fun providerAlarmDao(): AlarmDao

    abstract fun providerMedicineDao(): MedicineDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {

        // Thêm cột mới vào bảng hiện có
        database.execSQL("ALTER TABLE medicine ADD COLUMN createTime INTEGER DEFAULT 0 NOT NULL")
    }
}