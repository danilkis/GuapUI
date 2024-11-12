package org.lightwork.guapui.view

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import suai.ui.library.resources.*
import suai.ui.library.resources.Res
import suai.ui.library.resources.beta
import suai.ui.library.resources.guap_calendar
import suai.ui.library.resources.notes

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
    OnboardingPageWithContent(
        title = "Заметки",
        text = "Функция добавления заметок к парам позволяет вам сохранять важную информацию, связанную с парами. Все заметки синхронизируются между устройствами, чтобы вы могли легко получить доступ к ним в любом месте. Для использования этой функции необходимо войти в аккаунт или создать новый.",
        svgResource = Res.drawable.notes,
        iconTint = MaterialTheme.colorScheme.secondary // Step 2: Set primary color as tint
    )
}

@Composable
actual fun OnboardingPage4() {
    OnboardingPageWithContent(
        title = "iOS и Mac",
        text = "Сайт работает на iOS и macOS (поддерживаются только последние бета-версии). Когда приложение будет полностью готово, мы сообщим об этом в Telegram. Спасибо за внимание и приятного пользования!",
        svgResource = Res.drawable.apple,
        iconTint = MaterialTheme.colorScheme.tertiary // Step 2: Set primary color as tint
    )
}

@Composable
actual fun OnboardingPageWithContent(
    title: String,
    text: String,
    resource: Res,
    iconTint: Color?
) {
}