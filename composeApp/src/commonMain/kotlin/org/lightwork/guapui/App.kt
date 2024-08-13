package org.lightwork.guapui

import Overview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.guap02.screen.element.ExpandableField
import org.lightwork.guapui.elements.LessonEntry
import org.jetbrains.compose.resources.painterResource

import guapui.composeapp.generated.resources.Res
import guapui.composeapp.generated.resources.compose_multiplatform
import org.lightwork.guapui.elements.DayCard
import org.lightwork.guapui.models.Lesson

@Composable
fun App() {
    MaterialTheme {
         Overview()
        }
    }

