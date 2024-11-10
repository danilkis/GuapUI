package com.example.guap02.screen.element

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExpandableWeekField(
    items: List<String>,
    label: String,
    onItemSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf(items[0]) }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            label = {
                Text(label)
            },
            value = selectedValue,
            onValueChange = { selectedValue = it },
            modifier = Modifier
                .focusable(true)
                .widthIn(max = 200.dp)
                .menuAnchor()
                .padding(8.dp),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            }
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {
            items.forEach { group ->
                DropdownMenuItem(onClick = {
                    onItemSelected(group)
                    selectedValue = group
                }, text = { Text(group) })
            }
        }
    }
}