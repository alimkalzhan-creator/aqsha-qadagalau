package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.*
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.tr
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

data class AddRequest(val type: TxType, val categoryId: String? = null)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionSheet(req: AddRequest, onDismiss: () -> Unit) {
    val lang = LocalLang.current
    val color = if (req.type == TxType.EXPENSE) ExpenseColor else IncomeColor
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var catId by remember {
        mutableStateOf(req.categoryId ?: Repo.categories.firstOrNull { it.type == req.type }?.id)
    }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var showDate by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val cats = Repo.categories.filter { it.type == req.type }
    val parsed = parseAmount(amount)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                if (req.type == TxType.EXPENSE) tr(S.newExpense) else tr(S.newIncome),
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = { v -> amount = v.filter { it.isDigit() || it == '.' || it == ',' } },
                label = { Text(tr(S.amount)) },
                suffix = { Text(Repo.settings.currency.symbol) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text(tr(S.category), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                cats.forEach { c ->
                    Column(
                        Modifier
                            .width(76.dp)
                            .clickable { catId = c.id }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CategoryIconCircle(c, 46.dp, selected = c.id == catId)
                        Text(
                            c.title(lang),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            AssistChip(
                onClick = { showDate = true },
                label = { Text(if (date == LocalDate.now()) tr(S.today) else date.label(lang)) },
                leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) }
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(tr(S.note)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val cid = catId
                    if (parsed != null && parsed > 0 && cid != null) {
                        Repo.addTx(
                            Transaction(
                                id = newId(),
                                amountKzt = Repo.toBase(parsed),
                                categoryId = cid,
                                note = note.trim(),
                                date = date.toMillis(),
                                type = req.type
                            )
                        )
                        onDismiss()
                    }
                },
                enabled = parsed != null && parsed > 0 && catId != null,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(tr(S.save), style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showDate) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDate = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showDate = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDate = false }) { Text(tr(S.cancel)) } }
        ) {
            DatePicker(state = state)
        }
    }
}
