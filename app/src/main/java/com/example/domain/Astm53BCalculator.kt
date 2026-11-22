package com.example.domain

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.round

enum class ComplianceStatus {
    COMPLIANT,
    OUT_OF_SPEC,
    VARIANCE_BREACH,
    INVALID_INPUT
}

data class ConversionResult(
    val fuelGrade: FuelGrade,
    val observedTempC: Double,
    val observedDensityKgM3: Double,
    val correctedHydrometerDensity: Double,
    val density15CKgM3: Double,
    val vcf: Double,
    val alpha15: Double,
    val hydrometerCorrectionOffset: Double,
    val invoiceDensity15C: Double?,
    val invoiceVarianceKgM3: Double?,
    val complianceStatus: ComplianceStatus,
    val statusHeadline: String,
    val statusDetail: String,
    val curvePoints: List<ThermalCurvePoint>
)

data class ThermalCurvePoint(
    val tempC: Double,
    val densityKgM3: Double
)

/**
 * Implements ASTM D1250-04 / API MPMS Chapter 11.1 Table 53B
 * Generalized Refined Products density reduction to standard 15.0 °C.
 */
object Astm53BCalculator {

    private const val MIN_TEMP_C = -18.0
    private const val MAX_TEMP_C = 90.0
    private const val MIN_DENSITY_KG_M3 = 600.0
    private const val MAX_DENSITY_KG_M3 = 1075.0

    fun calculate(
        fuelGrade: FuelGrade,
        observedTempC: Double,
        observedDensityKgM3: Double,
        applyGlassHydrometerCorrection: Boolean = true,
        hydrometerOffsetKgM3: Double = 0.0,
        thermometerOffsetC: Double = 0.0,
        invoiceDensity15C: Double? = null,
        maxVarianceThresholdKgM3: Double = 3.0
    ): ConversionResult {
        val calibratedTemp = observedTempC + thermometerOffsetC
        val calibratedObsDensity = observedDensityKgM3 + hydrometerOffsetKgM3

        if (calibratedTemp !in MIN_TEMP_C..MAX_TEMP_C ||
            calibratedObsDensity !in MIN_DENSITY_KG_M3..MAX_DENSITY_KG_M3
        ) {
            return ConversionResult(
                fuelGrade = fuelGrade,
                observedTempC = observedTempC,
                observedDensityKgM3 = observedDensityKgM3,
                correctedHydrometerDensity = calibratedObsDensity,
                density15CKgM3 = 0.0,
                vcf = 1.0,
                alpha15 = 0.0,
                hydrometerCorrectionOffset = 0.0,
                invoiceDensity15C = invoiceDensity15C,
                invoiceVarianceKgM3 = null,
                complianceStatus = ComplianceStatus.INVALID_INPUT,
                statusHeadline = "READING OUT OF RANGE",
                statusDetail = "ASTM 53B requires -18.0 to 90.0 °C and 600.0 to 1075.0 kg/m³.",
                curvePoints = emptyList()
            )
        }

        val deltaT = calibratedTemp - 15.0

        // Soda-lime glass hydrometer thermal expansion correction (ASTM D1250)
        val hycFactor = if (applyGlassHydrometerCorrection) {
            1.0 - 0.000023 * deltaT - 0.00000002 * deltaT * deltaT
        } else {
            1.0
        }
        val rhoHydCorrected = calibratedObsDensity * hycFactor
        val hycDelta = rhoHydCorrected - calibratedObsDensity

        // Fixed-point iteration for rho_15 per API MPMS 11.1 Table 53B
        var rho15 = rhoHydCorrected
        var vcf = 1.0
        var alpha15 = computeAlpha15(rho15)

        repeat(16) {
            alpha15 = computeAlpha15(rho15)
            val exponent = -alpha15 * deltaT * (1.0 + 0.8 * alpha15 * deltaT)
            vcf = exp(exponent)
            val nextRho15 = rhoHydCorrected / vcf
            if (abs(nextRho15 - rho15) < 1e-7) {
                rho15 = nextRho15
                return@repeat
            }
            rho15 = nextRho15
        }

        val roundedRho15 = roundTo1Decimal(rho15)
        val roundedVcf = roundTo4Decimals(vcf)
        val roundedHycDelta = roundTo2Decimals(hycDelta)

        val invoiceVariance = invoiceDensity15C?.let { roundTo1Decimal(roundedRho15 - it) }

        val inGradeSpec = roundedRho15 in fuelGrade.minSpec15C..fuelGrade.maxSpec15C
        val varianceBreached = invoiceVariance != null && abs(invoiceVariance) > maxVarianceThresholdKgM3

        val (status, headline, detail) = when {
            varianceBreached -> Triple(
                ComplianceStatus.VARIANCE_BREACH,
                "CALIBRATION / VARIANCE ALERT",
                "Invoice variance ${formatSigned(invoiceVariance!!)} kg/m³ exceeds ±${roundTo1Decimal(maxVarianceThresholdKgM3)} kg/m³ limit."
            )
            !inGradeSpec -> Triple(
                ComplianceStatus.OUT_OF_SPEC,
                "OUT OF SPEC • ${fuelGrade.shortCode}",
                "15°C density ${roundedRho15} outside ${fuelGrade.minSpec15C}–${fuelGrade.maxSpec15C} kg/m³ standard."
            )
            else -> Triple(
                ComplianceStatus.COMPLIANT,
                "ASTM 53B COMPLIANT",
                "Verified within ${fuelGrade.standardCode} (${fuelGrade.minSpec15C}–${fuelGrade.maxSpec15C} kg/m³)."
            )
        }

        val curvePoints = (10..45 step 5).map { temp ->
            val dt = temp - 15.0
            val pointVcf = exp(-alpha15 * dt * (1.0 + 0.8 * alpha15 * dt))
            ThermalCurvePoint(
                tempC = temp.toDouble(),
                densityKgM3 = roundTo1Decimal(rho15 * pointVcf)
            )
        }

        return ConversionResult(
            fuelGrade = fuelGrade,
            observedTempC = calibratedTemp,
            observedDensityKgM3 = calibratedObsDensity,
            correctedHydrometerDensity = roundTo1Decimal(rhoHydCorrected),
            density15CKgM3 = roundedRho15,
            vcf = roundedVcf,
            alpha15 = alpha15,
            hydrometerCorrectionOffset = roundedHycDelta,
            invoiceDensity15C = invoiceDensity15C,
            invoiceVarianceKgM3 = invoiceVariance,
            complianceStatus = status,
            statusHeadline = headline,
            statusDetail = detail,
            curvePoints = curvePoints
        )
    }

