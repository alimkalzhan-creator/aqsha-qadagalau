package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.*
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.T
import kz.aqsha.qadagalau.i18n.tr
import kz.aqsha.qadagalau.i18n.trf
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

private fun RecurringGroup.titleT(): T = when (this) {
    RecurringGroup.CREDIT -> S.groupCredit
    RecurringGroup.FOOD -> S.groupFood
    RecurringGroup.UTILITIES -> S.groupUtil
    RecurringGroup.OTHER -> S.groupOther
}

private fun RecurringGroup.icon(): ImageVector = when (this) {
    RecurringGroup.CREDIT -> Icons.Outlined.CreditCard
    RecurringGroup.FOOD -> Icons.Outlined.ShoppingBasket
    RecurringGroup.UTILITIES -> Icons.Outlined.Bolt
    RecurringGroup.OTHER -> Icons.Outlined.Receipt
}

private fun RecurringGroup.color(): Color = when (this) {
    RecurringGroup.CREDIT -> Color(0xFFC98B5B)
    RecurringGroup.FOOD -> Color(0xFFE5739A)
    RecurringGroup.UTILITIES -> Color(0xFF3FA7A3)
    RecurringGroup.OTHER -> Color(0xFF7E8A99)
}

@Composable
fun RecurringScreen(month: YearMonth, onMonth: (YearMonth) -> Unit, onBack: () -> Unit) {
    var edit by remember { mutableStateOf<Recurring?>(null) }
    var newGroup by remember { mutableStateOf<RecurringGroup?>(null) }
    val items = Repo.recurring.toList()
    val total = items.sumOf { it.amountKzt }
    val paid = items.filter { Repo.isPaid(it, month) }.sumOf { it.amountKzt }
    val income = statsFor(month).income

    SubScreen(
        title = tr(S.recurring),
        onBack = onBack,
        fab = {
            FloatingActionButton(
                onClick = { newGroup = RecurringGroup.OTHER },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) { Icon(Icons.Filled.Add, contentDescription = null) }
        }
    ) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { MonthSwitcher(month, onMonth) }
            item {
                AppCard {
                    Text(tr(S.monthlyTotal), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(money(total), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                    if (income > 0 && total > 0) {
                        Text(
                            trf(S.ofIncome, (total / income * 100).roundToInt()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { if (total > 0) (paid / total).toFloat() else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = IncomeColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    Row {
                        StatColumn(tr(S.paid), money(paid), IncomeColor, Modifier.weight(1f))
                        StatColumn(tr(S.remaining), money(total - paid), ExpenseColor, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(tr(S.payHint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            RecurringGroup.entries.forEach { g ->
                val list = items.filter { it.group == g }.sortedBy { it.day }
                item(key = "h_${g.name}") {
                    GroupHeader(g, list.sumOf { it.amountKzt }) { newGroup = g }
                }
                if (list.isEmpty()) {
                    item(key = "e_${g.name}") {
                        Text(
                            tr(S.recurringEmpty),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                        )
                    }
                }
                items(list, key = { it.id }) { r ->
                    RecurringRow(r, month, onToggle = { Repo.togglePaid(r, month) }, onEdit = { edit = r })
                }
            }
        }
    }

    if (edit != null || newGroup != null) {
        RecurringEditor(edit, newGroup ?: RecurringGroup.OTHER) {
            edit = null
            newGroup = null
        }
    }
}

@Composable
private fun GroupHeader(g: RecurringGroup, sum: Double, onAdd: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(g.icon(), contentDescription = null, tint = g.color(), modifier = Modifier.size(26.dp))
        Spacer(Modifier.width(8.dp))
        Text(tr(g.titleT()), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(money(sum), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        IconButton(onClick = onAdd) {
            Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun RecurringRow(r: Recurring, month: YearMonth, onToggle: () -> Unit, onEdit: () -> Unit) {
    val isPaid = Repo.isPaid(r, month)
    val now = LocalDate.now()
    val status: Pair<String, Color>? = when {
        isPaid -> null
        month != YearMonth.now() -> null
        r.day < now.dayOfMonth -> tr(S.overdue) to ExpenseColor
        r.day == now.dayOfMonth -> tr(S.today) to Color(0xFFF0A040)
        r.day - now.dayOfMonth <= 5 -> trf(S.inDays, r.day - now.dayOfMonth) to Color(0xFFF0A040)
        else -> null
    }
    AppCard(Modifier.clickable(onClick = onEdit)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(r.name, style = MaterialTheme.typography.titleMedium)
                Row {
                    Text(trf(S.dayN, r.day), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (status != null) {
                        Text("  •  ${status.first}", style = MaterialTheme.typography.bodySmall, color = status.second)
                    }
                }
                Text(money(r.amountKzt), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            if (isPaid) {
                FilledTonalButton(
                    onClick = onToggle,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = IncomeColor.copy(alpha = 0.18f),
                        contentColor = IncomeColor
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(tr(S.paid))
                }
            } else {
                Button(
                    onClick = onToggle,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text(tr(S.pay)) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecurringEditor(initial: Recurring?, defaultGroup: RecurringGroup, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var amount by remember { mutableStateOf(initial?.let { plainNumber(Repo.toDisplay(it.amountKzt)) } ?: "") }
    var group by remember { mutableStateOf(initial?.group ?: defaultGroup) }
    var day by remember { mutableStateOf((initial?.day ?: LocalDate.now().dayOfMonth).toString()) }
    val amountVal = parseAmount(amount)
    val dayVal = day.toIntOrNull()?.takeIf { it in 1..31 }
    val valid = name.isNotBlank() && amountVal != null && amountVal > 0 && dayVal != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) tr(S.newPayment) else tr(S.editPayment)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text(tr(S.name)) }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { v -> amount = v.filter { it.isDigit() || it == '.' || it == ',' } },
                    label = { Text(tr(S.amount)) },
                    suffix = { Text(Repo.settings.currency.symbol) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = day,
                    onValueChange = { v -> day = v.filter { it.isDigit() }.take(2) },
                    label = { Text(tr(S.dayOfMonth)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    RecurringGroup.entries.forEach { g ->
                        FilterChip(
                            selected = g == group,
                            onClick = { group = g },
                            label = { Text(tr(g.titleT())) },
                            leadingIcon = { Icon(g.icon(), contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = {
                if (amountVal != null && dayVal != null) {
                    Repo.upsertRecurring(
                        Recurring(
                            id = initial?.id ?: newId(),
                            name = name.trim(),
                            amountKzt = Repo.toBase(amountVal),
                            group = group,
                            day = dayVal,
                            paidMonths = initial?.paidMonths ?: emptySet()
                        )
                    )
                }
                onDismiss()
            }) { Text(tr(S.save)) }
        },
        dismissButton = {
            Row {
                if (initial != null) {
                    TextButton(onClick = {
                        Repo.deleteRecurring(initial.id)
                        onDismiss()
                    }) { Text(tr(S.delete), color = ExpenseColor) }
                }
                TextButton(onClick = onDismiss) { Text(tr(S.cancel)) }
            }
        }
    )
}
