package com.shreyash.antitheft.ui.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.shreyash.antitheft.R
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme
import kotlinx.coroutines.launch

@Composable
fun PinEntryScreen(
    onPinVerified: () -> Unit,
    onVerifyPin: (String) -> Boolean,
) {
    var pin by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val spacing = dimensionResource(R.dimen.spacing_large)
    val spacingXlarge = dimensionResource(R.dimen.spacing_xlarge)
    val invalidMsg = stringResource(R.string.pin_invalid)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.pin_entry_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(spacingXlarge))

        OutlinedTextField(
            value = pin,
            onValueChange = {
                if (it.length <= 4) pin = it
            },
            label = { Text(stringResource(R.string.pin_entry_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing))

        Button(
            onClick = {
                if (pin.isEmpty()) return@Button
                if (onVerifyPin(pin)) {
                    onPinVerified()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(invalidMsg)
                    }
                    pin = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.unlock))
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}

@Preview(showBackground = true)
@Composable
private fun PinEntryScreenPreview() {
    AntiTheftAlarmTheme {
        PinEntryScreen(
            onPinVerified = {},
            onVerifyPin = { true }
        )
    }
}
