package com.sonusstudios.sonusedit.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.sonusstudios.sonusedit.R
import com.sonusstudios.sonusedit.create.AudioMetadata
import com.sonusstudios.sonusedit.create.AudioMetadataReader
import com.sonusstudios.sonusedit.create.CreateProjectDraft
import com.sonusstudios.sonusedit.create.DurationPreset
import com.sonusstudios.sonusedit.create.EditPreferences
import com.sonusstudios.sonusedit.create.FontPresetId
import com.sonusstudios.sonusedit.create.MediaDistributionPlanner
import com.sonusstudios.sonusedit.create.SavedEditPreferences
import com.sonusstudios.sonusedit.create.UriPermissionHelper
import com.sonusstudios.sonusedit.designsystem.SonusEditColors
import com.sonusstudios.sonusedit.integration.IncomingIntentRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

private enum class CreateStep(val label: String) {
    MUSIC("Música"),
    SIGNATURE("Assinatura"),
    IMAGES("Imagens"),
    STYLE("Estilo"),
    REVIEW("Revisão"),
}

@Composable
fun CreateFlowScreen(
    incomingRoute: IncomingIntentRouter.Route?,
    onBack: () -> Unit,
    onCreateDraft: (CreateProjectDraft) -> Unit,
) {
    val context = LocalContext.current
    val preferences = remember(context) { EditPreferences(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val savedPreferences by preferences.values.collectAsState(initial = SavedEditPreferences())

    var step by remember { mutableStateOf(CreateStep.MUSIC) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var metadata by remember { mutableStateOf<AudioMetadata?>(null) }
    var selectedDuration by remember { mutableStateOf(DurationPreset.all[1]) }
    var segmentStartMs by remember { mutableStateOf(0L) }
    var signatureText by remember { mutableStateOf("") }
    var signaturePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var saveSignatureDefault by remember { mutableStateOf(true) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var fontPreset by remember { mutableStateOf(FontPresetId.DEFAULT) }
    var saveFontDefault by remember { mutableStateOf(true) }
    var notice by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(savedPreferences) {
        if (signatureText.isBlank()) signatureText = savedPreferences.signatureText
        if (signaturePhotoUri == null) {
            signaturePhotoUri = savedPreferences.signaturePhotoUri?.let(Uri::parse)
        }
        if (fontPreset == FontPresetId.DEFAULT) fontPreset = savedPreferences.fontPreset
    }

    LaunchedEffect(incomingRoute) {
        val shared = incomingRoute as? IncomingIntentRouter.Route.SharedMedia ?: return@LaunchedEffect
        val audios = shared.uris.filter { context.contentResolver.getType(it)?.startsWith("audio/") == true }
        val images = shared.uris.filter { context.contentResolver.getType(it)?.startsWith("image/") == true }
        if (audioUri == null && audios.isNotEmpty()) audioUri = audios.first()
        if (images.isNotEmpty()) imageUris = (imageUris + images).distinct().take(MediaDistributionPlanner.maxImages(selectedDuration.seconds))
    }

    LaunchedEffect(audioUri) {
        val uri = audioUri ?: run {
            metadata = null
            return@LaunchedEffect
        }
        metadata = withContext(Dispatchers.IO) {
            runCatching { AudioMetadataReader.read(context, uri) }.getOrNull()
        }
        segmentStartMs = 0L
        val duration = metadata?.durationMs ?: 0L
        if (duration > 0 && selectedDuration.seconds * 1_000L > duration) {
            selectedDuration = DurationPreset.all.lastOrNull { it.seconds * 1_000L <= duration }
                ?: DurationPreset.all.first()
        }
    }

    val imageLimit = MediaDistributionPlanner.maxImages(selectedDuration.seconds)
    val segmentDurationMs = selectedDuration.seconds * 1_000L
    val signatureReady = signatureText.isNotBlank() || signaturePhotoUri != null
    val musicReady = audioUri != null && metadata?.durationMs?.let { it >= segmentDurationMs } == true
    val imagesReady = imageUris.isNotEmpty() && imageUris.size <= imageLimit

    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            UriPermissionHelper.persistRead(context, it)
            audioUri = it
        }
    }
    val signaturePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            UriPermissionHelper.persistRead(context, it)
            signaturePhotoUri = it
        }
    }
    val imagesPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { UriPermissionHelper.persistRead(context, it) }
        val merged = (imageUris + uris).distinct()
        if (merged.size > imageLimit) {
            imageUris = merged.take(imageLimit)
            notice = "Limite para ${selectedDuration.label}: $imageLimit imagens (mínimo de 5s por imagem)."
        } else {
            imageUris = merged
        }
    }

    val player = remember { ExoPlayer.Builder(context).build() }
    var isPlaying by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }
    LaunchedEffect(audioUri) {
        isPlaying = false
        player.stop()
        audioUri?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
        }
    }
    LaunchedEffect(isPlaying, segmentStartMs, segmentDurationMs) {
        if (!isPlaying) return@LaunchedEffect
        val end = segmentStartMs + segmentDurationMs
        while (isPlaying) {
            if (player.currentPosition >= end) {
                player.pause()
                player.seekTo(segmentStartMs)
                isPlaying = false
                break
            }
            delay(100)
        }
    }

    fun selectDuration(preset: DurationPreset) {
        val trackDuration = metadata?.durationMs ?: 0L
        if (trackDuration > 0 && preset.seconds * 1_000L > trackDuration) {
            notice = "A música selecionada é menor que ${preset.label}."
            return
        }
        selectedDuration = preset
        val newLimit = MediaDistributionPlanner.maxImages(preset.seconds)
        if (imageUris.size > newLimit) {
            imageUris = imageUris.take(newLimit)
            notice = "A duração foi reduzida. Mantivemos as primeiras $newLimit imagens."
        }
        val maxStart = (trackDuration - preset.seconds * 1_000L).coerceAtLeast(0L)
        segmentStartMs = segmentStartMs.coerceAtMost(maxStart)
    }

    val canContinue = when (step) {
        CreateStep.MUSIC -> musicReady
        CreateStep.SIGNATURE -> signatureReady
        CreateStep.IMAGES -> imagesReady
        CreateStep.STYLE -> true
        CreateStep.REVIEW -> musicReady && signatureReady && imagesReady
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SonusEditColors.Black)
            .padding(top = 18.dp),
    ) {
        CreateHeader(onBack = onBack)
        StepTabs(current = step, onSelect = { target ->
            if (target.ordinal <= step.ordinal) step = target
        })

        notice?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = SonusEditColors.Violet.copy(alpha = .16f)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(message, modifier = Modifier.weight(1f), color = SonusEditColors.Text, fontSize = 12.sp)
                    IconButton(onClick = { notice = null }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                when (step) {
                    CreateStep.MUSIC -> MusicStep(
                        audioUri = audioUri,
                        metadata = metadata,
                        selectedDuration = selectedDuration,
                        segmentStartMs = segmentStartMs,
                        isPlaying = isPlaying,
                        onPickMusic = { audioPicker.launch(arrayOf("audio/*")) },
                        onDuration = ::selectDuration,
                        onStartChange = { segmentStartMs = it },
                        onFineAdjust = { seconds ->
                            val maxStart = ((metadata?.durationMs ?: segmentDurationMs) - segmentDurationMs).coerceAtLeast(0L)
                            segmentStartMs = (segmentStartMs + seconds * 1_000L).coerceIn(0L, maxStart)
                            if (isPlaying) {
                                player.seekTo(segmentStartMs)
                            }
                        },
                        onPlayPause = {
                            if (audioUri != null) {
                                if (isPlaying) {
                                    player.pause()
                                    isPlaying = false
                                } else {
                                    val end = segmentStartMs + segmentDurationMs
                                    if (player.currentPosition < segmentStartMs || player.currentPosition >= end) {
                                        player.seekTo(segmentStartMs)
                                    }
                                    player.play()
                                    isPlaying = true
                                }
                            }
                        },
                    )
                    CreateStep.SIGNATURE -> SignatureStep(
                        text = signatureText,
                        photoUri = signaturePhotoUri,
                        saveAsDefault = saveSignatureDefault,
                        onText = { signatureText = it },
                        onPickPhoto = { signaturePicker.launch(arrayOf("image/*")) },
                        onSaveDefault = { saveSignatureDefault = it },
                    )
                    CreateStep.IMAGES -> ImagesStep(
                        images = imageUris,
                        limit = imageLimit,
                        durationLabel = selectedDuration.label,
                        onAdd = { imagesPicker.launch(arrayOf("image/*")) },
                        onRemove = { uri -> imageUris = imageUris - uri },
                    )
                    CreateStep.STYLE -> StyleStep(
                        fontPreset = fontPreset,
                        saveAsDefault = saveFontDefault,
                        onFont = { fontPreset = it },
                        onSaveDefault = { saveFontDefault = it },
                    )
                    CreateStep.REVIEW -> ReviewStep(
                        metadata = metadata,
                        duration = selectedDuration,
                        segmentStartMs = segmentStartMs,
                        signatureText = signatureText,
                        signaturePhotoUri = signaturePhotoUri,
                        imageCount = imageUris.size,
                        fontPreset = fontPreset,
                    )
                }
            }
        }

        BottomFlowBar(
            step = step,
            canContinue = canContinue,
            onPrevious = {
                if (step == CreateStep.MUSIC) onBack() else step = CreateStep.entries[step.ordinal - 1]
            },
            onNext = {
                when (step) {
                    CreateStep.MUSIC -> step = CreateStep.SIGNATURE
                    CreateStep.SIGNATURE -> {
                        if (saveSignatureDefault) {
                            scope.launch {
                                preferences.saveSignature(signatureText, signaturePhotoUri?.toString())
                            }
                        }
                        step = CreateStep.IMAGES
                    }
                    CreateStep.IMAGES -> step = CreateStep.STYLE
                    CreateStep.STYLE -> {
                        if (saveFontDefault) {
                            scope.launch { preferences.saveFont(fontPreset) }
                        }
                        step = CreateStep.REVIEW
                    }
                    CreateStep.REVIEW -> {
                        val uri = audioUri
                        val meta = metadata
                        if (uri != null && meta != null) {
                            onCreateDraft(
                                CreateProjectDraft(
                                    audioUri = uri.toString(),
                                    audioTitle = meta.title,
                                    audioArtist = meta.artist,
                                    audioDurationMs = meta.durationMs,
                                    segmentStartMs = segmentStartMs,
                                    segmentDurationMs = segmentDurationMs,
                                    signatureText = signatureText.trim(),
                                    signaturePhotoUri = signaturePhotoUri?.toString(),
                                    imageUris = imageUris.map { it.toString() },
                                    fontPreset = fontPreset,
                                )
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun CreateHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
        }
        Image(
            painter = painterResource(R.drawable.sonus_edit_logo),
            contentDescription = "SONUS Edit",
            modifier = Modifier.size(52.dp).clip(CircleShape),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("SONUS ", fontWeight = FontWeight.ExtraBold, fontSize = 23.sp)
                Text("Edit", fontWeight = FontWeight.ExtraBold, fontSize = 23.sp, color = SonusEditColors.Cyan)
            }
            Text("Criação automática vertical • 9:16", color = SonusEditColors.TextMuted, fontSize = 12.sp)
        }
        Card(
            colors = CardDefaults.cardColors(containerColor = SonusEditColors.SurfaceRaised),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text("9:16", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StepTabs(current: CreateStep, onSelect: (CreateStep) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CreateStep.entries.forEachIndexed { index, step ->
            val selected = step == current
            val available = step.ordinal <= current.ordinal
            Card(
                modifier = Modifier.clickable(enabled = available) { onSelect(step) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) SonusEditColors.Violet else SonusEditColors.SurfaceRaised
                ),
                shape = RoundedCornerShape(18.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${index + 1}", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(step.label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SonusEditColors.Surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SonusEditColors.Violet.copy(alpha = .18f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = SonusEditColors.Cyan)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(subtitle, color = SonusEditColors.TextMuted, fontSize = 12.sp)
                }
            }
            content()
        }
    }
}

@Composable
private fun MusicStep(
    audioUri: Uri?,
    metadata: AudioMetadata?,
    selectedDuration: DurationPreset,
    segmentStartMs: Long,
    isPlaying: Boolean,
    onPickMusic: () -> Unit,
    onDuration: (DurationPreset) -> Unit,
    onStartChange: (Long) -> Unit,
    onFineAdjust: (Int) -> Unit,
    onPlayPause: () -> Unit,
) {
    SectionCard(Icons.Default.MusicNote, "Música e trecho", "Escolha a música antes de qualquer outra etapa.") {
        Button(
            onClick = onPickMusic,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SonusEditColors.Violet),
        ) {
            Icon(Icons.Default.MusicNote, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (audioUri == null) "ESCOLHER MÚSICA" else "TROCAR MÚSICA")
        }

        metadata?.let { meta ->
            Card(colors = CardDefaults.cardColors(containerColor = SonusEditColors.SurfaceRaised)) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Text(meta.title, fontWeight = FontWeight.Bold)
                    Text(meta.artist ?: "Artista não informado", color = SonusEditColors.TextMuted, fontSize = 12.sp)
                    Text("Duração ${formatTime(meta.durationMs)}", color = SonusEditColors.Cyan, fontSize = 12.sp)
                }
            }
        }

        Text("Duração do vídeo", fontWeight = FontWeight.SemiBold)
        DurationPreset.all.chunked(4).forEach { rowPresets ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowPresets.forEach { preset ->
                    val enabled = metadata?.durationMs?.let { it >= preset.seconds * 1_000L } ?: true
                    FilterChip(
                        selected = selectedDuration == preset,
                        onClick = { onDuration(preset) },
                        enabled = enabled,
                        label = { Text(preset.label) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(4 - rowPresets.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        val trackDuration = metadata?.durationMs ?: 0L
        val segmentDurationMs = selectedDuration.seconds * 1_000L
        val maxStartMs = (trackDuration - segmentDurationMs).coerceAtLeast(0L)
        Text("Início do trecho", fontWeight = FontWeight.SemiBold)
        Slider(
            value = segmentStartMs.coerceAtMost(maxStartMs).toFloat(),
            onValueChange = { onStartChange(it.roundToLong()) },
            valueRange = 0f..maxStartMs.coerceAtLeast(1L).toFloat(),
            enabled = audioUri != null && maxStartMs > 0,
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onPlayPause,
                enabled = audioUri != null,
                modifier = Modifier
                    .size(52.dp)
                    .background(SonusEditColors.Cyan.copy(alpha = .16f), CircleShape),
            ) {
                Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Ouvir trecho", tint = SonusEditColors.Cyan)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("${formatTime(segmentStartMs)} → ${formatTime(segmentStartMs + segmentDurationMs)}", fontWeight = FontWeight.Bold)
                Text("O play reproduz somente o trecho selecionado.", color = SonusEditColors.TextMuted, fontSize = 11.sp)
            }
        }

        Text("Ajuste fino", fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(-10, -5, -1, 1, 5, 10).forEach { seconds ->
                OutlinedButton(onClick = { onFineAdjust(seconds) }, modifier = Modifier.weight(1f)) {
                    Text(if (seconds > 0) "+${seconds}" else seconds.toString(), fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun SignatureStep(
    text: String,
    photoUri: Uri?,
    saveAsDefault: Boolean,
    onText: (String) -> Unit,
    onPickPhoto: () -> Unit,
    onSaveDefault: (Boolean) -> Unit,
) {
    SectionCard(Icons.Default.Person, "Assinatura", "Configure uma vez e reutilize automaticamente.") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(SonusEditColors.SurfaceRaised)
                    .clickable { onPickPhoto() },
                contentAlignment = Alignment.Center,
            ) {
                if (photoUri != null) {
                    UriThumbnail(photoUri, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Foto da assinatura", tint = SonusEditColors.Cyan)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onText,
                    label = { Text("Nome / usuário") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Text("Texto ou foto libera a próxima etapa.", color = SonusEditColors.TextMuted, fontSize = 11.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Save, contentDescription = null, tint = SonusEditColors.Cyan)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Salvar como padrão", fontWeight = FontWeight.SemiBold)
                Text("Usar automaticamente nos próximos vídeos.", color = SonusEditColors.TextMuted, fontSize = 11.sp)
            }
            Switch(checked = saveAsDefault, onCheckedChange = onSaveDefault)
        }
        if (text.isNotBlank() || photoUri != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SonusEditColors.Cyan)
                Spacer(Modifier.width(8.dp))
                Text("Assinatura configurada.", color = SonusEditColors.Cyan, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ImagesStep(
    images: List<Uri>,
    limit: Int,
    durationLabel: String,
    onAdd: () -> Unit,
    onRemove: (Uri) -> Unit,
) {
    SectionCard(Icons.Default.Image, "Imagens", "Selecione as mídias. O tempo é distribuído automaticamente.") {
        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth(),
            enabled = images.size < limit,
            colors = ButtonDefaults.buttonColors(containerColor = SonusEditColors.Violet),
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ADICIONAR DA GALERIA")
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoMiniCard("Selecionadas", "${images.size}/$limit", Modifier.weight(1f))
            InfoMiniCard("Duração", durationLabel, Modifier.weight(1f))
            InfoMiniCard("Mínimo", "5s por imagem", Modifier.weight(1f))
        }

        if (images.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = SonusEditColors.SurfaceRaised)) {
                Text(
                    "Nenhuma imagem selecionada. A ordem escolhida será a ordem do vídeo.",
                    modifier = Modifier.padding(16.dp),
                    color = SonusEditColors.TextMuted,
                    fontSize = 12.sp,
                )
            }
        } else {
            Text("Galeria selecionada", fontWeight = FontWeight.SemiBold)
            images.take(20).chunked(4).forEachIndexed { rowIndex, rowImages ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowImages.forEachIndexed { colIndex, uri ->
                        val number = rowIndex * 4 + colIndex + 1
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(9f / 16f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SonusEditColors.SurfaceRaised),
                        ) {
                            UriThumbnail(uri, Modifier.fillMaxSize(), ContentScale.Crop)
                            Text(
                                number.toString(),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .background(Color.Black.copy(alpha = .62f), CircleShape)
                                    .padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            IconButton(
                                onClick = { onRemove(uri) },
                                modifier = Modifier.align(Alignment.TopEnd).size(30.dp),
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remover", tint = Color.White)
                            }
                        }
                    }
                    repeat(4 - rowImages.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            if (images.size > 20) {
                Text(
                    "Mostrando as primeiras 20 de ${images.size} imagens. Todas as ${images.size} entram no vídeo.",
                    color = SonusEditColors.Cyan,
                    fontSize = 11.sp,
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = SonusEditColors.Cyan.copy(alpha = .08f)),
            shape = RoundedCornerShape(18.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Automação vertical 9:16", fontWeight = FontWeight.Bold, color = SonusEditColors.Cyan)
                Text("• mínimo de 5s por imagem", color = SonusEditColors.TextMuted, fontSize = 12.sp)
                Text("• duração dividida igualmente entre as imagens", color = SonusEditColors.TextMuted, fontSize = 12.sp)
                Text("• leve zoom/crop automático para preencher a tela sem barras pretas", color = SonusEditColors.TextMuted, fontSize = 12.sp)
                Text("• fade in + fade out em todas as imagens", color = SonusEditColors.TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun InfoMiniCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = SonusEditColors.SurfaceRaised)) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = SonusEditColors.TextMuted, fontSize = 10.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StyleStep(
    fontPreset: FontPresetId,
    saveAsDefault: Boolean,
    onFont: (FontPresetId) -> Unit,
    onSaveDefault: (Boolean) -> Unit,
) {
    SectionCard(Icons.Default.Style, "Estilo", "As 10 fontes oficiais desta primeira versão.") {
        FontPresetId.entries.chunked(2).forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { preset ->
                    FontCard(
                        preset = preset,
                        selected = preset == fontPreset,
                        onClick = { onFont(preset) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Save, contentDescription = null, tint = SonusEditColors.Cyan)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Salvar fonte como padrão", fontWeight = FontWeight.SemiBold)
                Text("A próxima criação já abre com esta fonte.", color = SonusEditColors.TextMuted, fontSize = 11.sp)
            }
            Switch(checked = saveAsDefault, onCheckedChange = onSaveDefault)
        }

        Card(colors = CardDefaults.cardColors(containerColor = SonusEditColors.Violet.copy(alpha = .12f))) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Efeito visual", fontWeight = FontWeight.Bold)
                Text("Efeito atual mantido nesta versão + fade in/out automático entre imagens.", color = SonusEditColors.TextMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun FontCard(
    preset: FontPresetId,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderBrush = if (preset == FontPresetId.NEON) {
        Brush.linearGradient(listOf(SonusEditColors.VioletBright, SonusEditColors.Cyan))
    } else {
        Brush.linearGradient(listOf(
            if (selected) SonusEditColors.VioletBright else SonusEditColors.SurfaceRaised,
            if (selected) SonusEditColors.Cyan else SonusEditColors.SurfaceRaised,
        ))
    }
    Card(
        modifier = modifier
            .border(1.dp, borderBrush, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected) SonusEditColors.Violet.copy(alpha = .15f) else SonusEditColors.SurfaceRaised
        ),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                if (preset == FontPresetId.IMPACT) "AA" else "Aa",
                style = fontPreviewStyle(preset),
                color = if (preset == FontPresetId.NEON) SonusEditColors.Cyan else SonusEditColors.Text,
            )
            Spacer(Modifier.height(4.dp))
            Text(preset.label, color = SonusEditColors.TextMuted, fontSize = 11.sp)
        }
    }
}

private fun fontPreviewStyle(preset: FontPresetId): TextStyle = when (preset) {
    FontPresetId.DEFAULT -> TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 28.sp)
    FontPresetId.MODERN -> TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 28.sp, letterSpacing = 1.sp)
    FontPresetId.ELEGANT -> TextStyle(fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 30.sp)
    FontPresetId.CLASSIC -> TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 28.sp)
    FontPresetId.MINIMAL -> TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Light, fontSize = 27.sp, letterSpacing = 2.sp)
    FontPresetId.IMPACT -> TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, fontSize = 25.sp, letterSpacing = (-1).sp)
    FontPresetId.HANDWRITTEN -> TextStyle(fontFamily = FontFamily.Cursive, fontStyle = FontStyle.Italic, fontSize = 32.sp)
    FontPresetId.RETRO -> TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 27.sp)
    FontPresetId.NEON -> TextStyle(fontFamily = FontFamily.Cursive, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, fontSize = 31.sp)
    FontPresetId.EDITORIAL -> TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 30.sp, letterSpacing = .5.sp)
}

@Composable
private fun ReviewStep(
    metadata: AudioMetadata?,
    duration: DurationPreset,
    segmentStartMs: Long,
    signatureText: String,
    signaturePhotoUri: Uri?,
    imageCount: Int,
    fontPreset: FontPresetId,
) {
    SectionCard(Icons.Default.AutoAwesome, "Revisão", "Confira o projeto antes de abrir o editor.") {
        ReviewLine("Música", metadata?.title ?: "Não selecionada")
        ReviewLine("Trecho", "${formatTime(segmentStartMs)} → ${formatTime(segmentStartMs + duration.seconds * 1_000L)}")
        ReviewLine("Duração", duration.label)
        ReviewLine("Assinatura", signatureText.ifBlank { if (signaturePhotoUri != null) "Somente imagem" else "Não configurada" })
        ReviewLine("Imagens", "$imageCount selecionada(s)")
        ReviewLine("Fonte", fontPreset.label)
        ReviewLine("Formato", "9:16 vertical")
        ReviewLine("Preenchimento", "Zoom/crop automático sem barras pretas")
        ReviewLine("Transições", "Fade in + fade out automáticos")
        ReviewLine("Efeito", "Efeito atual")
    }
}

@Composable
private fun ReviewLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, color = SonusEditColors.TextMuted, fontSize = 12.sp, modifier = Modifier.width(110.dp))
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BottomFlowBar(
    step: CreateStep,
    canContinue: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SonusEditColors.Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OutlinedButton(onClick = onPrevious, modifier = Modifier.weight(.40f)) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
            Text(if (step == CreateStep.MUSIC) "Sair" else "Voltar")
        }
        Button(
            onClick = onNext,
            enabled = canContinue,
            modifier = Modifier.weight(.60f),
            colors = ButtonDefaults.buttonColors(containerColor = SonusEditColors.Violet),
        ) {
            Text(if (step == CreateStep.REVIEW) "CRIAR PROJETO" else "AVANÇAR")
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun UriThumbnail(
    uri: Uri,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
                var sample = 1
                while ((bounds.outWidth / sample) > 512 || (bounds.outHeight / sample) > 512) sample *= 2
                val options = BitmapFactory.Options().apply { inSampleSize = sample.coerceAtLeast(1) }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    if (bitmap != null) {
        Image(bitmap = bitmap!!, contentDescription = null, modifier = modifier, contentScale = contentScale)
    } else {
        Box(modifier = modifier.background(SonusEditColors.SurfaceRaised), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Image, contentDescription = null, tint = SonusEditColors.TextMuted)
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms.coerceAtLeast(0L) / 1_000L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
