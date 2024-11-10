package org.lightwork.guapui.elements

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.lightwork.guapui.models.Group

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpandableGroupField(
    items: List<Group>,
    label: String,
    selectedGroupId: Int?, // Add selectedGroupId parameter
    onItemSelected: (Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf("") }

    // If selectedGroupId is not null, update selectedValue based on it
    LaunchedEffect(selectedGroupId) {
        selectedGroupId?.let { id ->
            val selectedGroup = items.find { it.ItemId == id }
            selectedValue = selectedGroup?.Name ?: ""
        }
    }

    // Filter the items based on the typed input
    val filteredItems = items.filter { it.Name.contains(selectedValue, ignoreCase = true) }

    val keyboardController = LocalSoftwareKeyboardController.current

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        OutlinedTextField(
            label = {
                Text(label)
            },
            value = selectedValue,
            onValueChange = { newValue ->
                selectedValue = newValue
            },
            modifier = Modifier
                .focusable(true)
                .widthIn(max = 200.dp)
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    isExpanded = true // Expand the dropdown when Enter/Done is pressed
                    keyboardController?.hide()
                }
            )
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }) {

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