    /**
     * API MPMS 11.1 / ASTM D1250-04 Table 53B Group B (Generalized Products) constants.
     */
    private fun computeAlpha15(rho15: Double): Double {
        return when {
            rho15 < 770.352 -> {
                // Gasolines
                val k0 = 346.4228
                val k1 = 0.4388
                (k0 / (rho15 * rho15)) + (k1 / rho15)
            }
            rho15 < 787.520 -> {
                // Transition zone
                val k0 = 2680.3206
                val k2 = -0.00336312
                (k0 / (rho15 * rho15)) + k2
            }
            rho15 < 838.313 -> {
                // Jet fuels / Kerosenes / Light Diesel
                val k0 = 594.5418
                k0 / (rho15 * rho15)
            }
            else -> {
                // Fuel Oils / Heavy Diesel / Biodiesel
                val k0 = 186.9696
                val k1 = 0.4862
                (k0 / (rho15 * rho15)) + (k1 / rho15)
            }
        }
    }

    fun roundTo1Decimal(value: Double): Double = round(value * 10.0) / 10.0
    fun roundTo2Decimals(value: Double): Double = round(value * 100.0) / 100.0
    fun roundTo4Decimals(value: Double): Double = round(value * 10000.0) / 10000.0

    fun formatSigned(value: Double): String {
        val sign = if (value >= 0.0) "+" else ""
        return "$sign${String.format(java.util.Locale.US, "%.1f", value)}"
    }
}
