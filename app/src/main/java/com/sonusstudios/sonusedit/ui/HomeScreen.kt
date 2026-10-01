package com.sonusstudios.sonusedit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonusstudios.sonusedit.R
import com.sonusstudios.sonusedit.designsystem.SonusEditColors

@Composable
fun HomeScreen(onCreate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SonusEditColors.Black)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.sonus_edit_logo),
                contentDescription = "SONUS Edit",
                modifier = Modifier.size(72.dp).clip(CircleShape),
            )
            Spacer(Modifier.size(14.dp))
            Column {
                Text("SONUS", fontSize = 13.sp, color = SonusEditColors.Cyan, fontWeight = FontWeight.Bold)
                Text("Edit", fontSize = 36.sp, color = SonusEditColors.Text, fontWeight = FontWeight.ExtraBold)
            }
        }

        Text(
            "Criação automática vertical primeiro; refinamento manual depois.",
            style = MaterialTheme.typography.bodyLarge,
            color = SonusEditColors.TextMuted,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SonusEditColors.Surface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(SonusEditColors.Violet.copy(alpha = .32f), SonusEditColors.Cyan.copy(alpha = .10f))
                        )
                    )
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SonusEditColors.VioletBright)
                        Spacer(Modifier.size(10.dp))
                        Text("Criar vídeo automático", fontWeight = FontWeight.Bold, fontSize = 21.sp)
                    }
                    Text(
                        "Música → assinatura → imagens → estilo → revisão. Formato inicial 9:16, com distribuição automática e fade entre imagens.",
                        color = SonusEditColors.TextMuted,
                    )
                    Button(
                        onClick = onCreate,
                        colors = ButtonDefaults.buttonColors(containerColor = SonusEditColors.Violet),
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("CRIAR PROJETO")
                    }
                }
            }
        }

        QuickEntry(Icons.Default.MusicNote, "Música primeiro", "15s até 10min, com ajuste fino ±1s, ±5s e ±10s")
        QuickEntry(Icons.Default.FolderOpen, "Galeria vertical", "9:16 com preenchimento automático sem barras pretas")

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SonusEditColors.SurfaceRaised),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(
                "SONUS Edit 0.1.0 • code01",
                modifier = Modifier.padding(16.dp),
                color = SonusEditColors.TextMuted,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun QuickEntry(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SonusEditColors.SurfaceRaised),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = SonusEditColors.Cyan)
            Spacer(Modifier.size(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = SonusEditColors.TextMuted, fontSize = 13.sp)
            }
        }
    }
}
