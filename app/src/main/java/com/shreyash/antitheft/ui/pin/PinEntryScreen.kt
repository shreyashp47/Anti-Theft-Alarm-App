package com.shreyash.antitheft.ui.pin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.shreyash.antitheft.security.PinManager.Companion.MAX_ATTEMPTS
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme
import kotlinx.coroutines.delay

private const val PIN_LENGTH = 4

@Composable
fun PinEntryScreen(
    onPinVerified: () -> Unit,
    onVerifyPin: (String) -> PinVerifyResult,
    isLockedOut: () -> Boolean,
    getRemainingLockoutMillis: () -> Long,
    getRemainingAttempts: () -> Int,
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var remainingAttempts by remember { mutableIntStateOf(MAX_ATTEMPTS) }
    var lockout by remember { mutableStateOf(false) }
    var lockoutRemaining by remember { mutableLongStateOf(0L) }

    LaunchedEffect(lockout) {
        if (!lockout) return@LaunchedEffect
        while (lockoutRemaining > 0) {
            delay(1000L)
            lockoutRemaining = getRemainingLockoutMillis()
            if (lockoutRemaining <= 0) {
                lockout = false
                remainingAttempts = MAX_ATTEMPTS
                pin = ""
                error = null
            }
        }
    }

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

        val shape = MaterialTheme.shapes.extraLarge

        if (lockout) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 120.dp)
                    .clip(shape),
                tint = MaterialTheme.colorScheme.error,
            )
        } else {
            Icon(
                imageVector = Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 120.dp)
                    .clip(shape),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (lockout) "Too Many Attempts" else "Enter PIN",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (lockout) {
            Text(
                text = "Locked for ${lockoutRemaining / 1000}s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = when {
                    remainingAttempts <= 2 && remainingAttempts > 0 ->
                        "$remainingAttempts attempts remaining"
                    else -> "Enter your 4-digit PIN to continue"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (remainingAttempts <= 2)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!lockout) {
            PinDotIndicator(
                pin = pin,
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

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = remainingAttempts < MAX_ATTEMPTS && !lockout,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { remainingAttempts.toFloat() / MAX_ATTEMPTS },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .height(4.dp)
                            .clip(CircleShape),
                        color = if (remainingAttempts <= 2)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        if (!lockout) {
            PinKeypad(
                onDigit = { digit ->
                    error = null
                    if (isLockedOut()) {
                        lockout = true
                        lockoutRemaining = getRemainingLockoutMillis()
                        return@PinKeypad
                    }
                    if (pin.length < PIN_LENGTH) {
                        pin += digit.toString()
                        if (pin.length == PIN_LENGTH) {
                            val result = onVerifyPin(pin)
                            if (result.success) {
                                onPinVerified()
                            } else {
                                remainingAttempts = result.remainingAttempts
                                pin = ""
                                if (result.isLockedOut) {
                                    lockout = true
                                    lockoutRemaining = getRemainingLockoutMillis()
                                } else {
                                    error = "Incorrect PIN"
                                }
                            }
                        }
                    }
                },
                onDelete = {
                    error = null
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

data class PinVerifyResult(
    val success: Boolean,
    val remainingAttempts: Int,
    val isLockedOut: Boolean,
)

@Preview(showBackground = true)
@Composable
private fun PinEntryScreenPreview() {
    AntiTheftAlarmTheme {
        PinEntryScreen(
            onPinVerified = {},
            onVerifyPin = { PinVerifyResult(false, 4, false) },
            isLockedOut = { false },
            getRemainingLockoutMillis = { 0L },
            getRemainingAttempts = { 5 }
        )
    }
}
