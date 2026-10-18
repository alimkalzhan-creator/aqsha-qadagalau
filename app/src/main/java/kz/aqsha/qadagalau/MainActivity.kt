package kz.aqsha.qadagalau

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import kz.aqsha.qadagalau.data.Repo
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.ui.*
import java.time.YearMonth

enum class Screen { HOME, CATEGORIES, RECURRING, ANALYTICS, VOICE, HISTORY, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repo.init(applicationContext)
        enableEdgeToEdge()
        setContent { AqshaApp() }
    }
}

@Composable
fun AqshaApp() {
    val settings = Repo.settings
    CompositionLocalProvider(LocalLang provides settings.lang) {
        AqshaTheme(settings) {
            var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
            var month by rememberSaveable { mutableStateOf(YearMonth.now()) }
            val drawer = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val back: () -> Unit = { screen = Screen.HOME }

            BackHandler(enabled = drawer.isOpen) { scope.launch { drawer.close() } }
            BackHandler(enabled = !drawer.isOpen && screen != Screen.HOME) { back() }

            ModalNavigationDrawer(
                drawerState = drawer,
                gesturesEnabled = screen == Screen.HOME,
                drawerContent = {
                    DrawerContent { picked ->
                        screen = picked
                        scope.launch { drawer.close() }
                    }
                }
            ) {
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        month = month,
                        onMonth = { month = it },
                        openDrawer = { scope.launch { drawer.open() } },
                        navigate = { screen = it }
                    )
                    Screen.CATEGORIES -> CategoriesScreen(onBack = back)
                    Screen.RECURRING -> RecurringScreen(month, { month = it }, back)
                    Screen.ANALYTICS -> AnalyticsScreen(month, { month = it }, back)
                    Screen.VOICE -> VoiceScreen(onBack = back)
                    Screen.HISTORY -> HistoryScreen(month, { month = it }, back)
                    Screen.SETTINGS -> SettingsScreen(onBack = back)
                }
            }
        }
    }
}
