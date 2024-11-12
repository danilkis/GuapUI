package org.lightwork.guapui.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import suai.ui.library.resources.Guap_logo
import suai.ui.library.resources.Res

@Composable
fun SplashScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Uncomment the Image composable if you want to display the logo
//             Image( TODO: Сделать под андроид
//                 painter = painterResource(Res.drawable.Guap_logo),
//                 contentDescription = "GUAP Logo",
//                 modifier = Modifier
//                     .size(120.dp)
//                     .scale(scale),
//                 contentScale = ContentScale.Crop
//             )
//             Spacer(modifier = Modifier.height(24.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(350.dp)
                    .height(20.dp)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun WebLoadingScreen(progress: Float, message: String = "Подгружаем библиотеки, буквально пару минут") {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Uncomment the Image composable if you want to display the logo
             Image(
                 painter = painterResource(Res.drawable.Guap_logo),
                 contentDescription = "GUAP Logo",
                 modifier = Modifier
                     .size(120.dp)
                     .scale(scale),
                 contentScale = ContentScale.Crop
             )
            Spacer(modifier = Modifier.height(24.dp))
            Text(message, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier
                    .width(350.dp)
                    .height(20.dp)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}