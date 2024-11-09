package org.lightwork.guapui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pages = listOf("Welcome", "Tutorial", "Auth")
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = TODO(),
        pageCount = { pages.size }
    ) // Correctly initialize pagerState

    Column {
        HorizontalPager( // This is the correct parameter
            state = pagerState, // Pass pagerState correctly
            modifier = Modifier.weight(1f)
        ) { page ->
            // Display each onboarding page
            OnboardingPage(pageText = pages[page])
        }

        Button(
            onClick = onComplete,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Get Started")
        }
    }
}

@Composable
fun OnboardingPage(pageText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colors.primary)
    ) {
        Text(
            text = pageText,
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h5
        )
    }
}

