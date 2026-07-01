package com.shreyash.antitheft.ui.pin

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@Composable
fun PinCreateScreen(
    onPinSet: () -> Unit,
    onSavePin: (String) -> Boolean,
) {
    var step by remember { mutableIntStateOf(0) }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val shape = MaterialTheme.shapes.extraLarge

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 120.dp)
                .clip(shape),
            tint = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = step to error,
            transitionSpec = {
                (slideInVertically { it } + fadeIn()).togetherWith(
                    slideOutVertically { -it } + fadeOut()
                )
            },
            label = "stepTransition"
        ) { (currentStep, currentError) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (currentStep == 0) "Create a PIN" else "Confirm PIN",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (currentStep == 0)
                        "Choose a 4-digit PIN to secure your app"
                    else
                        "Enter the same PIN again to confirm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                val currentPin = if (currentStep == 0) pin else confirmPin
                val isError = currentError != null

                PinDotIndicator(
                    pin = currentPin,
                    maxLength = PIN_LENGTH,
                    isError = isError
                )

                if (isError) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        PinKeypad(
            onDigit = { digit ->
                error = null
                if (step == 0) {
                    if (pin.length < PIN_LENGTH) {
                        pin += digit.toString()
                        if (pin.length == PIN_LENGTH) {
                            step = 1
                        }
                    }
                } else {
                    if (confirmPin.length < PIN_LENGTH) {
                        confirmPin += digit.toString()
                        if (confirmPin.length == PIN_LENGTH) {
                            if (pin == confirmPin) {
                                onSavePin(pin)
                                onPinSet()
                            } else {
                                error = "PINs do not match"
                                confirmPin = ""
                            }
                        }
                    }
                }
            },
            onDelete = {
                error = null
                if (step == 0) {
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                } else {
                    if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                    else {
                        step = 0
                        pin = ""
                    }
                }
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PinCreateScreenPreview() {
    AntiTheftAlarmTheme {
        PinCreateScreen(
            onPinSet = {},
            onSavePin = { true }
        )
    }
}
