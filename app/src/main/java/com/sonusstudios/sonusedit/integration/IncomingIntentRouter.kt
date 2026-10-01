package com.sonusstudios.sonusedit.integration

import android.content.Intent
import android.net.Uri

object IncomingIntentRouter {
    sealed interface Route {
        data object Home : Route
        data class SonusTrack(
            val trackId: String,
            val startMs: Long?,
            val durationMs: Long?,
        ) : Route
        data class SharedMedia(val uris: List<Uri>) : Route
    }

    fun parse(intent: Intent?): Route {
        intent ?: return Route.Home

        if (intent.action == Intent.ACTION_VIEW) {
            val data = intent.data
            if (data?.scheme == SonusLinkContract.SCHEME && data.host == SonusLinkContract.HOST_CREATE) {
                val trackId = data.getQueryParameter("trackId")?.trim().orEmpty()
                if (trackId.isNotEmpty()) {
                    return Route.SonusTrack(
                        trackId = trackId,
                        startMs = data.getQueryParameter("startMs")?.toLongOrNull(),
                        durationMs = data.getQueryParameter("durationMs")?.toLongOrNull(),
                    )
                }
            }
        }

        if (intent.action == Intent.ACTION_SEND) {
            @Suppress("DEPRECATION")
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (uri != null) return Route.SharedMedia(listOf(uri))
        }

        if (intent.action == Intent.ACTION_SEND_MULTIPLE) {
            @Suppress("DEPRECATION")
            val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM).orEmpty()
            if (uris.isNotEmpty()) return Route.SharedMedia(uris)
        }

        return Route.Home
    }
}
