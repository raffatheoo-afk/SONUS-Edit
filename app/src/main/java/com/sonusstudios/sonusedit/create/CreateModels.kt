package com.sonusstudios.sonusedit.create

import kotlinx.serialization.Serializable

@Serializable
data class CreateProjectDraft(
    val audioUri: String,
    val audioTitle: String,
    val audioArtist: String? = null,
    val audioDurationMs: Long,
    val segmentStartMs: Long,
    val segmentDurationMs: Long,
    val signatureText: String,
    val signaturePhotoUri: String? = null,
    val imageUris: List<String>,
    val fontPreset: FontPresetId,
    val aspectRatio: String = "9:16",
    val minimumImageDurationMs: Long = 5_000L,
    val fadeInMs: Long = 350L,
    val fadeOutMs: Long = 350L,
    val autoFillVertical: Boolean = true,
    val visualEffectPreset: String = "CURRENT",
)

@Serializable
enum class FontPresetId(val label: String) {
    DEFAULT("Padrão"),
    MODERN("Moderna"),
    ELEGANT("Elegante"),
    CLASSIC("Clássica"),
    MINIMAL("Minimal"),
    IMPACT("Impacto"),
    HANDWRITTEN("Manuscrita"),
    RETRO("Retrô"),
    NEON("Neon"),
    EDITORIAL("Editorial"),
}

data class DurationPreset(val seconds: Int, val label: String) {
    companion object {
        val all = listOf(
            DurationPreset(15, "15s"),
            DurationPreset(30, "30s"),
            DurationPreset(40, "40s"),
            DurationPreset(50, "50s"),
            DurationPreset(60, "60s"),
            DurationPreset(65, "65s"),
            DurationPreset(90, "1m30"),
            DurationPreset(120, "2m"),
            DurationPreset(180, "3m"),
            DurationPreset(300, "5m"),
            DurationPreset(600, "10m"),
        )
    }
}

object MediaDistributionPlanner {
    const val MIN_IMAGE_SECONDS = 5

    fun maxImages(durationSeconds: Int): Int =
        (durationSeconds / MIN_IMAGE_SECONDS).coerceAtLeast(1)

    fun durationsMs(durationSeconds: Int, imageCount: Int): List<Long> {
        require(durationSeconds > 0) { "durationSeconds must be > 0" }
        require(imageCount > 0) { "imageCount must be > 0" }
        require(imageCount <= maxImages(durationSeconds)) {
            "imageCount exceeds the 5-second minimum per image"
        }

        val totalMs = durationSeconds * 1_000L
        val base = totalMs / imageCount
        var remainder = totalMs % imageCount
        return List(imageCount) {
            base + if (remainder-- > 0) 1L else 0L
        }
    }
}
