package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AstmReferenceDao {
    @Query("SELECT * FROM astm_reference_rows ORDER BY fuelGradeId ASC, observedTempC ASC, observedDensityKgM3 ASC")
    fun observeAllRows(): Flow<List<AstmReferenceRowEntity>>

    @Query("SELECT COUNT(*) FROM astm_reference_rows")
    suspend fun getRowCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rows: List<AstmReferenceRowEntity>)

    @Query("DELETE FROM astm_reference_rows")
    suspend fun clearAll()
}

@Dao
interface CachedConversionDao {
    @Query("SELECT * FROM cached_conversions ORDER BY timestampMillis DESC LIMIT 20")
    fun observeRecentConversions(): Flow<List<CachedConversionEntity>>

    @Query("SELECT COUNT(*) FROM cached_conversions")
    fun observeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversion(entry: CachedConversionEntity)

    @Query("DELETE FROM cached_conversions")
    suspend fun clearAll()
}

@Dao
interface TankLogDao {
    @Query("SELECT * FROM tank_log_entries ORDER BY timestampMillis DESC")
    fun observeAllLogs(): Flow<List<TankLogEntryEntity>>

    @Query("SELECT COUNT(*) FROM tank_log_entries")
    suspend fun getLogCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(entry: TankLogEntryEntity)

    @Query("DELETE FROM tank_log_entries WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface CalibrationDao {
    @Query("SELECT * FROM calibration_settings WHERE id = 1")
    fun observeSettings(): Flow<CalibrationSettingsEntity?>

    @Query("SELECT * FROM calibration_settings WHERE id = 1")
    suspend fun getSettingsOnce(): CalibrationSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: CalibrationSettingsEntity)
}
