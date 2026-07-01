package com.shreyash.antitheft.ui.pin

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme

private const val PIN_LENGTH = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinChangeScreen(
    onBack: () -> Unit,
    onChangePin: (String, String) -> Boolean,
    onPinChanged: () -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val stepTitles = listOf("Enter old PIN", "New PIN", "Confirm new PIN")
    val stepSubtitles = listOf(
        "Verify your identity with your current PIN",
        "Choose a new 4-digit PIN",
        "Enter the new PIN again to confirm"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change PIN") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 0) {
                            step--
                            error = null
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 120.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    (slideInVertically { it } + fadeIn()).togetherWith(
                        slideOutVertically { -it } + fadeOut()
                    )
                },
                label = "stepTransition"
            ) { currentStep ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stepTitles.getOrElse(currentStep) { "" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stepSubtitles.getOrElse(currentStep) { "" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val currentPin = when (step) {
                0 -> oldPin
                1 -> newPin
                else -> confirmPin
            }

            PinDotIndicator(
                pin = currentPin,
                maxLength = PIN_LENGTH,
                isError = error != null
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            PinKeypad(
                onDigit = { digit ->
                    error = null
                    when (step) {
                        0 -> {
                            if (oldPin.length < PIN_LENGTH) {
                                oldPin += digit.toString()
                                if (oldPin.length == PIN_LENGTH) step = 1
                            }
                        }
                        1 -> {
                            if (newPin.length < PIN_LENGTH) {
                                newPin += digit.toString()
                                if (newPin.length == PIN_LENGTH) step = 2
                            }
                        }
                        else -> {
                            if (confirmPin.length < PIN_LENGTH) {
                                confirmPin += digit.toString()
                                if (confirmPin.length == PIN_LENGTH) {
                                    if (newPin != confirmPin) {
                                        error = "PINs do not match"
                                        confirmPin = ""
                                    } else {
                                        val changed = onChangePin(oldPin, newPin)
                                        if (changed) onPinChanged()
                                        else {
                                            error = "Incorrect old PIN"
                                            step = 0
                                            oldPin = ""
                                            newPin = ""
                                            confirmPin = ""
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                onDelete = {
                    error = null
                    when (step) {
                        0 -> if (oldPin.isNotEmpty()) oldPin = oldPin.dropLast(1)
                        1 -> if (newPin.isNotEmpty()) newPin = newPin.dropLast(1)
                        else -> if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                    }
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PinChangeScreenPreview() {
    AntiTheftAlarmTheme {
        PinChangeScreen(
            onBack = {},
            onChangePin = { _, _ -> true },
            onPinChanged = {}
        )
    }
}
