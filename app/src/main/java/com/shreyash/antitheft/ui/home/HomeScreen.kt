package com.shreyash.antitheft.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.shreyash.antitheft.R
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme

@Composable
fun HomeScreen() {
    var isArmed by remember { mutableStateOf(false) }

    val spacing = dimensionResource(R.dimen.spacing_large)
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val spacingXxlarge = dimensionResource(R.dimen.spacing_xxlarge)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(spacingSmall))

        Text(
            text = stringResource(R.string.app_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(spacingXxlarge))

        Switch(
            checked = isArmed,
            onCheckedChange = { isArmed = it }
        )

        Spacer(modifier = Modifier.height(spacingSmall))

        Text(
            text = if (isArmed) stringResource(R.string.armed) else stringResource(R.string.disarmed),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AntiTheftAlarmTheme {
        HomeScreen()
    }
}
