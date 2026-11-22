package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AstmReferenceRowEntity
import com.example.data.local.CachedConversionEntity
import com.example.data.local.CalibrationSettingsEntity
import com.example.data.local.TankLogEntryEntity
import com.example.domain.Astm53BCalculator
import com.example.domain.ComplianceStatus
import com.example.domain.ConversionResult
import com.example.domain.FuelGrade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FuelDensityRepository(private val database: AppDatabase) {

    private val astmDao = database.astmReferenceDao()
    private val cacheDao = database.cachedConversionDao()
    private val tankLogDao = database.tankLogDao()
    private val calibrationDao = database.calibrationDao()

    val astmReferenceRows: Flow<List<AstmReferenceRowEntity>> = astmDao.observeAllRows()
    val recentConversions: Flow<List<CachedConversionEntity>> = cacheDao.observeRecentConversions()
    val cachedConversionCount: Flow<Int> = cacheDao.observeCount()
    val tankLogs: Flow<List<TankLogEntryEntity>> = tankLogDao.observeAllLogs()
    val calibrationSettings: Flow<CalibrationSettingsEntity> =
        calibrationDao.observeSettings().map { it ?: CalibrationSettingsEntity() }

    suspend fun ensureOfflineCacheSeeded() = withContext(Dispatchers.IO) {
        if (calibrationDao.getSettingsOnce() == null) {
            calibrationDao.upsertSettings(CalibrationSettingsEntity())
        }
        if (astmDao.getRowCount() == 0) {
            astmDao.insertAll(generateAstmReferenceDataset())
        }
        if (tankLogDao.getLogCount() == 0) {
            tankLogDao.insertLog(
                TankLogEntryEntity(
                    tankName = "Tank 02 • HSD-1",
                    fuelGradeId = FuelGrade.DIESEL_HSD.id,
                    observedTempC = 28.0,
                    observedDensityKgM3 = 823.0,
                    density15CKgM3 = 831.7,
                    invoiceDensity15C = 832.0,
                    varianceKgM3 = -0.3,
                    dipMm = 1420.0,
                    volumeLiters = 18450.0,
                    isCompliant = true,
                    complianceStatusName = ComplianceStatus.COMPLIANT.name,
                    operatorNote = "Morning verification",
                    timestampMillis = System.currentTimeMillis() - 3_600_000L * 2
                )
            )
            tankLogDao.insertLog(
                TankLogEntryEntity(
                    tankName = "Tank 01 • MS-1",
                    fuelGradeId = FuelGrade.PETROL_MS.id,
                    observedTempC = 29.0,
                    observedDensityKgM3 = 731.0,
                    density15CKgM3 = 743.3,
                    invoiceDensity15C = 743.2,
                    varianceKgM3 = 0.1,
                    dipMm = 1185.0,
                    volumeLiters = 14200.0,
                    isCompliant = true,
                    complianceStatusName = ComplianceStatus.COMPLIANT.name,
                    operatorNote = "Standard check passed",
                    timestampMillis = System.currentTimeMillis() - 3_600_000L * 6
                )
            )
            tankLogDao.insertLog(
                TankLogEntryEntity(
                    tankName = "Tank 03 • SPD-1",
                    fuelGradeId = FuelGrade.PREMIUM_SPEED.id,
                    observedTempC = 31.0,
                    observedDensityKgM3 = 740.0,
                    density15CKgM3 = 754.0,
                    invoiceDensity15C = 750.5,
                    varianceKgM3 = 3.5,
                    dipMm = 940.0,
                    volumeLiters = 9800.0,
                    isCompliant = false,
                    complianceStatusName = ComplianceStatus.VARIANCE_BREACH.name,
                    operatorNote = "Variance alert",
                    timestampMillis = System.currentTimeMillis() - 3_600_000L * 14
                )
            )
            tankLogDao.insertLog(
                TankLogEntryEntity(
                    tankName = "Tank 04 • BIO-1",
                    fuelGradeId = FuelGrade.BIO_DIESEL.id,
                    observedTempC = 26.0,
                    observedDensityKgM3 = 870.0,
                    density15CKgM3 = 877.5,
                    invoiceDensity15C = 877.6,
                    varianceKgM3 = -0.1,
                    dipMm = 780.0,
                    volumeLiters = 6400.0,
                    isCompliant = true,
                    complianceStatusName = ComplianceStatus.COMPLIANT.name,
                    operatorNote = "B100 verified",
                    timestampMillis = System.currentTimeMillis() - 3_600_000L * 24
                )
            )
        }
    }

    suspend fun cacheConversion(result: ConversionResult) = withContext(Dispatchers.IO) {
        if (result.complianceStatus == ComplianceStatus.INVALID_INPUT) return@withContext
        cacheDao.insertConversion(
            CachedConversionEntity(
                fuelGradeId = result.fuelGrade.id,
                observedTempC = result.observedTempC,
                observedDensityKgM3 = result.observedDensityKgM3,
                density15CKgM3 = result.density15CKgM3,
                vcf = result.vcf,
                hydrometerOffset = result.hydrometerCorrectionOffset,
                isCompliant = result.complianceStatus == ComplianceStatus.COMPLIANT,
                statusHeadline = result.statusHeadline
            )
        )
    }

    suspend fun saveTankLog(entry: TankLogEntryEntity) = withContext(Dispatchers.IO) {
        tankLogDao.insertLog(entry)
    }

    suspend fun deleteTankLog(id: Int) = withContext(Dispatchers.IO) {
        tankLogDao.deleteById(id)
    }

    suspend fun updateCalibration(settings: CalibrationSettingsEntity) = withContext(Dispatchers.IO) {
        calibrationDao.upsertSettings(settings)
    }

    suspend fun rebuildOfflineAstmCache() = withContext(Dispatchers.IO) {
        astmDao.clearAll()
        astmDao.insertAll(generateAstmReferenceDataset())
    }

    suspend fun clearConversionCache() = withContext(Dispatchers.IO) {
        cacheDao.clearAll()
    }

    private fun generateAstmReferenceDataset(): List<AstmReferenceRowEntity> {
        val rows = mutableListOf<AstmReferenceRowEntity>()
        val temps = listOf(10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0)

        val gradeDensityGrids = mapOf(
            FuelGrade.PETROL_MS to listOf(715.0, 725.0, 735.0, 745.0, 755.0, 765.0),
            FuelGrade.DIESEL_HSD to listOf(810.0, 820.0, 825.0, 832.0, 840.0, 850.0),
            FuelGrade.PREMIUM_SPEED to listOf(725.0, 735.0, 745.0, 755.0, 765.0),
            FuelGrade.BIO_DIESEL to listOf(855.0, 865.0, 875.0, 885.0, 895.0)
        )

        for ((grade, densities) in gradeDensityGrids) {
            for (temp in temps) {
                for (obsDensity in densities) {
                    val res = Astm53BCalculator.calculate(
                        fuelGrade = grade,
                        observedTempC = temp,
                        observedDensityKgM3 = obsDensity,
                        applyGlassHydrometerCorrection = true
                    )
                    rows.add(
                        AstmReferenceRowEntity(
                            fuelGradeId = grade.id,
                            observedTempC = temp,
                            observedDensityKgM3 = obsDensity,
                            density15CKgM3 = res.density15CKgM3,
                            vcf = res.vcf,
                            isInStandardSpec = res.complianceStatus == ComplianceStatus.COMPLIANT
                        )
                    )
                }
            }
        }
        return rows
    }
}
