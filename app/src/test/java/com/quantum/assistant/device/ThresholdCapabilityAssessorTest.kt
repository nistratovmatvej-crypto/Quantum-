package com.quantum.assistant.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThresholdCapabilityAssessorTest {
    private val assessor = ThresholdCapabilityAssessor()
    private fun input(total: Long, avail: Long = total / 2, abis: List<String> = listOf("arm64-v8a"),
                      thermal: Int? = 0, battery: Int? = 80, saver: Boolean? = false) =
        CapabilityInput(total, avail, 34, abis, thermal, battery, saver)
    private fun CapabilityAssessment.verdict(t: LocalTier) = tiers.first { it.tier == t }.verdict

    @Test fun lowRamRecommendsMeteorOnly() {
        val a = assessor.assess(input(3_000))
        assertEquals(Verdict.MEETS, a.verdict(LocalTier.METEOR)); assertEquals(Verdict.BELOW, a.verdict(LocalTier.COMET))
        assertEquals(LocalTier.METEOR, a.recommended)
    }

    @Test fun midRamRecommendsComet() = assertEquals(LocalTier.COMET, assessor.assess(input(6_000)).recommended)

    @Test fun highRamRecommendsNova() = assertEquals(LocalTier.NOVA, assessor.assess(input(12_000)).recommended)

    @Test fun veryLowRamRecommendsNothing() {
        val a = assessor.assess(input(1_500))
        assertNull(a.recommended); assertTrue(a.tiers.all { it.verdict == Verdict.BELOW })
    }

    @Test fun thirtyTwoBitCannotRunCometOrNova() {
        val a = assessor.assess(input(12_000, abis = listOf("armeabi-v7a")))
        assertEquals(Verdict.BELOW, a.verdict(LocalTier.COMET)); assertEquals(Verdict.BELOW, a.verdict(LocalTier.NOVA))
        assertEquals(LocalTier.METEOR, a.recommended)
    }

    @Test fun lowFreeRamIsMarginalNotBelow() {
        val a = assessor.assess(input(12_000, avail = 500))
        assertEquals(Verdict.MARGINAL, a.verdict(LocalTier.NOVA))
        assertEquals(LocalTier.METEOR, a.recommended) // every tier is marginal, so the lightest is advised
    }

    @Test fun severeThermalDowngradesToMarginal() {
        val a = assessor.assess(input(12_000, thermal = 3))
        assertEquals(Verdict.MARGINAL, a.verdict(LocalTier.NOVA)); assertTrue(a.tiers.first().reasons.any { it.contains("hot") })
    }

    @Test fun batterySaverAndLowBatteryDowngrade() {
        assertEquals(Verdict.MARGINAL, assessor.assess(input(12_000, saver = true)).verdict(LocalTier.COMET))
        assertEquals(Verdict.MARGINAL, assessor.assess(input(12_000, battery = 10)).verdict(LocalTier.COMET))
    }

    @Test fun unreadableSignalsAreReportedAsNotMeasured() {
        val a = assessor.assess(input(6_000, thermal = null, battery = null, saver = null))
        assertTrue(a.notMeasured.any { it.startsWith("Thermal") }); assertTrue(a.notMeasured.any { it.startsWith("Battery level") })
        assertTrue(a.notMeasured.contains("GPU / NPU capability")) // never claimed as measured
        assertEquals(Verdict.MEETS, a.verdict(LocalTier.COMET)) // unknown signals do not penalize
    }
}
