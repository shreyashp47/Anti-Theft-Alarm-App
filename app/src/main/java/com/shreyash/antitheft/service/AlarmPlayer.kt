package com.shreyash.antitheft.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import com.shreyash.antitheft.R

class AlarmPlayer(private val appContext: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var isLooping = false

    fun play(loop: Boolean = true) {
        isLooping = loop
        stop()

        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxAlarm = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)

        for (i in 0 until maxAlarm) {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_ALARM,
                AudioManager.ADJUST_RAISE,
                0
            )
        }

        audioManager.adjustStreamVolume(
            AudioManager.STREAM_ALARM,
            AudioManager.ADJUST_UNMUTE,
            0
        )

        val uri = Uri.parse("android.resource://${appContext.packageName}/${R.raw.alarm}")

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(appContext, uri)
                prepare()
                setVolume(1.0f, 1.0f)
                isLooping = isLooping
                start()
            }
        } catch (e: Exception) {
            try {
                mediaPlayer?.release()
            } catch (_: Exception) {
            }
            mediaPlayer = tryFallback(audioManager, uri)
        }
    }

    private fun tryFallback(audioManager: AudioManager, uri: Uri): MediaPlayer? {
        return try {
            val maxMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            for (i in 0 until maxMusic) {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    0
                )
            }
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(appContext, uri)
                prepare()
                setVolume(1.0f, 1.0f)
                isLooping = isLooping
                start()
            }
        } catch (_: Exception) {
            null
        }
    }

    fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (_: Exception) {
        }
        mediaPlayer = null
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying ?: false
        } catch (_: Exception) {
            false
        }
    }
}
