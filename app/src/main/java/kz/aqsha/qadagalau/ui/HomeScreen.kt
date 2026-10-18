package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.EventRepeat
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.KeyboardVoice
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.aqsha.qadagalau.Screen
import kz.aqsha.qadagalau.data.*
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.tr
import java.time.YearMonth
import kotlin.math.roundToInt

@Composable
fun AppTitle(subtitle: String) {
    Column {
        Text(
            S.APP,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 25.sp,
            maxLines = 1
        )
        Text(subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f), maxLines = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    month: YearMonth,
    onMonth: (YearMonth) -> Unit,
    openDrawer: () -> Unit,
    navigate: (Screen) -> Unit
) {
    val lang = LocalLang.current
    val st = statsFor(month)
    var add by remember { mutableStateOf<AddRequest?>(null) }

    val expenseCats = Repo.categories.filter { it.type == TxType.EXPENSE }
    val shown = expenseCats.take(11)
    val slots: List<Category?> = shown + List(11 - shown.size) { null }
    val spent = st.byCategory.associate { it.first.id to it.second }

    fun percent(c: Category?): Int? {
        if (c == null || st.expense <= 0) return null
        val v = spent[c.id] ?: return null
        return (v / st.expense * 100).roundToInt()
    }

    val slot: @Composable (Int) -> Unit = { i ->
        if (i == 11) {
            CatSlot(null, null, isMore = true) { add = AddRequest(TxType.EXPENSE) }
        } else {
            val c = slots[i]
            CatSlot(c, percent(c)) {
                if (c != null) add = AddRequest(TxType.EXPENSE, c.id) else navigate(Screen.CATEGORIES)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { AppTitle(tr(S.subtitle)) },
                navigationIcon = {
                    IconButton(onClick = openDrawer) { Icon(Icons.Filled.Menu, contentDescription = null) }
                },
                actions = {
                    IconButton(onClick = { navigate(Screen.ANALYTICS) }) {
                        Icon(Icons.Outlined.PieChart, contentDescription = null)
                    }
                    IconButton(onClick = { navigate(Screen.SETTINGS) }) {
                        Icon(Icons.Outlined.Settings, contentDescription = null)
                    }
                },
                colors = appBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            MonthSwitcher(month, onMonth)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (i in 0..3) slot(i)
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) { slot(4); slot(5) }
                BoxWithConstraints(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    val side = minOf(maxWidth, maxHeight)
                    val fs = (side.value * 0.068f).coerceIn(11f, 20f).sp
                    DonutChart(
                        segments = st.byCategory.map { Color(it.first.color) to it.second },
                        modifier = Modifier.size(side)
                    ) {
                        Text(money(st.income), color = IncomeColor, fontSize = fs, maxLines = 1)
                        Text(money(st.expense), color = ExpenseColor, fontSize = fs, maxLines = 1)
                        Text(
                            money(st.balance),
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = fs,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
                Column(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) { slot(6); slot(7) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (i in 8..11) slot(i)
            }

            // balance bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigate(Screen.HISTORY) }) {
                    Icon(Icons.Outlined.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = if (st.balance < 0) ExpenseColor else MaterialTheme.colorScheme.primary,
                    shadowElevation = 3.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "${tr(S.balance)}  ${money(st.balance)}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                    }
                }
                IconButton(onClick = { navigate(Screen.RECURRING) }) {
                    Icon(Icons.Outlined.EventRepeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            // − 🎤 +
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundAction(Icons.Filled.Remove, ExpenseColor) { add = AddRequest(TxType.EXPENSE) }
                Surface(
                    onClick = { navigate(Screen.VOICE) },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(58.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
                RoundAction(Icons.Filled.Add, IncomeColor) { add = AddRequest(TxType.INCOME) }
            }
        }
    }

    add?.let { AddTransactionSheet(it) { add = null } }
}

@Composable
private fun CatSlot(cat: Category?, percent: Int?, isMore: Boolean = false, onClick: () -> Unit) {
    val lang = LocalLang.current
    Column(
        Modifier
            .width(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = cat != null || isMore, onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isMore -> {
                Icon(
                    Icons.Outlined.MoreHoriz, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(42.dp)
                )
                Text(tr(S.more), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            cat != null -> {
                Icon(iconFor(cat.icon), contentDescription = null, tint = Color(cat.color), modifier = Modifier.size(42.dp))
                if (percent != null) {
                    Text(
                        "$percent%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                } else {
                    Text(
                        cat.title(lang),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            else -> Spacer(Modifier.size(42.dp))
        }
    }
}

@Composable
private fun RoundAction(icon: ImageVector, color: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(6.dp, color, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(42.dp))
    }
}

/** Side menu, styled like the screenshot: big outlined icons with captions. */
@Composable
fun DrawerContent(onPick: (Screen) -> Unit) {
    val items: List<Triple<Screen, ImageVector, String>> = listOf(
        Triple(Screen.CATEGORIES, iconFor("category"), tr(S.categories)),
        Triple(Screen.RECURRING, Icons.Outlined.EventRepeat, tr(S.recurring)),
        Triple(Screen.ANALYTICS, Icons.Outlined.Lightbulb, tr(S.analytics)),
        Triple(Screen.VOICE, Icons.Outlined.KeyboardVoice, tr(S.voice)),
        Triple(Screen.HISTORY, Icons.Outlined.History, tr(S.history)),
        Triple(Screen.SETTINGS, Icons.Outlined.Settings, tr(S.settings))
    )
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                S.APP,
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            items.forEach { (screen, icon, label) ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onPick(screen) }
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}
