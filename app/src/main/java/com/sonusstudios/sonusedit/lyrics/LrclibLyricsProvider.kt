package com.sonusstudios.sonusedit.lyrics

import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Minimal LRCLIB client for the starter project.
 * Requests are serialized and slightly throttled to respect LRCLIB guidance.
 */
class LrclibLyricsProvider(
    private val json: Json = Json { ignoreUnknownKeys = true },
) : LyricsProvider {
    private val requestMutex = Mutex()
    private val baseUrl = "https://lrclib.net"

    override suspend fun bestMatch(metadata: LyricsMetadata): LyricsMatch? = requestMutex.withLock {
        delay(250)
        val params = buildList {
            add("track_name" to metadata.trackName)
            add("artist_name" to metadata.artistName)
            metadata.albumName?.takeIf { it.isNotBlank() }?.let { add("album_name" to it) }
            metadata.durationSeconds?.takeIf { it in 1..3600 }?.let { add("duration" to it.toString()) }
        }
        val response = get("/api/get", params) ?: return@withLock null
        json.decodeFromString<LyricsMatch>(response)
    }

    override suspend fun search(trackName: String, artistName: String?): List<LyricsMatch> = requestMutex.withLock {
        delay(250)
        val params = buildList {
            add("track_name" to trackName)
            artistName?.takeIf { it.isNotBlank() }?.let { add("artist_name" to it) }
        }
        val response = get("/api/search", params) ?: return@withLock emptyList()
        json.decodeFromString<List<LyricsMatch>>(response)
    }

    private suspend fun get(path: String, params: List<Pair<String, String>>): String? = withContext(Dispatchers.IO) {
        val query = params.joinToString("&") { (key, value) -> "${encode(key)}=${encode(value)}" }
        val url = URI.create("$baseUrl$path?$query").toURL()
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 8_000
            readTimeout = 12_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "SONUS-Edit/0.1.0 Android")
        }
        try {
            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK -> connection.inputStream.bufferedReader().use { it.readText() }
                HttpURLConnection.HTTP_NOT_FOUND -> null
                429 -> null // Future UI: surface rate-limit state and Retry-After.
                else -> null
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun encode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
}
