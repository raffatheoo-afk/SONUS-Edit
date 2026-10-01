package com.sonusstudios.sonusedit.lyrics

import kotlinx.serialization.Serializable

@Serializable
data class LyricsMetadata(
    val trackName: String,
    val artistName: String,
    val albumName: String? = null,
    val durationSeconds: Int? = null,
)

@Serializable
data class LyricsMatch(
    val id: Long,
    val trackName: String,
    val artistName: String,
    val albumName: String? = null,
    val duration: Double? = null,
    val instrumental: Boolean = false,
    val plainLyrics: String? = null,
    val syncedLyrics: String? = null,
    val lyricsfile: String? = null,
) {
    val hasSyncedLyrics: Boolean get() = !syncedLyrics.isNullOrBlank()
}
