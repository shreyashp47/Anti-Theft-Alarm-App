package com.shreyash.antitheft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shreyash.antitheft.security.PinManager
import com.shreyash.antitheft.ui.home.HomeScreen
import com.shreyash.antitheft.ui.permission.SettingsScreen
import com.shreyash.antitheft.ui.pin.PinChangeScreen
import com.shreyash.antitheft.ui.pin.PinCreateScreen
import com.shreyash.antitheft.ui.pin.PinEntryScreen
import com.shreyash.antitheft.ui.pin.PinVerifyResult
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme

class MainActivity : ComponentActivity() {

    private lateinit var pinManager: PinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pinManager = PinManager(this)

        setContent {
            AntiTheftAlarmTheme {
                AppNavigation(pinManager = pinManager)
            }
        }
    }
}

@Composable
private fun AppNavigation(pinManager: PinManager) {
    val navController = rememberNavController()
    val startDestination = if (pinManager.isPinSet()) "pin_entry" else "pin_create"

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("pin_create") {
                PinCreateScreen(
                    onPinSet = {
                        navController.navigate("home") {
                            popUpTo("pin_create") { inclusive = true }
                        }
                    },
                    onSavePin = { pin ->
                        pinManager.setPin(pin)
                        true
                    }
                )
            }
            composable("pin_entry") {
                PinEntryScreen(
                    onPinVerified = {
                        navController.navigate("home") {
                            popUpTo("pin_entry") { inclusive = true }
                        }
                    },
                    onVerifyPin = { pin ->
                        val success = pinManager.verifyPin(pin)
                        PinVerifyResult(
                            success = success,
                            remainingAttempts = pinManager.getRemainingAttempts(),
                            isLockedOut = pinManager.isLockedOut()
                        )
                    },
                    isLockedOut = { pinManager.isLockedOut() },
                    getRemainingLockoutMillis = { pinManager.getRemainingLockoutMillis() },
                    getRemainingAttempts = { pinManager.getRemainingAttempts() }
                )
            }
            composable("home") {
                HomeScreen(
                    onChangePin = {
                        navController.navigate("pin_change")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            composable("pin_change") {
                PinChangeScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onChangePin = { oldPin, newPin ->
                        pinManager.changePin(oldPin, newPin)
                    },
                    onPinChanged = {
                        navController.popBackStack()
                    }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
