package com.sonusstudios.sonusedit.model

import kotlinx.serialization.Serializable

@Serializable
data class EditorProject(
    val id: String,
    val name: String = "Novo projeto",
    val createdAtEpochMs: Long,
    val source: ProjectSource = ProjectSource.LOCAL,
    val sonusTrackId: String? = null,
    val audio: AudioTrack? = null,
    val clips: List<MediaClip> = emptyList(),
    val canvas: CanvasSpec = CanvasSpec(),
    val segmentStartMs: Long = 0L,
    val segmentDurationMs: Long? = null,
)

@Serializable
enum class ProjectSource { LOCAL, SONUS_STREAM, SHARED_MEDIA }

@Serializable
data class AudioTrack(
    val uri: String,
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val durationMs: Long? = null,
)

@Serializable
data class MediaClip(
    val id: String,
    val uri: String,
    val type: ClipType,
    val startMs: Long = 0L,
    val durationMs: Long? = null,
)

@Serializable
enum class ClipType { VIDEO, IMAGE, AUDIO }

@Serializable
data class CanvasSpec(
    val ratio: CanvasRatio = CanvasRatio.VERTICAL_9_16,
    val backgroundArgb: Long = 0xFF000000,
)

@Serializable
enum class CanvasRatio { VERTICAL_9_16, LANDSCAPE_16_9, SQUARE_1_1, PORTRAIT_4_5 }
