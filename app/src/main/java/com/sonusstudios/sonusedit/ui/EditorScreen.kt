package com.sonusstudios.sonusedit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonusstudios.sonusedit.designsystem.SonusEditColors
import com.sonusstudios.sonusedit.create.CreateProjectDraft
import com.sonusstudios.sonusedit.integration.IncomingIntentRouter

@Composable
fun EditorScreen(
    incomingRoute: IncomingIntentRouter.Route?,
    draft: CreateProjectDraft?,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SonusEditColors.Black)
            .padding(top = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(draft?.audioTitle ?: "Novo projeto", fontWeight = FontWeight.Bold)
                Text(projectSubtitle(draft, incomingRoute), color = SonusEditColors.TextMuted, fontSize = 12.sp)
            }
            Button(
                onClick = { },
                enabled = false,
                colors = ButtonDefaults.buttonColors(containerColor = SonusEditColors.Violet),
            ) { Text("EXPORTAR") }
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 18.dp)
                .background(Color.Black, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SonusEditColors.VioletBright, modifier = Modifier.size(44.dp))
                Spacer(Modifier.height(8.dp))
                Text("Projeto preparado", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    draft?.let { "${it.imageUris.size} imagem(s) • ${it.aspectRatio} • ${it.fontPreset.label}" }
                        ?: "Media3 Composition entra aqui",
                    color = SonusEditColors.TextMuted,
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SonusEditColors.Surface),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Subtitles, contentDescription = null, tint = SonusEditColors.Cyan)
                    Spacer(Modifier.size(8.dp))
                    Text("Letra sincronizada", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.Search, contentDescription = "Pesquisar", tint = SonusEditColors.TextMuted)
                }
                Text(
                    "Detecção automática por título + artista + duração. O trecho selecionado desloca os timestamps sozinho.",
                    color = SonusEditColors.TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        TimelinePlaceholder()
        ToolRail()
    }
}

@Composable
private fun TimelinePlaceholder() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
        Text("TIMELINE", color = SonusEditColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(SonusEditColors.SurfaceRaised, RoundedCornerShape(14.dp)),
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 30.dp, top = 12.dp)
                    .fillMaxWidth(.62f)
                    .height(22.dp)
                    .background(SonusEditColors.Violet.copy(alpha = .60f), RoundedCornerShape(7.dp))
            )
            Box(
                modifier = Modifier
                    .padding(start = 56.dp, top = 42.dp)
                    .fillMaxWidth(.72f)
                    .height(14.dp)
                    .background(SonusEditColors.Cyan.copy(alpha = .45f), RoundedCornerShape(7.dp))
            )
        }
    }
}

@Composable
private fun ToolRail() {
    val tools = listOf(
        Tool("Mídia", Icons.Default.AddPhotoAlternate),
        Tool("Áudio", Icons.Default.MusicNote),
        Tool("Letra", Icons.Default.Subtitles),
        Tool("Texto", Icons.Default.TextFields),
        Tool("Filtros", Icons.Default.FilterVintage),
        Tool("Ajustes", Icons.Default.Tune),
        Tool("Auto", Icons.Default.AutoAwesome),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SonusEditColors.Surface)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tools.forEach { tool ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 5.dp)) {
                Icon(tool.icon, contentDescription = tool.label, tint = SonusEditColors.Text)
                Spacer(Modifier.height(4.dp))
                Text(tool.label, fontSize = 11.sp, color = SonusEditColors.TextMuted)
            }
        }
    }
}

private data class Tool(val label: String, val icon: ImageVector)

private fun sourceLabel(route: IncomingIntentRouter.Route?): String = when (route) {
    is IncomingIntentRouter.Route.SonusTrack -> "SONUS Stream • ${route.trackId}"
    is IncomingIntentRouter.Route.SharedMedia -> "${route.uris.size} mídia(s) compartilhada(s)"
    else -> "Projeto local"
}


private fun projectSubtitle(
    draft: CreateProjectDraft?,
    route: IncomingIntentRouter.Route?,
): String {
    draft ?: return sourceLabel(route)
    val start = draft.segmentStartMs / 1_000L
    val duration = draft.segmentDurationMs / 1_000L
    return "Trecho ${start}s • duração ${duration}s • criação automática"
}
