package com.sonusstudios.sonusedit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.sonusstudios.sonusedit.integration.IncomingIntentRouter
import com.sonusstudios.sonusedit.ui.SonusEditApp
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val incomingRoute = MutableStateFlow<IncomingIntentRouter.Route?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        incomingRoute.value = IncomingIntentRouter.parse(intent)
        setContent {
            SonusEditApp(incomingRoute = incomingRoute)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingRoute.value = IncomingIntentRouter.parse(intent)
    }
}
