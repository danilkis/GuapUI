package org.lightwork.guapui.view

import androidx.compose.ui.Modifier
import org.lightwork.guapui.models.Group

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
}