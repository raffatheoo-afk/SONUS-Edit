package com.sonusstudios.sonusedit.media

import android.net.Uri

data class SimpleExportRequest(
    val inputUri: Uri,
    val outputPath: String,
)

interface EditorEngine {
    fun exportSingleAsset(
        request: SimpleExportRequest,
        onCompleted: (String) -> Unit,
        onError: (Throwable) -> Unit,
    )

    fun cancel()
}
