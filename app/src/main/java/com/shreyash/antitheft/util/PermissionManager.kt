package com.shreyash.antitheft.util

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.shreyash.antitheft.R

sealed class PermissionRequirement {
    abstract val rationaleKey: Int
    abstract val titleKey: Int

    data class Runtime(
        val permission: String,
        override val rationaleKey: Int,
        override val titleKey: Int,
    ) : PermissionRequirement()

    data class SettingsIntent(
        val action: String,
        override val rationaleKey: Int,
        override val titleKey: Int,
    ) : PermissionRequirement()

    data class DeviceAdmin(
        override val rationaleKey: Int,
        override val titleKey: Int,
    ) : PermissionRequirement()
}

object PermissionManager {
    val requiredPermissions: List<PermissionRequirement> = listOf(
        PermissionRequirement.Runtime(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            rationaleKey = R.string.permission_notifications_rationale,
            titleKey = R.string.permission_name_notifications,
        ),
        PermissionRequirement.SettingsIntent(
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            rationaleKey = R.string.permission_battery_rationale,
            titleKey = R.string.permission_name_battery,
        ),
    )

    val optionalPermissions: List<PermissionRequirement> = listOf(
        PermissionRequirement.Runtime(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            rationaleKey = R.string.permission_location_rationale,
            titleKey = R.string.permission_name_location,
        ),
        PermissionRequirement.Runtime(
            permission = Manifest.permission.CAMERA,
            rationaleKey = R.string.permission_camera_rationale,
            titleKey = R.string.permission_name_camera,
        ),
        PermissionRequirement.Runtime(
            permission = Manifest.permission.READ_PHONE_STATE,
            rationaleKey = R.string.permission_phone_rationale,
            titleKey = R.string.permission_name_phone,
        ),
        PermissionRequirement.SettingsIntent(
            action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            rationaleKey = R.string.permission_overlay_rationale,
            titleKey = R.string.permission_name_overlay,
        ),
        PermissionRequirement.DeviceAdmin(
            rationaleKey = R.string.permission_device_admin_rationale,
            titleKey = R.string.permission_name_device_admin,
        ),
    )

    fun isPermissionGranted(context: Context, requirement: PermissionRequirement): Boolean {
        return when (requirement) {
            is PermissionRequirement.Runtime -> {
                ContextCompat.checkSelfPermission(context, requirement.permission) ==
                    PackageManager.PERMISSION_GRANTED
            }
            is PermissionRequirement.SettingsIntent -> {
                when (requirement.action) {
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Settings.canDrawOverlays(context)
                        } else true
                    }
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) true
                        else {
                            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                            pm.isIgnoringBatteryOptimizations(context.packageName)
                        }
                    }
                    else -> false
                }
            }
            is PermissionRequirement.DeviceAdmin -> {
                val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as
                    DevicePolicyManager
                val cn = ComponentName(
                    context,
                    com.shreyash.antitheft.receiver.DeviceAdminReceiver::class.java
                )
                dpm.isAdminActive(cn)
            }
        }
    }

    fun isAllRequiredGranted(context: Context): Boolean {
        return requiredPermissions.all { isPermissionGranted(context, it) }
    }
}

fun PermissionRequirement.toSettingsIntent(packageName: String): Intent? {
    return when (this) {
        is PermissionRequirement.SettingsIntent -> {
            Intent(action).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        is PermissionRequirement.DeviceAdmin -> {
            val cn = ComponentName(
                packageName,
                "com.shreyash.antitheft.receiver.DeviceAdminReceiver"
            )
            Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Device Admin is required to prevent unauthorized disabling of the app."
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        else -> null
    }
}
