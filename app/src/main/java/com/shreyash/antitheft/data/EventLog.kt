package com.shreyash.antitheft.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class EventLog(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun addEvent(type: String, description: String) {
        val events = getEvents().toMutableList()
        events.add(0, AlarmEvent(type = type, timestamp = System.currentTimeMillis(), description = description))
        val max = if (events.size > MAX_EVENTS) MAX_EVENTS else events.size
        saveEvents(events.take(max))
    }

    fun getEvents(): List<AlarmEvent> {
        val json = prefs.getString(KEY_EVENTS, "[]") ?: "[]"
        val arr = JSONArray(json)
        return (0 until arr.length()).map { i ->
            val obj = arr.getJSONObject(i)
            AlarmEvent(
                type = obj.getString("type"),
                timestamp = obj.getLong("timestamp"),
                description = obj.getString("description"),
            )
        }
    }

    private fun saveEvents(events: List<AlarmEvent>) {
        val arr = JSONArray()
        events.forEach { e ->
            arr.put(JSONObject().apply {
                put("type", e.type)
                put("timestamp", e.timestamp)
                put("description", e.description)
            })
        }
        prefs.edit().putString(KEY_EVENTS, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "event_log"
        private const val KEY_EVENTS = "events"
        private const val MAX_EVENTS = 100
    }
}
