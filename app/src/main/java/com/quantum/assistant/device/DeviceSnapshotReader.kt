package com.quantum.assistant.device

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager

/** Reads real values from Android. Anything unreadable becomes null rather than a guess. */
object DeviceSnapshotReader {
    fun read(context: Context): CapabilityInput {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mem = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        val battery = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)?.takeIf { it in 0..100 }
        return CapabilityInput(
            totalRamMb = mem.totalMem / BYTES_PER_MB,
            availableRamMb = mem.availMem / BYTES_PER_MB,
            sdkInt = Build.VERSION.SDK_INT,
            abis = Build.SUPPORTED_ABIS.toList(),
            thermalStatus = if (Build.VERSION.SDK_INT >= 29) pm?.currentThermalStatus else null,
            batteryPercent = battery,
            powerSaveMode = pm?.isPowerSaveMode
        )
    }
    private const val BYTES_PER_MB = 1024L * 1024L
}
