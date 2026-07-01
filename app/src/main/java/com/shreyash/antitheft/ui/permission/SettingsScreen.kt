package com.shreyash.antitheft.ui.permission

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shreyash.antitheft.R
import com.shreyash.antitheft.service.PrefsManager
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme
import com.shreyash.antitheft.util.PermissionManager
import com.shreyash.antitheft.util.PermissionRequirement
import com.shreyash.antitheft.util.toSettingsIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val prefsManager = remember { PrefsManager(context) }
    val required = remember { PermissionManager.requiredPermissions }
    val optional = remember { PermissionManager.optionalPermissions }
    val allPermissions = remember { required + optional }

    var grantStates by remember {
        mutableStateOf(allPermissions.map { PermissionManager.isPermissionGranted(context, it) })
    }
    var pendingRequirement by remember { mutableStateOf<PermissionRequirement?>(null) }
    var infoRequirement by remember { mutableStateOf<PermissionRequirement?>(null) }
    var chargingGuardEnabled by remember { mutableStateOf(prefsManager.isChargingGuardEnabled) }

    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)

    val runtimeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val idx = pendingRequirement?.let { allPermissions.indexOf(it) } ?: -1
        if (idx >= 0) {
            val newStates = grantStates.toMutableList()
            newStates[idx] = granted
            grantStates = newStates
        }
        pendingRequirement = null
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val idx = pendingRequirement?.let { allPermissions.indexOf(it) } ?: -1
        if (idx >= 0) {
            val req = allPermissions[idx]
            val granted = PermissionManager.isPermissionGranted(context, req)
            val newStates = grantStates.toMutableList()
            newStates[idx] = granted
            grantStates = newStates
        }
        pendingRequirement = null
    }

    if (infoRequirement != null) {
        AlertDialog(
            onDismissRequest = { infoRequirement = null },
            title = { Text(text = stringResource(R.string.permissions_info_title)) },
            text = { Text(text = stringResource(infoRequirement!!.rationaleKey)) },
            confirmButton = {
                TextButton(onClick = { infoRequirement = null }) {
                    Text(text = "OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = dimensionResource(R.dimen.spacing_large))
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(spacingSmall),
        ) {
            Spacer(modifier = Modifier.height(spacingMedium))

            Text(
                text = stringResource(R.string.settings_features_section),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(spacingSmall))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacingSmall),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.spacing_medium), vertical = spacingSmall),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.feature_charging_guard),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = stringResource(R.string.feature_charging_guard_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = chargingGuardEnabled,
                        onCheckedChange = { enabled ->
                            chargingGuardEnabled = enabled
                            prefsManager.isChargingGuardEnabled = enabled
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacingMedium))

            Text(
                text = stringResource(R.string.settings_permissions_section),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(spacingSmall))

            allPermissions.forEachIndexed { index, requirement ->
                PermissionRow(
                    title = stringResource(requirement.titleKey),
                    isGranted = grantStates.getOrElse(index) { false },
                    onGrant = {
                        pendingRequirement = requirement
                        when (requirement) {
                            is PermissionRequirement.Runtime -> {
                                runtimeLauncher.launch(requirement.permission)
                            }
                            is PermissionRequirement.SettingsIntent,
                            is PermissionRequirement.DeviceAdmin,
                            -> {
                                val intent = requirement.toSettingsIntent(context.packageName)
                                if (intent != null) settingsLauncher.launch(intent)
                            }
                        }
                    },
                    onInfo = { infoRequirement = requirement },
                )
            }

            Spacer(modifier = Modifier.height(spacingMedium))
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    isGranted: Boolean,
    onGrant: () -> Unit,
    onInfo: () -> Unit,
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacingSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.spacing_medium), vertical = spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = onInfo) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.permissions_info_title),
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(modifier = Modifier.width(spacingSmall))

            if (isGranted) {
                Text(
                    text = stringResource(R.string.permissions_granted),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                OutlinedButton(onClick = onGrant) {
                    Text(text = stringResource(R.string.permissions_grant))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AntiTheftAlarmTheme {
        SettingsScreen(onBack = {})
    }
}
