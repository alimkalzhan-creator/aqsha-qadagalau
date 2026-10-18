package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.*
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.tr
import java.time.YearMonth

@Composable
fun HistoryScreen(month: YearMonth, onMonth: (YearMonth) -> Unit, onBack: () -> Unit) {
    val lang = LocalLang.current
    val list = Repo.transactions.filter { monthOf(it.date) == month }.sortedByDescending { it.date }
    val grouped = list.groupBy { it.date.toLocalDate() }
    var del by remember { mutableStateOf<Transaction?>(null) }

    SubScreen(title = tr(S.history), onBack = onBack) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item { MonthSwitcher(month, onMonth) }
            if (list.isEmpty()) {
                item {
                    AppCard { Text(tr(S.noData), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
            grouped.forEach { (day, txs) ->
                item(key = "d_$day") {
                    Text(
                        day.label(lang),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 2.dp)
                    )
                }
                items(txs, key = { it.id }) { t ->
                    val cat = Repo.category(t.categoryId)
                    val isIncome = t.type == TxType.INCOME
                    AppCard(Modifier.clickable { del = t }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryIconCircle(cat, 40.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(cat?.title(lang) ?: "—", style = MaterialTheme.typography.titleSmall)
                                if (t.note.isNotBlank()) {
                                    Text(
                                        t.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Text(
                                (if (isIncome) "+" else "−") + money(t.amountKzt),
                                color = if (isIncome) IncomeColor else ExpenseColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    del?.let { t ->
        AlertDialog(
            onDismissRequest = { del = null },
            title = { Text(money(t.amountKzt)) },
            text = { Text(tr(S.deleteTxQ)) },
            confirmButton = {
                TextButton(onClick = {
                    Repo.deleteTx(t.id)
                    del = null
                }) { Text(tr(S.delete), color = ExpenseColor) }
            },
            dismissButton = { TextButton(onClick = { del = null }) { Text(tr(S.cancel)) } }
        )
    }
}
