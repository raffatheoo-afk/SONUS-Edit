// REFERÊNCIA APENAS. NÃO APLICADA AO SONUS CODE122.
// Quando Rafa autorizar integração no SONUS, um botão "Criar no SONUS Edit" pode usar:

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun openTrackInSonusEdit(context: Context, trackId: String, startMs: Long? = null, durationMs: Long? = null) {
    val uri = Uri.Builder()
        .scheme("sonusedit")
        .authority("create")
        .appendQueryParameter("source", "sonus")
        .appendQueryParameter("trackId", trackId)
        .apply {
            startMs?.let { appendQueryParameter("startMs", it.toString()) }
            durationMs?.let { appendQueryParameter("durationMs", it.toString()) }
        }
        .build()

    val intent = Intent(Intent.ACTION_VIEW, uri)
        .setPackage("com.sonusstudios.sonusedit")
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // Futuro: abrir página oficial de instalação do SONUS Edit.
    }
}
