package org.lightwork.guapui.elements

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.lightwork.guapui.models.Group

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExpandableGroupField(
    items: List<Group>,
    label: String,
    onItemSelected: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = items.filter { it.Name.contains(searchQuery, ignoreCase = true) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded } // Toggle expanded state
    ) {
        TextField(
            readOnly = false, // Allow user to type in the field
            label = { Text(label) },
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isExpanded = true // Automatically expand the dropdown when typing
            },
            modifier = Modifier
                .focusable(true)
                .widthIn(max = 200.dp)
                .menuAnchor()
                .padding(8.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {

            filteredItems.forEach { group ->
                DropdownMenuItem(
                    onClick = {
                        searchQuery = group.Name
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