package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.CalibrationSettingsEntity
import com.example.domain.Astm53BCalculator
import com.example.ui.components.StepperPillButton
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardPerimeter
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RoyalSecondary
import com.example.ui.theme.TelemetryTypography

@Composable
fun CalibrationScreen(
    settings: CalibrationSettingsEntity,
    astmRowCount: Int,
    cachedConversionCount: Int,
    tankLogCount: Int,
    onToggleGlassHydrometer: (Boolean) -> Unit,
    onStepHydrometerOffset: (Double) -> Unit,
    onStepThermometerOffset: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, CardPerimeter),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = null,
                        tint = RoyalSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Instrument Calibration",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Glass Hydrometer Correction (HYC)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ASTM D1250 thermal glass compensation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.applyGlassHydrometerCorrection,
                        onCheckedChange = onToggleGlassHydrometer,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NavyPrimary,
                            checkedTrackColor = AmberAccent
                        ),
                        modifier = Modifier.testTag("hyc_toggle_switch")
                    )
                }

                HorizontalDivider(color = CardPerimeter)

                CalibrationStepperRow(
                    title = "Hydrometer Offset",
                    valueText = "${Astm53BCalculator.formatSigned(settings.hydrometerOffsetKgM3)} kg/m³",
                    onDecrement = { onStepHydrometerOffset(-1.0) },
                    onIncrement = { onStepHydrometerOffset(1.0) },
                    testTagPrefix = "cal_hydrometer"
                )

                HorizontalDivider(color = CardPerimeter)

                CalibrationStepperRow(
                    title = "Thermometer Offset",
                    valueText = "${Astm53BCalculator.formatSigned(settings.thermometerOffsetC)} °C",
                    onDecrement = { onStepThermometerOffset(-1.0) },
                    onIncrement = { onStepThermometerOffset(1.0) },
                    testTagPrefix = "cal_thermometer"
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, CardPerimeter),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Offline Data Cache",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = settings.offlineTableVersion,
                    style = TelemetryTypography.DataBadge,
                    color = RoyalSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CacheStatColumn(label = "ASTM ROWS", value = "$astmRowCount")
                    CacheStatColumn(label = "CACHED", value = "$cachedConversionCount")
                    CacheStatColumn(label = "LOGS", value = "$tankLogCount")
                }
            }
        }
    }
}

@Composable
private fun CalibrationStepperRow(
    title: String,
    valueText: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    testTagPrefix: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valueText,
                style = TelemetryTypography.DataBadge,
                color = RoyalSecondary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StepperPillButton(
                text = "-1",
                onClick = onDecrement,
                testTag = "${testTagPrefix}_dec"
            )
            StepperPillButton(
                text = "+1",
                onClick = onIncrement,
                testTag = "${testTagPrefix}_inc"
            )
        }
    }
}

@Composable
private fun CacheStatColumn(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = TelemetryTypography.DataMetric,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
