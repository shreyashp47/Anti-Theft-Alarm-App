package com.shreyash.antitheft.ui.pin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinDotIndicator(
    pin: String,
    maxLength: Int = 4,
    dotSize: Dp = 16.dp,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until maxLength) {
            val isFilled = i < pin.length
            val dotColor by animateColorAsState(
                targetValue = when {
                    isError && isFilled -> errorColor
                    isFilled -> primaryColor
                    else -> surfaceVariant
                },
                animationSpec = tween(200),
                label = "dotColor"
            )
            val dotScale by animateFloatAsState(
                targetValue = if (isFilled) 1f else 0.6f,
                animationSpec = spring(dampingRatio = 0.5f),
                label = "dotScale"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(dotSize)
                    .scale(dotScale)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

@Composable
fun PinKeypad(
    onDigit: (Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KeypadButton("1", { onDigit(1) }, Modifier.weight(1f))
            KeypadButton("2", { onDigit(2) }, Modifier.weight(1f))
            KeypadButton("3", { onDigit(3) }, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KeypadButton("4", { onDigit(4) }, Modifier.weight(1f))
            KeypadButton("5", { onDigit(5) }, Modifier.weight(1f))
            KeypadButton("6", { onDigit(6) }, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KeypadButton("7", { onDigit(7) }, Modifier.weight(1f))
            KeypadButton("8", { onDigit(8) }, Modifier.weight(1f))
            KeypadButton("9", { onDigit(9) }, Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            KeypadButton("0", { onDigit(0) }, Modifier.weight(1f))
            KeypadButton(
                label = "",
                onClick = onDelete,
                modifier = Modifier.weight(1f),
                isBackspace = true,
            )
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBackspace: Boolean = false,
) {
    val haptic = LocalHapticFeedback.current
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isBackspace) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Delete",
                tint = onSurface,
            )
        } else {
            Text(
                text = label,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
