package org.lightwork.guapui.elements

import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*

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
            color = MaterialTheme.colorScheme.primary // Applying primary color for the icon
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