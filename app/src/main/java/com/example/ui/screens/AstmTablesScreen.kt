package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AstmReferenceRowEntity
import com.example.domain.FuelGrade
import com.example.ui.AstmTablesUiState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CardPerimeter
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RoyalSecondary
import com.example.ui.theme.TelemetryTypography
import java.util.Locale

@Composable
fun AstmTablesScreen(
    state: AstmTablesUiState,
    onSelectGradeFilter: (FuelGrade?) -> Unit,
    onSelectTempFilter: (Double?) -> Unit,
    onLoadRowIntoConverter: (AstmReferenceRowEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val tempOptions = listOf(10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Offline Cache Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "ASTM D1250-04 • Table 53B",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "${state.totalCachedRows} calibrated reference rows cached offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.CloudOff,
                    contentDescription = "Offline database available",
                    tint = Color(0xFFFFC928),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Grade Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.selectedGradeFilter == null,
                    onClick = { onSelectGradeFilter(null) },
                    label = { Text("All Grades") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("table_filter_all")
                )
            }
            items(FuelGrade.entries) { grade ->
                FilterChip(
                    selected = state.selectedGradeFilter == grade,
                    onClick = {
                        onSelectGradeFilter(if (state.selectedGradeFilter == grade) null else grade)
                    },
                    label = { Text(grade.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("table_filter_${grade.id.lowercase()}")
                )
            }
        }

        // Temperature Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.selectedTempFilter == null,
                    onClick = { onSelectTempFilter(null) },
                    label = { Text("All °C") },
                    modifier = Modifier.testTag("table_temp_all")
                )
            }
            items(tempOptions) { temp ->
                FilterChip(
                    selected = state.selectedTempFilter == temp,
                    onClick = {
                        onSelectTempFilter(if (state.selectedTempFilter == temp) null else temp)
                    },
                    label = { Text("${temp.toInt()}°C") },
                    modifier = Modifier.testTag("table_temp_${temp.toInt()}")
                )
            }
        }

        // Tabular Matrix Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, CardPerimeter),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GRADE",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.85f)
                    )
                    Text(
                        text = "OBS °C",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.75f)
                    )
                    Text(
                        text = "OBS ρ",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.85f)
                    )
                    Text(
                        text = "15°C ρ",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.95f)
                    )
                    Text(
                        text = "VCF",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.85f)
                    )
                    Text(
                        text = "LOAD",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(0.45f)
                    )
                }
                HorizontalDivider(color = CardPerimeter)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 176.dp)
                ) {
                    items(state.rows, key = { it.id }) { row ->
                        val grade = FuelGrade.fromId(row.fuelGradeId)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .clickable { onLoadRowIntoConverter(row) }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .testTag("astm_row_${row.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(0.85f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (row.isInStandardSpec) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                    contentDescription = if (row.isInStandardSpec) "In spec" else "Boundary spec",
                                    tint = if (row.isInStandardSpec) ComplianceGreen else AlertRed,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = grade.shortCode,
                                    style = TelemetryTypography.DataBadge,
                                    color = RoyalSecondary
                                )
                            }
                            Text(
                                text = "${row.observedTempC.toInt()}",
                                style = TelemetryTypography.DataRow,
                                modifier = Modifier.weight(0.75f)
                            )
                            Text(
                                text = "${row.observedDensityKgM3.toInt()}",
                                style = TelemetryTypography.DataRow,
                                modifier = Modifier.weight(0.85f)
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f", row.density15CKgM3),
                                style = TelemetryTypography.DataRow.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(0.95f)
                            )
                            Text(
                                text = String.format(Locale.US, "%.4f", row.vcf),
                                style = TelemetryTypography.DataBadge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(0.85f)
                            )
                            Box(
                                modifier = Modifier.weight(0.45f),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.OpenInNew,
                                    contentDescription = "Load into converter",
                                    tint = RoyalSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = CardPerimeter)
                    }
                }
            }
        }
    }
}
