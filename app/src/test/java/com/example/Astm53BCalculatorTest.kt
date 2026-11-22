package com.example

import com.example.domain.Astm53BCalculator
import com.example.domain.ComplianceStatus
import com.example.domain.FuelGrade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Astm53BCalculatorTest {

    @Test
    fun dieselAt15C_returnsExactObservedDensity() {
        val result = Astm53BCalculator.calculate(
            fuelGrade = FuelGrade.DIESEL_HSD,
            observedTempC = 15.0,
            observedDensityKgM3 = 832.0
        )
        assertEquals(832.0, result.density15CKgM3, 0.05)
        assertEquals(1.0, result.vcf, 0.0005)
        assertEquals(ComplianceStatus.COMPLIANT, result.complianceStatus)
    }

    @Test
    fun dieselAt28_5C_reducesToHigher15CDensity() {
        val result = Astm53BCalculator.calculate(
            fuelGrade = FuelGrade.DIESEL_HSD,
            observedTempC = 28.5,
            observedDensityKgM3 = 823.4
        )
        assertTrue(result.density15CKgM3 in 831.0..834.0)
        assertTrue(result.vcf < 1.0)
        assertEquals(ComplianceStatus.COMPLIANT, result.complianceStatus)
    }

    @Test
    fun invoiceVarianceBreach_flagsAlert() {
        val result = Astm53BCalculator.calculate(
            fuelGrade = FuelGrade.PETROL_MS,
            observedTempC = 28.5,
            observedDensityKgM3 = 731.8,
            invoiceDensity15C = 735.0,
            maxVarianceThresholdKgM3 = 3.0
        )
        assertEquals(ComplianceStatus.VARIANCE_BREACH, result.complianceStatus)
    }

    @Test
    fun outOfRangeInput_returnsInvalidInput() {
        val result = Astm53BCalculator.calculate(
            fuelGrade = FuelGrade.BIO_DIESEL,
            observedTempC = 120.0,
            observedDensityKgM3 = 870.0
        )
        assertEquals(ComplianceStatus.INVALID_INPUT, result.complianceStatus)
    }
}
