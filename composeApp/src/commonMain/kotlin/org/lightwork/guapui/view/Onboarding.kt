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
import guapui.composeapp.generated.resources.*
import guapui.composeapp.generated.resources.Res
import guapui.composeapp.generated.resources.beta
import guapui.composeapp.generated.resources.guap_calendar
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
fun OnboardingPage1() {
    OnboardingPageWithContent(
        title = "Добро пожаловать!",
        text = "SuaiUI — это простой инструмент для просмотра расписания, создания заметок и напоминаний для занятий.",
        svgResource = Res.drawable.guap_calendar
    )
}

@Composable
fun OnboardingPage2() {
    OnboardingPageWithContent(
        title = "Мы еще тестируемся",
        text = "Этот сайт разрабатывается одним человеком и находится в стадии бета-тестирования. Если вы заметите ошибки или столкнулись с проблемами, пожалуйста, сообщите об этом в Telegram: @SuaiMultiplatform.",
        svgResource = Res.drawable.beta,
        iconTint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun OnboardingPage3() {
    OnboardingPageWithContent(
        title = "Заметки",
        text = "Функция добавления заметок к парам позволяет вам сохранять важную информацию, связанную с парами. Все заметки синхронизируются между устройствами, чтобы вы могли легко получить доступ к ним в любом месте. Для использования этой функции необходимо войти в аккаунт или создать новый.",
        svgResource = Res.drawable.notes,
        iconTint = MaterialTheme.colorScheme.secondary // Step 2: Set primary color as tint
    )
}

@Composable
fun OnboardingPage4() {
    OnboardingPageWithContent(
        title = "iOS и Mac",
        text = "Сайт работает на iOS и macOS (поддерживаются только последние бета-версии). Когда приложение будет полностью готово, мы сообщим об этом в Telegram. Спасибо за внимание и приятного пользования!",
        svgResource = Res.drawable.apple,
        iconTint = MaterialTheme.colorScheme.tertiary // Step 2: Set primary color as tint
    )
}
@Composable
fun OnboardingPageWithContent(
    title: String,
    text: String,
    svgResource: DrawableResource,
    iconTint: Color? = null // Step 1: Add iconTint parameter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(svgResource),
            contentDescription = "",
            modifier = Modifier
                .size(240.dp)
                .padding(bottom = 16.dp),
            colorFilter = iconTint?.let { ColorFilter.tint(it) } // Apply tint if provided
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

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
