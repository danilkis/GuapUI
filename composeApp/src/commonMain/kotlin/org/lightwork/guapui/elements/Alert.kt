package org.lightwork.guapui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Вспомогательный компонент для отображения сообщения
 * @param icon иконка сообщения
 * @param text текст сообщения
 * @param modifier модификатор
 * @param iconColor цвет иконки
 * @param backgroundColor цвет фона
 * @author Сергей Рейнн (bulkabuka)
 */
@Composable
fun Alert(icon: ImageVector, text: String, modifier: Modifier = Modifier, iconColor: Color, backgroundColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = modifier) {
        Icon(imageVector = icon, contentDescription = "test", tint = iconColor, modifier = Modifier.background(backgroundColor, MaterialTheme.shapes.small).padding(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}