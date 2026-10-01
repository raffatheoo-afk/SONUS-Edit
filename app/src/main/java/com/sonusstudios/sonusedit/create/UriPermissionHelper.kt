package com.sonusstudios.sonusedit.create

import android.content.Context
import android.content.Intent
import android.net.Uri

object UriPermissionHelper {
    fun persistRead(context: Context, uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
    }
}
