package kz.aqsha.qadagalau.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kz.aqsha.qadagalau.data.Settings

val ExpenseColor = Color(0xFFF07C7C)
val IncomeColor = Color(0xFF4DBF80)

val PrimaryPresets: List<Long> = listOf(
    0xFF6DBE86, 0xFF3FA36B, 0xFF2E7D6B, 0xFF26A69A,
    0xFF5B8DEF, 0xFF3F51B5, 0xFF7E57C2, 0xFFEC6F8E,
    0xFFF2994A, 0xFFC9A46E, 0xFF546E7A, 0xFF212121
)

val TextPresets: List<Long> = listOf(
    0xFF1F2A24, 0xFF000000, 0xFF37474F, 0xFF1A237E,
    0xFF4E342E, 0xFF2E7D32, 0xFFFFFFFF, 0xFFE0E0E0
)

val CategoryPalette: List<Long> = listOf(
    0xFFE5739A, 0xFFF0A040, 0xFF5B9BD5, 0xFF3FA7A3, 0xFFC98B5B, 0xFFE0B020,
    0xFF6E7B95, 0xFF9C8DC1, 0xFFB06BD6, 0xFFE05A5A, 0xFFC9A46E, 0xFFB8939B,
    0xFF5FB3AC, 0xFF4CAF7D, 0xFF2E8B57, 0xFF9E9E9E, 0xFF795548, 0xFF26C6DA
)

@Composable
fun AqshaTheme(s: Settings, content: @Composable () -> Unit) {
    val primary = Color(s.primary)
    val autoText = if (s.dark) Color(0xFFE6EFE8) else Color(0xFF1F2A24)
    val text = s.textColor?.let { Color(it) } ?: autoText
    val scheme = if (s.dark) {
        val bg = lerp(Color(0xFF121614), primary, 0.05f)
        darkColorScheme(
            primary = primary, onPrimary = Color.White,
            secondary = primary, onSecondary = Color.White,
            background = bg, onBackground = text,
            surface = Color(0xFF1C2320), onSurface = text,
            surfaceVariant = Color(0xFF26302B), onSurfaceVariant = text.copy(alpha = 0.7f),
            surfaceContainerLow = Color(0xFF1C2320),
            surfaceContainer = Color(0xFF1F2723),
            surfaceContainerHigh = Color(0xFF242D28),
            outline = text.copy(alpha = 0.3f)
        )
    } else {
        val bg = lerp(Color.White, primary, 0.07f)
        lightColorScheme(
            primary = primary, onPrimary = Color.White,
            secondary = primary, onSecondary = Color.White,
            secondaryContainer = primary.copy(alpha = 0.18f), onSecondaryContainer = text,
            background = bg, onBackground = text,
            surface = Color.White, onSurface = text,
            surfaceVariant = lerp(Color.White, primary, 0.12f), onSurfaceVariant = text.copy(alpha = 0.7f),
            surfaceContainerLow = Color.White,
            surfaceContainer = Color.White,
            surfaceContainerHigh = Color.White,
            surfaceContainerHighest = lerp(Color.White, primary, 0.1f),
            outline = text.copy(alpha = 0.3f)
        )
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
