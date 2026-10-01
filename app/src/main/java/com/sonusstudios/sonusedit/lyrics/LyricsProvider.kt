package com.sonusstudios.sonusedit.lyrics

interface LyricsProvider {
    suspend fun bestMatch(metadata: LyricsMetadata): LyricsMatch?
    suspend fun search(trackName: String, artistName: String? = null): List<LyricsMatch>
}
