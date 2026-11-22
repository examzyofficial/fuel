package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.ComplianceStatus
import com.example.domain.ConversionResult
import com.example.domain.FuelGrade
import com.example.ui.ConverterUiState
import com.example.ui.components.AstmVerificationBadge
import com.example.ui.components.FuelGradeCard
import com.example.ui.components.PrimaryAmberCtaButton
import com.example.ui.components.TelemetryParameterField
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CardPerimeter
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TelemetryTypography
import java.util.Locale

@Composable
fun ConverterScreen(
    state: ConverterUiState,
    onSelectFuelGrade: (FuelGrade) -> Unit,
    onTempChange: (String) -> Unit,
    onStepTemp: (Int) -> Unit,
    onDensityChange: (String) -> Unit,
    onStepDensity: (Int) -> Unit,
    onCalculateAndCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideLayout = maxWidth >= 600.dp

        if (isWideLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 8.dp, bottom = 112.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ConverterInputsColumn(
                        state = state,
                        onSelectFuelGrade = onSelectFuelGrade,
                        onTempChange = onTempChange,
                        onStepTemp = onStepTemp,
                        onDensityChange = onDensityChange,
                        onStepDensity = onStepDensity,
                        onCalculateAndCache = onCalculateAndCache
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 8.dp, bottom = 176.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ResultHeroCard(result = state.activeResult)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 176.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ConverterInputsColumn(
                    state = state,
                    onSelectFuelGrade = onSelectFuelGrade,
                    onTempChange = onTempChange,
                    onStepTemp = onStepTemp,
                    onDensityChange = onDensityChange,
                    onStepDensity = onStepDensity,
                    onCalculateAndCache = onCalculateAndCache
                )

                ResultHeroCard(result = state.activeResult)
            }
        }
    }
}

@Composable
private fun ConverterInputsColumn(
    state: ConverterUiState,
    onSelectFuelGrade: (FuelGrade) -> Unit,
    onTempChange: (String) -> Unit,
    onStepTemp: (Int) -> Unit,
    onDensityChange: (String) -> Unit,
    onStepDensity: (Int) -> Unit,
    onCalculateAndCache: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.label_fuel_grade),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val grades = FuelGrade.entries
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FuelGradeCard(
                grade = grades[0],
                isSelected = state.selectedGrade == grades[0],
                onClick = { onSelectFuelGrade(grades[0]) },
                modifier = Modifier.weight(1f)
            )
            FuelGradeCard(
                grade = grades[1],
                isSelected = state.selectedGrade == grades[1],
                onClick = { onSelectFuelGrade(grades[1]) },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FuelGradeCard(
                grade = grades[2],
                isSelected = state.selectedGrade == grades[2],
                onClick = { onSelectFuelGrade(grades[2]) },
                modifier = Modifier.weight(1f)
            )
            FuelGradeCard(
                grade = grades[3],
                isSelected = state.selectedGrade == grades[3],
                onClick = { onSelectFuelGrade(grades[3]) },
                modifier = Modifier.weight(1f)
            )
        }
    }

    TelemetryParameterField(
        label = stringResource(R.string.label_observed_temp),
        value = state.tempInput,
        unit = stringResource(R.string.unit_celsius),
        onValueChange = onTempChange,
        onDecrement = { onStepTemp(-1) },
        onIncrement = { onStepTemp(1) },
        stepLabel = "1",
        testTagPrefix = "observed_temp"
    )

    TelemetryParameterField(
        label = stringResource(R.string.label_observed_density),
        value = state.densityInput,
        unit = stringResource(R.string.unit_kg_m3),
        onValueChange = onDensityChange,
        onDecrement = { onStepDensity(-1) },
        onIncrement = { onStepDensity(1) },
        stepLabel = "1",
        testTagPrefix = "observed_density"
    )

    PrimaryAmberCtaButton(
        text = stringResource(R.string.cta_calculate_density),
        onClick = onCalculateAndCache,
        testTag = "calculate_density_button",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ResultHeroCard(
    result: ConversionResult,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = NavyPrimary.copy(alpha = 0.08f),
                spotColor = NavyPrimary.copy(alpha = 0.08f)
            )
            .testTag("result_hero_card"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CardPerimeter)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_standard_density_15c),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AstmVerificationBadge(
                    status = result.complianceStatus,
                    headline = result.statusHeadline
                )
            }

            if (result.complianceStatus == ComplianceStatus.INVALID_INPUT) {
                Text(
                    text = "---.- kg/m³",
                    style = TelemetryTypography.DataDisplay,
                    color = AlertRed
                )
                Text(
                    text = result.statusDetail,
                    style = MaterialTheme.typography.bodySmall,
                    color = AlertRed
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f", result.density15CKgM3),
                        style = TelemetryTypography.DataDisplay,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("result_density_15c_text")
                    )
                    Text(
                        text = "kg/m³",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
        }
    }
}
