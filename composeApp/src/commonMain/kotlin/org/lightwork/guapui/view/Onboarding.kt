package org.lightwork.guapui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import suai.ui.library.resources.Res
import suai.ui.library.resources.apple
import suai.ui.library.resources.beta
import suai.ui.library.resources.guap_calendar
import suai.ui.library.resources.notes

// Основной экран онбординга с тремя страницами и уникальными SVG изображениями
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pages: List<@Composable () -> Unit> = listOf(
        { OnboardingPage1() },
        { OnboardingPage2() },
        { OnboardingPage3() },
        { OnboardingPage4() },
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            pages[page].invoke()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (pagerState.currentPage == pages.size - 1) {
                    onComplete()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        ) {
            Text(if (pagerState.currentPage == pages.size - 1) "Погнали!" else "Дальше")
        }

        DotsIndicator(
            pageCount = pages.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// Компоненты для страниц с уникальными SVG и текстом
@Composable
expect fun OnboardingPage1()

@Composable
expect fun OnboardingPage2()

@Composable
expect fun OnboardingPage3()

@Composable
expect fun OnboardingPage4()

@Composable
expect fun OnboardingPageWithContent(
    title: String,
    text: String,
    resource: DrawableResource,
    iconTint: Color? = null // Step 1: Add iconTint parameter
)

@Composable
fun DotsIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically // Вертикально центрируем точки
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .background(
                        color = if (index == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        shape = MaterialTheme.shapes.large
                    )
            )
        }
    }
}
