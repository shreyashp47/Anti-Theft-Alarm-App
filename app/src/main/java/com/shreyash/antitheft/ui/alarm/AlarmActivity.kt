package com.shreyash.antitheft.ui.alarm

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shreyash.antitheft.R
import com.shreyash.antitheft.data.EventLog
import com.shreyash.antitheft.receiver.ChargingReceiver
import com.shreyash.antitheft.security.PinManager
import com.shreyash.antitheft.service.AlarmPlayer
import com.shreyash.antitheft.service.PrefsManager
import com.shreyash.antitheft.ui.pin.PinDotIndicator
import com.shreyash.antitheft.ui.pin.PinKeypad
import com.shreyash.antitheft.ui.theme.AntiTheftAlarmTheme

class AlarmActivity : ComponentActivity() {

    private lateinit var pinManager: PinManager
    private lateinit var alarmPlayer: AlarmPlayer
    private lateinit var prefsManager: PrefsManager
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        pinManager = PinManager(this)
        alarmPlayer = AlarmPlayer(this)
        prefsManager = PrefsManager(this)
        val eventLog = EventLog(this)
        val alarmType = intent.getStringExtra(ChargingReceiver.EXTRA_ALARM_TYPE) ?: "unknown"

        alarmPlayer.play()
        eventLog.addEvent("alarm", "Alarm triggered: $alarmType")

        setContent {
            AntiTheftAlarmTheme {
                AlarmScreen(
                    pinManager = pinManager,
                    onPinVerified = {
                        alarmPlayer.stop()
                        countdownTimer?.cancel()
                        eventLog.addEvent("info", "Alarm dismissed by user")
                        finish()
                    },
                )
            }
        }
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        alarmPlayer.stop()
        super.onDestroy()
    }
}

@Composable
private fun AlarmScreen(
    pinManager: PinManager,
    onPinVerified: () -> Unit,
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableIntStateOf(0) }

    val spacingXlarge = dimensionResource(R.dimen.spacing_xlarge)
    val spacingLarge = dimensionResource(R.dimen.spacing_large)
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.alarm_triggered_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacingMedium))

        Text(
            text = stringResource(R.string.alarm_enter_pin),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(spacingXlarge))

        PinDotIndicator(
            pin = pin,
            maxLength = 4,
            isError = errorMessage != null,
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(spacingMedium))
            Text(
                text = errorMessage!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(spacingXlarge))

        PinKeypad(
            onDigit = { digit ->
                if (pin.length < 4) {
                    pin += digit.toString()
                    errorMessage = null
                }
                if (pin.length == 4) {
                    if (pinManager.verifyPin(pin)) {
                        onPinVerified()
                    } else {
                        errorMessage = context.getString(R.string.pin_invalid)
                        pin = ""
                    }
                }
            },
            onDelete = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                    errorMessage = null
                }
            }
        )
    }
}
