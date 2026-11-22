package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AstmReferenceRowEntity
import com.example.data.local.CachedConversionEntity
import com.example.data.local.CalibrationSettingsEntity
import com.example.data.local.TankLogEntryEntity
import com.example.data.repository.FuelDensityRepository
import com.example.domain.Astm53BCalculator
import com.example.domain.ComplianceStatus
import com.example.domain.ConversionResult
import com.example.domain.FuelGrade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

data class ConverterUiState(
    val selectedGrade: FuelGrade = FuelGrade.DIESEL_HSD,
    val tempInput: String = "28",
    val densityInput: String = "823",
    val activeResult: ConversionResult = Astm53BCalculator.calculate(
        fuelGrade = FuelGrade.DIESEL_HSD,
        observedTempC = 28.0,
        observedDensityKgM3 = 823.0
    ),
    val lastCalculatedMillis: Long = System.currentTimeMillis()
)

data class AstmTablesUiState(
    val selectedGradeFilter: FuelGrade? = null,
    val selectedTempFilter: Double? = null,
    val rows: List<AstmReferenceRowEntity> = emptyList(),
    val totalCachedRows: Int = 0
)

data class TankLogbookUiState(
    val selectedGradeFilter: FuelGrade? = null,
    val alertsOnly: Boolean = false,
    val logs: List<TankLogEntryEntity> = emptyList(),
    val compliantCount: Int = 0,
    val alertCount: Int = 0
)

class FuelDensityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FuelDensityRepository(AppDatabase.getInstance(application))

    private val _converterState = MutableStateFlow(ConverterUiState())
    val converterState: StateFlow<ConverterUiState> = _converterState.asStateFlow()

    private val _tableGradeFilter = MutableStateFlow<FuelGrade?>(null)
    private val _tableTempFilter = MutableStateFlow<Double?>(null)

    private val _logGradeFilter = MutableStateFlow<FuelGrade?>(null)
    private val _logAlertsOnly = MutableStateFlow(false)

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val calibrationSettings: StateFlow<CalibrationSettingsEntity> =
        repository.calibrationSettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CalibrationSettingsEntity()
        )

    val recentConversions: StateFlow<List<CachedConversionEntity>> =
        repository.recentConversions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val cachedConversionCount: StateFlow<Int> =
        repository.cachedConversionCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val astmTablesState: StateFlow<AstmTablesUiState> = combine(
        repository.astmReferenceRows,
        _tableGradeFilter,
        _tableTempFilter
    ) { allRows, gradeFilter, tempFilter ->
        val filtered = allRows.filter { row ->
            (gradeFilter == null || row.fuelGradeId == gradeFilter.id) &&
                (tempFilter == null || row.observedTempC == tempFilter)
        }
        AstmTablesUiState(
            selectedGradeFilter = gradeFilter,
            selectedTempFilter = tempFilter,
            rows = filtered,
            totalCachedRows = allRows.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AstmTablesUiState()
    )

    val tankLogbookState: StateFlow<TankLogbookUiState> = combine(
        repository.tankLogs,
        _logGradeFilter,
        _logAlertsOnly
    ) { allLogs, gradeFilter, alertsOnly ->
        val filtered = allLogs.filter { log ->
            (gradeFilter == null || log.fuelGradeId == gradeFilter.id) &&
                (!alertsOnly || !log.isCompliant)
        }
        TankLogbookUiState(
            selectedGradeFilter = gradeFilter,
            alertsOnly = alertsOnly,
            logs = filtered,
            compliantCount = allLogs.count { it.isCompliant },
            alertCount = allLogs.count { !it.isCompliant }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TankLogbookUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureOfflineCacheSeeded()
            recomputeLive(persistToCache = false)
        }
    }

    fun selectFuelGrade(grade: FuelGrade, loadDefaultInputs: Boolean = true) {
        _converterState.update { state ->
            if (loadDefaultInputs) {
                state.copy(
                    selectedGrade = grade,
                    tempInput = grade.defaultObservedTemp.toString(),
                    densityInput = grade.defaultObservedDensity.toString()
                )
            } else {
                state.copy(selectedGrade = grade)
            }
        }
        recomputeLive(persistToCache = false)
    }

    fun updateTempInput(value: String) {
        if (isValidIntegerInput(value, allowNegative = true)) {
            _converterState.update { it.copy(tempInput = value) }
            recomputeLive(persistToCache = false)
        }
    }

    fun stepTemp(delta: Int) {
        val current = _converterState.value.tempInput.toIntOrNull() ?: 28
        val next = (current + delta).coerceIn(-18, 90)
        _converterState.update { it.copy(tempInput = next.toString()) }
        recomputeLive(persistToCache = false)
    }

    fun updateDensityInput(value: String) {
        if (isValidIntegerInput(value, allowNegative = false)) {
            _converterState.update { it.copy(densityInput = value) }
            recomputeLive(persistToCache = false)
        }
    }

    fun stepDensity(delta: Int) {
        val current = _converterState.value.densityInput.toIntOrNull() ?: 823
        val next = (current + delta).coerceIn(600, 1075)
        _converterState.update { it.copy(densityInput = next.toString()) }
        recomputeLive(persistToCache = false)
    }

    fun calculateAndCache() {
        recomputeLive(persistToCache = true)
    }

    fun loadAstmRowIntoConverter(row: AstmReferenceRowEntity) {
        val grade = FuelGrade.fromId(row.fuelGradeId)
        _converterState.update {
            it.copy(
                selectedGrade = grade,
                tempInput = row.observedTempC.roundToInt().toString(),
                densityInput = row.observedDensityKgM3.roundToInt().toString()
            )
        }
        recomputeLive(persistToCache = false)
        showMessage("Loaded ${grade.shortCode} (${row.observedTempC.roundToInt()}°C / ${row.observedDensityKgM3.roundToInt()} kg/m³)")
    }

    private fun recomputeLive(persistToCache: Boolean) {
        val state = _converterState.value
        val cal = calibrationSettings.value
        val obsTemp = state.tempInput.toIntOrNull()?.toDouble() ?: Double.NaN
        val obsDensity = state.densityInput.toIntOrNull()?.toDouble() ?: Double.NaN

        val result = if (obsTemp.isNaN() || obsDensity.isNaN()) {
            Astm53BCalculator.calculate(
                fuelGrade = state.selectedGrade,
                observedTempC = -999.0,
                observedDensityKgM3 = -999.0
            )
        } else {
            Astm53BCalculator.calculate(
                fuelGrade = state.selectedGrade,
                observedTempC = obsTemp,
                observedDensityKgM3 = obsDensity,
                applyGlassHydrometerCorrection = cal.applyGlassHydrometerCorrection,
                hydrometerOffsetKgM3 = cal.hydrometerOffsetKgM3,
                thermometerOffsetC = cal.thermometerOffsetC,
                invoiceDensity15C = null,
                maxVarianceThresholdKgM3 = cal.maxVarianceThresholdKgM3
            )
        }

        _converterState.update {
            it.copy(
                activeResult = result,
                lastCalculatedMillis = System.currentTimeMillis()
            )
        }

        if (persistToCache && result.complianceStatus != ComplianceStatus.INVALID_INPUT) {
            viewModelScope.launch {
                repository.cacheConversion(result)
                repository.saveTankLog(
                    TankLogEntryEntity(
                        tankName = result.fuelGrade.defaultTankName,
                        fuelGradeId = result.fuelGrade.id,
                        observedTempC = result.observedTempC,
                        observedDensityKgM3 = result.observedDensityKgM3,
                        density15CKgM3 = result.density15CKgM3,
                        invoiceDensity15C = null,
                        varianceKgM3 = null,
                        dipMm = null,
                        volumeLiters = null,
                        isCompliant = result.complianceStatus == ComplianceStatus.COMPLIANT,
                        complianceStatusName = result.complianceStatus.name,
                        operatorNote = "ASTM 53B calculation cached"
                    )
                )
                showMessage("Calculated ${String.format(Locale.US, "%.1f", result.density15CKgM3)} kg/m³")
            }
        }
    }

    // Dark Mode Toggle (persisted offline in Room)
    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = calibrationSettings.value
            repository.updateCalibration(current.copy(isDarkMode = !current.isDarkMode))
        }
    }

    // ASTM Tables Filters
    fun setTableGradeFilter(grade: FuelGrade?) {
        _tableGradeFilter.value = grade
    }

    fun setTableTempFilter(temp: Double?) {
        _tableTempFilter.value = temp
    }

    // Tank Logbook Actions
    fun setLogGradeFilter(grade: FuelGrade?) {
        _logGradeFilter.value = grade
    }

    fun toggleLogAlertsOnly() {
        _logAlertsOnly.value = !_logAlertsOnly.value
    }

    fun deleteTankLog(id: Int) {
        viewModelScope.launch {
            repository.deleteTankLog(id)
            showMessage("Removed entry")
        }
    }

    // Calibration Settings Actions
    fun toggleGlassHydrometerCorrection(enabled: Boolean) {
        viewModelScope.launch {
            val current = calibrationSettings.value
            repository.updateCalibration(
                current.copy(
                    applyGlassHydrometerCorrection = enabled,
                    lastCalibratedMillis = System.currentTimeMillis()
                )
            )
            recomputeLive(persistToCache = false)
        }
    }

    fun stepHydrometerOffset(delta: Double) {
        viewModelScope.launch {
            val current = calibrationSettings.value
            val next = Astm53BCalculator.roundTo1Decimal(
                (current.hydrometerOffsetKgM3 + delta).coerceIn(-5.0, 5.0)
            )
            repository.updateCalibration(current.copy(hydrometerOffsetKgM3 = next))
            recomputeLive(persistToCache = false)
        }
    }

    fun stepThermometerOffset(delta: Double) {
        viewModelScope.launch {
            val current = calibrationSettings.value
            val next = Astm53BCalculator.roundTo1Decimal(
                (current.thermometerOffsetC + delta).coerceIn(-5.0, 5.0)
            )
            repository.updateCalibration(current.copy(thermometerOffsetC = next))
            recomputeLive(persistToCache = false)
        }
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun consumeMessage() {
        _snackbarMessage.value = null
    }

    private fun isValidIntegerInput(input: String, allowNegative: Boolean): Boolean {
        if (input.isEmpty()) return true
        if (allowNegative && input == "-") return true
        val pattern = if (allowNegative) Regex("^-?\\d{0,3}$") else Regex("^\\d{0,4}$")
        return input.matches(pattern)
    }
}
