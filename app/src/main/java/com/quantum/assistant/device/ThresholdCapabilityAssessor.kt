package com.quantum.assistant.device

/**
 * Transparent rule-based assessor.
 * IMPORTANT: the numbers in [DEFAULT_REQUIREMENTS] are PLACEHOLDERS, not measurements of real model needs.
 * They must be replaced with figures from actual Meteor/Comet/Nova evaluation (roadmap Alpha 0.7).
 * This class does not and cannot measure GPU/NPU performance, storage, or inference speed.
 */
class ThresholdCapabilityAssessor(
    private val requirements: List<TierRequirements> = DEFAULT_REQUIREMENTS
) : CapabilityAssessor {

    data class TierRequirements(
        val tier: LocalTier, val minTotalRamMb: Long, val minAvailableRamMb: Long, val needs64Bit: Boolean
    )

    override fun assess(input: CapabilityInput): CapabilityAssessment {
        val tiers = requirements.map { assessTier(it, input) }
        val recommended = tiers.lastOrNull { it.verdict == Verdict.MEETS }?.tier
            ?: tiers.firstOrNull { it.verdict == Verdict.MARGINAL }?.tier
        val measured = mutableListOf("Total RAM", "Available RAM (changes constantly)", "Android version", "CPU ABI")
        val notMeasured = mutableListOf("GPU / NPU capability", "Free storage", "Real model speed or memory use")
        if (input.thermalStatus != null) measured += "Thermal status" else notMeasured += "Thermal status (not readable on this device)"
        if (input.batteryPercent != null) measured += "Battery level" else notMeasured += "Battery level (not readable)"
        if (input.powerSaveMode != null) measured += "Battery saver" else notMeasured += "Battery saver (not readable)"
        return CapabilityAssessment(tiers, recommended, measured, notMeasured)
    }

    private fun assessTier(r: TierRequirements, i: CapabilityInput): TierAssessment {
        val reasons = mutableListOf<String>()
        var verdict = Verdict.MEETS
        fun lower(to: Verdict, why: String) { if (to.ordinal > verdict.ordinal) verdict = to; reasons += why }

        if (i.totalRamMb < r.minTotalRamMb) lower(Verdict.BELOW, "Total RAM ${i.totalRamMb} MB < ${r.minTotalRamMb} MB")
        if (r.needs64Bit && i.abis.none { it == "arm64-v8a" || it == "x86_64" }) lower(Verdict.BELOW, "Needs a 64-bit CPU")
        if (verdict != Verdict.BELOW && i.availableRamMb < r.minAvailableRamMb)
            lower(Verdict.MARGINAL, "Only ${i.availableRamMb} MB RAM free now (wants ${r.minAvailableRamMb} MB)")
        if (verdict != Verdict.BELOW) {
            if ((i.thermalStatus ?: 0) >= THERMAL_SEVERE) lower(Verdict.MARGINAL, "Device is running hot")
            if (i.powerSaveMode == true) lower(Verdict.MARGINAL, "Battery saver is on")
            if ((i.batteryPercent ?: 100) in 0..14) lower(Verdict.MARGINAL, "Battery is low")
        }
        return TierAssessment(r.tier, verdict, reasons)
    }

    companion object {
        const val THERMAL_SEVERE = 3 // PowerManager.THERMAL_STATUS_SEVERE
        val DEFAULT_REQUIREMENTS = listOf(
            TierRequirements(LocalTier.METEOR, 2_500, 1_000, needs64Bit = false),
            TierRequirements(LocalTier.COMET, 5_000, 2_000, needs64Bit = true),
            TierRequirements(LocalTier.NOVA, 7_500, 3_500, needs64Bit = true)
        )
    }
}
