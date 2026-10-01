package com.sonusstudios.sonusedit.create

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaDistributionPlannerTest {
    @Test
    fun maxImages_respectsFiveSecondMinimum() {
        assertEquals(3, MediaDistributionPlanner.maxImages(15))
        assertEquals(6, MediaDistributionPlanner.maxImages(30))
        assertEquals(13, MediaDistributionPlanner.maxImages(65))
        assertEquals(120, MediaDistributionPlanner.maxImages(600))
    }

    @Test
    fun distribution_usesFullDurationAndNeverDropsBelowMinimum() {
        val durations = MediaDistributionPlanner.durationsMs(durationSeconds = 65, imageCount = 13)
        assertEquals(65_000L, durations.sum())
        assertTrue(durations.all { it >= 5_000L })
    }

    @Test(expected = IllegalArgumentException::class)
    fun distribution_rejectsTooManyImages() {
        MediaDistributionPlanner.durationsMs(durationSeconds = 15, imageCount = 4)
    }
}
