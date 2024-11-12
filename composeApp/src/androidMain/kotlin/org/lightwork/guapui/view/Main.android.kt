package org.lightwork.guapui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.lightwork.guapui.elements.ExpandableGroupField
import org.lightwork.guapui.models.Group
import suai.ui.library.resources.Res
import suai.ui.library.resources.guap_calendar

@Composable
actual fun ScheduleAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    groups: List<Group>?,
    selectedGroupId: Int?,
    onGroupSelected: (Int) -> Unit,
    onNavigateToAccount: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            }

            if (currentScreen.title == "SuaiUI") {
//                Image(
//                    painter = painterResource(Res.drawable.guap_calendar),
//                    contentDescription = "SuaiUI Logo",
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .size(70.dp)
//                )
                groups?.let {
                    ExpandableGroupField(
                        items = it,
                        label = "Группа",
                        selectedGroupId = selectedGroupId,
                        onItemSelected = { id -> onGroupSelected(id) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onNavigateToAccount) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(90.dp)
                    )
                }
            } else {
                Text(currentScreen.title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}