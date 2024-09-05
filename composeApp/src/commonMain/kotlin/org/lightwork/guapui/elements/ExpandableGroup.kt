package org.lightwork.guapui.elements

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.lightwork.guapui.models.Group

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExpandableGroupField(
    items: List<Group>,
    label: String,
    onItemSelected: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf(items[0].Name) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = items.filter { it.Name.contains(searchQuery, ignoreCase = true) }

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

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .padding(8.dp),
                label = { Text("Найти") }
            )

            filteredItems.forEach { group ->
                DropdownMenuItem(
                    onClick = {
                        selectedValue = group.Name
                        onItemSelected(group.ItemId)
                        isExpanded = false
                    },
                    text = { Text(group.Name) }
                )
            }
        }
    }
}