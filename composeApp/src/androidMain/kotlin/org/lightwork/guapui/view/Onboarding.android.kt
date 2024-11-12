package org.lightwork.guapui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dry
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import suai.ui.library.resources.Res
import suai.ui.library.resources.apple
import suai.ui.library.resources.beta
import suai.ui.library.resources.guap_calendar
import suai.ui.library.resources.notes

@Composable
actual fun OnboardingPageWithContent(
    title: String,
    text: String,
    resource: DrawableResource,
    iconTint: Color?
)  {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        Image( TODO: Разобраться
//            imageVector = vectorResource(resource),
//            contentDescription = "",
//            modifier = Modifier
//                .size(240.dp)
//                .padding(bottom = 16.dp),
//            colorFilter = iconTint?.let { ColorFilter.tint(it) } // Apply tint if provided
//        )
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
actual fun OnboardingPage1() {
    OnboardingPageWithContent(
        title = "Добро пожаловать!",
        text = "SuaiUI — это простой инструмент для просмотра расписания, создания заметок и напоминаний для занятий.",
        resource = Res.drawable.beta,
    )
}

@Composable
actual fun OnboardingPage2() {
    OnboardingPageWithContent(
        title = "Мы еще тестируемся",
        text = "Этот сайт разрабатывается одним человеком и находится в стадии бета-тестирования. Если вы заметите ошибки или столкнулись с проблемами, пожалуйста, сообщите об этом в Telegram: @SuaiMultiplatform.",
        resource = Res.drawable.beta,
        iconTint = MaterialTheme.colorScheme.primary
    )
}

@Composable
actual fun OnboardingPage3() {
    OnboardingPageWithContent(
        title = "Заметки",
        text = "Функция добавления заметок к парам позволяет вам сохранять важную информацию, связанную с парами. Все заметки синхронизируются между устройствами, чтобы вы могли легко получить доступ к ним в любом месте. Для использования этой функции необходимо войти в аккаунт или создать новый.",
        resource = Res.drawable.notes,
        iconTint = MaterialTheme.colorScheme.secondary // Step 2: Set primary color as tint
    )
}

@Composable
actual fun OnboardingPage4() {
    OnboardingPageWithContent(
        title = "iOS и Mac",
        text = "Сайт работает на iOS и macOS (поддерживаются только последние бета-версии). Когда приложение будет полностью готово, мы сообщим об этом в Telegram. Спасибо за внимание и приятного пользования!",
        resource = Res.drawable.apple,
        iconTint = MaterialTheme.colorScheme.tertiary // Step 2: Set primary color as tint
    )
}
