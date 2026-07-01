package com.shreyash.antitheft.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.shreyash.antitheft.BuildConfig
import com.shreyash.antitheft.R
import com.shreyash.antitheft.data.EventLog
import com.shreyash.antitheft.receiver.ChargingReceiver
import com.shreyash.antitheft.service.AlarmForegroundService
import com.shreyash.antitheft.service.PrefsManager
import com.shreyash.antitheft.ui.alarm.AlarmActivity
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChangePin: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val context = LocalContext.current
    val prefsManager = remember { PrefsManager(context) }
    var isArmed by remember { mutableStateOf(prefsManager.isArmed) }

    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val spacingXxlarge = dimensionResource(R.dimen.spacing_xxlarge)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
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
                onCheckedChange = { armed ->
                    isArmed = armed
                    prefsManager.isArmed = armed
                    if (armed) {
                        val intent = Intent(context, AlarmForegroundService::class.java)
                        context.startForegroundService(intent)
                    } else {
                        val intent = Intent(context, AlarmForegroundService::class.java)
                        context.stopService(intent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacingSmall))

            Text(
                text = if (isArmed) stringResource(R.string.armed) else stringResource(R.string.disarmed),
                style = MaterialTheme.typography.titleMedium
            )

            if (BuildConfig.DEBUG) {
                Spacer(modifier = Modifier.weight(1f))

                val eventLog = remember { EventLog(context) }
                var refreshKey by remember { mutableStateOf(0) }
                val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
                val lastDisconnect = if (refreshKey >= 0) prefsManager.lastDisconnectTime else prefsManager.lastDisconnectTime
                val lastEvents = eventLog.getEvents().take(5)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacingSmall),
                    shape = RoundedCornerShape(spacingSmall),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(modifier = Modifier.padding(spacingSmall)) {
                        Text("── DEBUG ──", style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.height(2.dp))
                        Text("Armed: ${prefsManager.isArmed}  |  Guard: ${prefsManager.isChargingGuardEnabled}", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        if (lastDisconnect > 0) {
                            Text("Last unplug: ${dateFormat.format(Date(lastDisconnect))}", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        } else {
                            Text("Last unplug: —", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        }
                        lastEvents.forEach { event ->
                            val time = dateFormat.format(Date(event.timestamp))
                            Text("[${time}] ${event.description}", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, maxLines = 1)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = {
                                    prefsManager.lastDisconnectTime = System.currentTimeMillis()
                                    eventLog.addEvent("alarm", "Manual trigger: Charging Guard")
                                    val i = Intent(context, AlarmActivity::class.java).apply {
                                        putExtra(ChargingReceiver.EXTRA_ALARM_TYPE, ChargingReceiver.ALARM_TYPE_CHARGING)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(i)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("TEST ALARM", style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = { refreshKey++ },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("REFRESH", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AntiTheftAlarmTheme {
        HomeScreen()
    }
}
