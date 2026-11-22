package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.TankLogEntryEntity
import com.example.domain.ComplianceStatus
import com.example.domain.FuelGrade
import com.example.ui.TankLogbookUiState
import com.example.ui.components.AstmVerificationBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CardPerimeter
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TelemetryTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun TankLogbookScreen(
    state: TankLogbookUiState,
    onSelectGradeFilter: (FuelGrade?) -> Unit,
    onToggleAlertsOnly: () -> Unit,
    onDeleteLog: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.US) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, CardPerimeter),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "COMPLIANT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${state.compliantCount}",
                        style = TelemetryTypography.DataMetric,
                        color = ComplianceGreen
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ALERTS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${state.alertCount}",
                        style = TelemetryTypography.DataMetric,
                        color = if (state.alertCount > 0) AlertRed else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.alertsOnly,
                    onClick = onToggleAlertsOnly,
                    label = { Text("Alerts Only") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AlertRed,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("log_filter_alerts")
                )
            }
            item {
                FilterChip(
                    selected = state.selectedGradeFilter == null && !state.alertsOnly,
                    onClick = { onSelectGradeFilter(null) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("log_filter_all")
                )
            }
            items(FuelGrade.entries) { grade ->
                FilterChip(
                    selected = state.selectedGradeFilter == grade,
                    onClick = {
                        onSelectGradeFilter(if (state.selectedGradeFilter == grade) null else grade)
                    },
                    label = { Text(grade.shortCode) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("log_filter_${grade.id.lowercase()}")
                )
            }
        }

        if (state.logs.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, CardPerimeter),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "No tank logs yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tap \"Calculate 15°C Density\" in the Converter to save readings offline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 112.dp)
            ) {
                items(state.logs, key = { it.id }) { entry ->
                    TankLogCard(
                        entry = entry,
                        formattedTime = dateFormat.format(Date(entry.timestampMillis)),
                        onDelete = { onDeleteLog(entry.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TankLogCard(
    entry: TankLogEntryEntity,
    formattedTime: String,
    onDelete: () -> Unit
) {
    val grade = FuelGrade.fromId(entry.fuelGradeId)
    val status = runCatching { ComplianceStatus.valueOf(entry.complianceStatusName) }
        .getOrDefault(if (entry.isCompliant) ComplianceStatus.COMPLIANT else ComplianceStatus.OUT_OF_SPEC)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CardPerimeter),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tank_log_card_${entry.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = grade.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_tank_log_${entry.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = stringResource(R.string.cd_delete_log),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format(Locale.US, "%.1f", entry.density15CKgM3)} kg/m³",
                    style = TelemetryTypography.DataMetric,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AstmVerificationBadge(
                    status = status,
                    headline = when (status) {
                        ComplianceStatus.COMPLIANT -> "COMPLIANT"
                        ComplianceStatus.VARIANCE_BREACH -> "ALERT"
                        else -> "OUT OF SPEC"
                    }
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Obs Temp: ${entry.observedTempC.roundToInt()} °C",
                        style = TelemetryTypography.DataBadge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Obs Density: ${entry.observedDensityKgM3.roundToInt()} kg/m³",
                        style = TelemetryTypography.DataBadge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
