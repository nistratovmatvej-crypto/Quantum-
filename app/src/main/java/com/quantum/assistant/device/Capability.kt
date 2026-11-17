package com.quantum.assistant.device

enum class LocalTier(val label: String) { METEOR("Quantum Meteor"), COMET("Quantum Comet"), NOVA("Quantum Nova") }

/** Raw facts about the device. Nullable fields mean "could not be read", never "zero". */
data class CapabilityInput(
    val totalRamMb: Long,
    val availableRamMb: Long,
    val sdkInt: Int,
    val abis: List<String>,
    /** android.os.PowerManager THERMAL_STATUS_* (0 none .. 6 shutdown); null below API 29 or if unreadable. */
    val thermalStatus: Int? = null,
    val batteryPercent: Int? = null,
    val powerSaveMode: Boolean? = null
)

enum class Verdict(val label: String) {
    MEETS("Meets requirements"), MARGINAL("Marginal"), BELOW("Below requirements")
}

data class TierAssessment(val tier: LocalTier, val verdict: Verdict, val reasons: List<String>)

data class CapabilityAssessment(
    val tiers: List<TierAssessment>,
    /** Highest tier that meets requirements, else Meteor if marginal, else null. Advisory only. */
    val recommended: LocalTier?,
    /** Signals that were readable and used. */
    val measured: List<String>,
    /** Signals NOT measured. Shown to the user so nothing is implied that wasn't checked. */
    val notMeasured: List<String>
)

/** Replaceable: a future implementation can check real model files, free storage and benchmark results. */
interface CapabilityAssessor {
    fun assess(input: CapabilityInput): CapabilityAssessment
}
