package com.sonusstudios.sonusedit.create

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sonusEditDataStore by preferencesDataStore(name = "sonus_edit_preferences")

data class SavedEditPreferences(
    val signatureText: String = "",
    val signaturePhotoUri: String? = null,
    val fontPreset: FontPresetId = FontPresetId.DEFAULT,
)

class EditPreferences(private val context: Context) {
    private object Keys {
        val SignatureText = stringPreferencesKey("signature_text")
        val SignaturePhoto = stringPreferencesKey("signature_photo_uri")
        val FontPreset = stringPreferencesKey("font_preset")
    }

    val values: Flow<SavedEditPreferences> = context.sonusEditDataStore.data.map { prefs ->
        SavedEditPreferences(
            signatureText = prefs[Keys.SignatureText].orEmpty(),
            signaturePhotoUri = prefs[Keys.SignaturePhoto]?.takeIf { it.isNotBlank() },
            fontPreset = prefs[Keys.FontPreset]
                ?.let { runCatching { FontPresetId.valueOf(it) }.getOrNull() }
                ?: FontPresetId.DEFAULT,
        )
    }

    suspend fun saveSignature(text: String, photoUri: String?) {
        context.sonusEditDataStore.edit { prefs ->
            prefs[Keys.SignatureText] = text.trim()
            prefs[Keys.SignaturePhoto] = photoUri.orEmpty()
        }
    }

    suspend fun saveFont(fontPreset: FontPresetId) {
        context.sonusEditDataStore.edit { prefs ->
            prefs[Keys.FontPreset] = fontPreset.name
        }
    }
}
