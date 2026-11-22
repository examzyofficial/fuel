package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AstmReferenceRowEntity::class,
        CachedConversionEntity::class,
        TankLogEntryEntity::class,
        CalibrationSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun astmReferenceDao(): AstmReferenceDao
    abstract fun cachedConversionDao(): CachedConversionDao
    abstract fun tankLogDao(): TankLogDao
    abstract fun calibrationDao(): CalibrationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fuel_density_offline_cache.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
