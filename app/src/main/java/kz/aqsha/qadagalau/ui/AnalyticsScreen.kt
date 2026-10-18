package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.*
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.tr
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(month: YearMonth, onMonth: (YearMonth) -> Unit, onBack: () -> Unit) {
    val lang = LocalLang.current
    val st = statsFor(month)
    val prev = statsFor(month.minusMonths(1))
    val prevMap = prev.byCategory.associate { it.first.id to it.second }
    val tips = buildTips(month, lang)

    val isCurrent = month == YearMonth.now()
    val daysPassed = if (isCurrent) LocalDate.now().dayOfMonth else month.lengthOfMonth()
    val dailyAvg = if (daysPassed > 0) st.expense / daysPassed else 0.0
    val forecast = dailyAvg * month.lengthOfMonth()

    SubScreen(title = tr(S.analytics), onBack = onBack) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { MonthSwitcher(month, onMonth) }

            item {
                AppCard {
                    Row {
                        StatColumn(tr(S.incomes), money(st.income), IncomeColor, Modifier.weight(1f))
                        StatColumn(tr(S.expenses), money(st.expense), ExpenseColor, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row {
                        StatColumn(
                            tr(S.balance), money(st.balance),
                            if (st.balance < 0) ExpenseColor else MaterialTheme.colorScheme.primary,
                            Modifier.weight(1f)
                        )
                        val rate = if (st.income > 0) ((st.income - st.expense) / st.income * 100).roundToInt() else null
                        StatColumn(
                            tr(S.savingsRate), rate?.let { "$it%" } ?: "—",
                            MaterialTheme.colorScheme.onSurface, Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row {
                        StatColumn(tr(S.dailyAvg), money(dailyAvg), MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                        if (isCurrent) {
                            StatColumn(tr(S.forecast), money(forecast), Color(0xFFF0A040), Modifier.weight(1f))
                        }
                    }
                }
            }

            item { SectionTitle(tr(S.whereMoney)) }

            if (st.byCategory.isEmpty()) {
                item {
                    AppCard {
                        Text(tr(S.noData), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                item {
                    AppCard {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            DonutChart(
                                segments = st.byCategory.map { Color(it.first.color) to it.second },
                                modifier = Modifier.size(180.dp),
                                thickness = 0.22f
                            ) {
                                Text(tr(S.expenses), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(money(st.expense), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = ExpenseColor)
                            }
                        }
                    }
                }
                items(st.byCategory, key = { it.first.id }) { (cat, v) ->
                    val share = if (st.expense > 0) (v / st.expense).toFloat() else 0f
                    val p = prevMap[cat.id] ?: 0.0
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryIconCircle(cat, 40.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(cat.title(lang), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f), maxLines = 1)
                                    Text(money(v), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { share },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp),
                                    color = Color(cat.color),
                                    trackColor = Color(cat.color).copy(alpha = 0.15f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Row {
                                    Text(
                                        "${(share * 100).roundToInt()}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (p > 0) {
                                        val change = ((v - p) / p * 100).roundToInt()
                                        val sign = if (change > 0) "+" else if (change < 0) "−" else ""
                                        Text(
                                            "$sign${abs(change)}% ${tr(S.vsPrev)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (change > 0) ExpenseColor else IncomeColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { SectionTitle(tr(S.tips)) }

            items(tips) { tip ->
                val (icon, color) = when (tip.kind) {
                    TipKind.WARN -> Icons.Outlined.WarningAmber to Color(0xFFE08A3C)
                    TipKind.GOOD -> Icons.Outlined.ThumbUp to IncomeColor
                    TipKind.INFO -> Icons.Outlined.Lightbulb to MaterialTheme.colorScheme.primary
                }
                AppCard {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(tip.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
