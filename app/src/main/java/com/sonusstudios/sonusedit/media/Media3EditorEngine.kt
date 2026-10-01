package com.sonusstudios.sonusedit.media

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer

/**
 * Bootstrap engine only. Multi-track Composition compilation is intentionally separated
 * from this class and will be introduced after the project/timeline model is frozen.
 */
@OptIn(UnstableApi::class)
class Media3EditorEngine(private val context: Context) : EditorEngine {
    private var transformer: Transformer? = null

    override fun exportSingleAsset(
        request: SimpleExportRequest,
        onCompleted: (String) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        val localTransformer = Transformer.Builder(context)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: androidx.media3.transformer.Composition, exportResult: ExportResult) {
                    onCompleted(request.outputPath)
                }

                override fun onError(
                    composition: androidx.media3.transformer.Composition,
                    exportResult: ExportResult,
                    exportException: ExportException,
                ) {
                    onError(exportException)
                }
            })
            .build()

        transformer = localTransformer
        val item = EditedMediaItem.Builder(MediaItem.fromUri(request.inputUri)).build()
        localTransformer.start(item, request.outputPath)
    }

    override fun cancel() {
        transformer?.cancel()
        transformer = null
    }
}
