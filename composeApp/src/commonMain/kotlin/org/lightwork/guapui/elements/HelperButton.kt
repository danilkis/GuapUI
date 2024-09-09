package org.lightwork.guapui.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun HelperButton() {
    var showDialog by remember { mutableStateOf(false) }

    // Outlined Icon Button
    OutlinedButton(
        onClick = { showDialog = true }
    ) {
        Text(
            text = "?",
            style = MaterialTheme.typography.titleLarge, // Using the proper typography style
            color = MaterialTheme.colorScheme.primary,
            fontSize = 25.sp// Applying primary color for the icon
        )
    }

    // Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = "Как этим пользоваться?",
                    style = MaterialTheme.typography.titleMedium, // Applying title styling
                    color = MaterialTheme.colorScheme.onSurface // Using onSurface color
                )
            },
            text = {
                Text(
                    "Вверху выберите группу и тип недели (Числитель, Знаменатель или Авто).\nНажмите на номер аудитории, чтобы открыть навигатор до аудитории, или на адрес корпуса, чтобы увидеть навигатор по копусу\n. Если возникнут вопросы, нажмите на иконку с вопросом))\nПредложения и пожелания пишите мне в телеграмм @Puncheeesh ^-^",
                    style = MaterialTheme.typography.bodyMedium, // Applying body styling
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Color for secondary text
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("OK")
                }
            }
        )
    }
}


@Composable
fun SocialButton(socialUri: String, icon: DrawableResource) {
    val uriHandler = LocalUriHandler.current
    // Outlined Icon Button
    OutlinedButton(
        onClick = { uriHandler.openUri(socialUri) }
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "",
            modifier = Modifier // Set the size to ensure it fits well
                .padding(4.dp).size(25.dp) // Add padding to give some space around it
        )
    }
}
