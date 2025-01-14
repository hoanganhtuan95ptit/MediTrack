package com.simple.meditrack.di

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.simple.meditrack.data.dao.AlarmDao
import com.simple.meditrack.data.dao.AlarmRoom
import com.simple.meditrack.data.dao.KeyTranslateDao
import com.simple.meditrack.data.dao.KeyTranslateRoom
import com.simple.meditrack.data.dao.MedicineDao
import com.simple.meditrack.data.dao.MedicineRoom
import org.koin.dsl.module

const val versionDao = 1

@Database(entities = [AlarmRoom::class, MedicineRoom::class, KeyTranslateRoom::class], version = versionDao, exportSchema = false)
abstract class MediTrackRoomDatabase : RoomDatabase() {

    abstract fun providerAlarmDao(): AlarmDao

    abstract fun providerMedicineDao(): MedicineDao

    abstract fun providerKeyTranslateDao(): KeyTranslateDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {

        // Thêm cột mới vào bảng hiện có
        database.execSQL("ALTER TABLE medicine ADD COLUMN createTime INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {

        // Lệnh SQL để tạo bảng mới
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `key_translate` (
                `value` TEXT NOT NULL,
                `key` TEXT NOT NULL,
                `langCode` TEXT NOT NULL,
                PRIMARY KEY(`key`, `langCode`)
            )
            """.trimIndent()
        )
    }
}

@JvmField
val daoModule = module {

    single {
        Room.databaseBuilder(get(), MediTrackRoomDatabase::class.java, "alarm_database")
//            .addMigrations(MIGRATION_1_2)
//            .addMigrations(MIGRATION_2_3)
            .build()
    }

    single {
        get<MediTrackRoomDatabase>().providerAlarmDao()
    }

    single {
        get<MediTrackRoomDatabase>().providerMedicineDao()
    }

    single {
        get<MediTrackRoomDatabase>().providerKeyTranslateDao()
    }
}