package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "astm_reference_rows")
data class AstmReferenceRowEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fuelGradeId: String,
    val observedTempC: Double,
    val observedDensityKgM3: Double,
    val density15CKgM3: Double,
    val vcf: Double,
    val isInStandardSpec: Boolean
)

@Entity(tableName = "cached_conversions")
data class CachedConversionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fuelGradeId: String,
    val observedTempC: Double,
    val observedDensityKgM3: Double,
    val density15CKgM3: Double,
    val vcf: Double,
    val hydrometerOffset: Double,
    val isCompliant: Boolean,
    val statusHeadline: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "tank_log_entries")
data class TankLogEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tankName: String,
    val fuelGradeId: String,
    val observedTempC: Double,
    val observedDensityKgM3: Double,
    val density15CKgM3: Double,
    val invoiceDensity15C: Double?,
    val varianceKgM3: Double?,
    val dipMm: Double?,
    val volumeLiters: Double?,
    val isCompliant: Boolean,
    val complianceStatusName: String,
    val operatorNote: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "calibration_settings")
data class CalibrationSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isDarkMode: Boolean = false,
    val stationName: String = "Forecourt Depot #04",
    val operatorId: String = "AUD-208",
    val applyGlassHydrometerCorrection: Boolean = true,
    val hydrometerOffsetKgM3: Double = 0.0,
    val thermometerOffsetC: Double = 0.0,
    val maxVarianceThresholdKgM3: Double = 3.0,
    val lastCalibratedMillis: Long = System.currentTimeMillis(),
    val offlineTableVersion: String = "ASTM D1250-04 • Table 53B"
)
