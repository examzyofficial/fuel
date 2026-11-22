package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ComplianceStatus
import com.example.domain.FuelGrade
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedBg
import com.example.ui.theme.AlertRedBorder
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberDeep
import com.example.ui.theme.CardPerimeter
import com.example.ui.theme.ComplianceGreen
import com.example.ui.theme.ComplianceGreenBg
import com.example.ui.theme.ComplianceGreenBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RoyalSecondary
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.TelemetryTypography

@Composable
fun FuelGradeCard(
    grade: FuelGrade,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surface,
        animationSpec = spring(),
        label = "gradeCardBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
        animationSpec = spring(),
        label = "gradeCardFg"
    )
    val borderColor = if (isSelected) RoyalSecondary else CardPerimeter
    val icon: ImageVector = when (grade) {
        FuelGrade.PETROL_MS -> Icons.Filled.LocalGasStation
        FuelGrade.DIESEL_HSD -> Icons.Filled.WaterDrop
        FuelGrade.PREMIUM_SPEED -> Icons.Filled.Speed
        FuelGrade.BIO_DIESEL -> Icons.Filled.Science
    }

    Surface(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag("fuel_grade_card_${grade.id.lowercase()}")
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = NavyPrimary.copy(alpha = 0.08f),
                spotColor = NavyPrimary.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) Color.White.copy(alpha = 0.14f)
                            else SurfaceContainerHigh
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = grade.displayName,
                        tint = if (isSelected) AmberAccent else RoyalSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) AmberAccent else SurfaceContainerHigh
                        )
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = grade.shortCode,
                        style = TelemetryTypography.DataBadge,
                        color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = grade.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TelemetryParameterField(
    label: String,
    value: String,
    unit: String,
    onValueChange: (String) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    stepLabel: String,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = NavyPrimary.copy(alpha = 0.05f),
                spotColor = NavyPrimary.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, CardPerimeter)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TelemetryTypography.DataInput.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(RoyalSecondary),
                        modifier = Modifier
                            .width(120.dp)
                            .testTag("${testTagPrefix}_input")
                    )

                    Text(
                        text = unit,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepperPillButton(
                    text = "-$stepLabel",
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDecrement()
                    },
                    testTag = "${testTagPrefix}_decrement_button"
                )
                StepperPillButton(
                    text = "+$stepLabel",
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onIncrement()
                    },
                    testTag = "${testTagPrefix}_increment_button"
                )
            }
        }
    }
}

@Composable
fun StepperPillButton(
    text: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(width = 52.dp, height = 48.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = TelemetryTypography.DataBadge.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun PrimaryAmberCtaButton(
    text: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(),
        label = "ctaScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .height(54.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = AmberAccent.copy(alpha = 0.45f),
                spotColor = AmberAccent.copy(alpha = 0.45f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(AmberAccent, AmberDeep)
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = NavyPrimary
        )
    }
}

@Composable
fun AstmVerificationBadge(
    status: ComplianceStatus,
    headline: String,
    modifier: Modifier = Modifier
) {
    val isCompliant = status == ComplianceStatus.COMPLIANT
    val bgColor = if (isCompliant) ComplianceGreenBg else AlertRedBg
    val fgColor = if (isCompliant) ComplianceGreen else AlertRed
    val borderColor = if (isCompliant) ComplianceGreenBorder else AlertRedBorder
    val icon = if (isCompliant) Icons.Filled.CheckCircle else Icons.Filled.Error

    Surface(
        modifier = modifier.testTag("astm_verification_badge"),
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = headline,
                tint = fgColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = headline,
                style = TelemetryTypography.DataBadge,
                color = fgColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
