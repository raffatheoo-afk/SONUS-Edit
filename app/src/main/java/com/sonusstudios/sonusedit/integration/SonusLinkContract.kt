package com.sonusstudios.sonusedit.integration

import android.net.Uri

object SonusLinkContract {
    const val SCHEME = "sonusedit"
    const val HOST_CREATE = "create"
    const val SONUS_PACKAGE = "com.sonusstudios.streampulse"
    const val SONUS_EDIT_PACKAGE = "com.sonusstudios.sonusedit"

    /**
     * Public/non-secret contract between SONUS and SONUS Edit.
     * Never place auth tokens, pCloud direct URLs, Firebase secrets or signed credentials in this URI.
     */
    fun createProjectUri(
        trackId: String,
        startMs: Long? = null,
        durationMs: Long? = null,
    ): Uri = Uri.Builder()
        .scheme(SCHEME)
        .authority(HOST_CREATE)
        .appendQueryParameter("source", "sonus")
        .appendQueryParameter("trackId", trackId)
        .apply {
            startMs?.let { appendQueryParameter("startMs", it.toString()) }
            durationMs?.let { appendQueryParameter("durationMs", it.toString()) }
        }
        .build()
}
