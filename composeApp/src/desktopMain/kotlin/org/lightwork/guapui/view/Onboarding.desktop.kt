package org.lightwork.guapui.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import suai.ui.library.resources.Res
import suai.ui.library.resources.beta
import suai.ui.library.resources.guap_calendar

@Composable
actual fun OnboardingPage1() {
    OnboardingPageWithContent(
        title = "Добро пожаловать!",
        text = "SuaiUI — это простой инструмент для просмотра расписания, создания заметок и напоминаний для занятий.",
        svgResource = Res.drawable.guap_calendar
    )
}

@Composable
actual fun OnboardingPage2() {
    OnboardingPageWithContent(
        title = "Мы еще тестируемся",
        text = "Этот сайт разрабатывается одним человеком и находится в стадии бета-тестирования. Если вы заметите ошибки или столкнулись с проблемами, пожалуйста, сообщите об этом в Telegram: @SuaiMultiplatform.",
        svgResource = Res.drawable.beta,
        iconTint = MaterialTheme.colorScheme.primary
    )
}

@Composable
actual fun OnboardingPage3() {
}

@Composable
actual fun OnboardingPage4() {
}

@Composable
actual fun OnboardingPageWithContent(
    title: String,
    text: String,
    resource: Res,
    iconTint: Color?
) {
}