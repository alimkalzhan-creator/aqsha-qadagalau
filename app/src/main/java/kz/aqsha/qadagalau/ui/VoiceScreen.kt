package kz.aqsha.qadagalau.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.*
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.tr
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun VoiceScreen(onBack: () -> Unit) {
    val lang = LocalLang.current
    var heard by rememberSaveable { mutableStateOf("") }
    val items = remember { mutableStateListOf<ParsedItem>() }
    var message by remember { mutableStateOf<String?>(null) }

    fun runParse(text: String) {
        items.clear()
        items.addAll(VoiceParser.parse(text))
        message = if (items.isEmpty()) S.nothingFound.get(lang) else null
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val text = res.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()
            if (text.isNotBlank()) {
                heard = text
                runParse(text)
            }
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang.tag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang.tag)
            putExtra(RecognizerIntent.EXTRA_PROMPT, S.tapToSpeak.get(lang))
        }
        try {
            launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            message = S.noRecognizer.get(lang)
        }
    }

    val st = statsFor(YearMonth.now())

    SubScreen(title = tr(S.voice), onBack = onBack) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                AppCard {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            onClick = { startListening() },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 6.dp,
                            modifier = Modifier.size(110.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(tr(S.tapToSpeak), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            tr(S.voiceHint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = heard,
                    onValueChange = { heard = it },
                    label = { Text(tr(S.orType)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { runParse(heard) },
                    enabled = heard.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(tr(S.recognize)) }
            }

            message?.let { msg ->
                item {
                    Text(msg, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (items.isNotEmpty()) {
                item { SectionTitle(tr(S.found)) }
                itemsIndexed(items, key = { _, p -> p.uid }) { index, it ->
                    ParsedRow(
                        item = it,
                        onChange = { new -> items[index] = new },
                        onRemove = { items.removeAt(index) }
                    )
                }
                item {
                    Button(
                        onClick = {
                            var n = 0
                            items.forEach { p ->
                                if (p.amount > 0) {
                                    Repo.addTx(
                                        Transaction(
                                            id = newId(),
                                            amountKzt = Repo.toBase(p.amount),
                                            categoryId = p.categoryId,
                                            note = p.note,
                                            date = LocalDate.now().toMillis(),
                                            type = p.type
                                        )
                                    )
                                    n++
                                }
                            }
                            items.clear()
                            heard = ""
                            message = S.added.f(lang, n)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) { Text(tr(S.addAll), style = MaterialTheme.typography.titleMedium) }
                }
            }

            item {
                SectionTitle(tr(S.monthSummary))
                AppCard {
                    Row {
                        StatColumn(tr(S.incomes), money(st.income), IncomeColor, Modifier.weight(1f))
                        StatColumn(tr(S.expenses), money(st.expense), ExpenseColor, Modifier.weight(1f))
                        StatColumn(
                            tr(S.balance), money(st.balance),
                            if (st.balance < 0) ExpenseColor else MaterialTheme.colorScheme.primary,
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParsedRow(item: ParsedItem, onChange: (ParsedItem) -> Unit, onRemove: () -> Unit) {
    val lang = LocalLang.current
    val cat = Repo.category(item.categoryId)
    var menu by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf(plainNumber(item.amount)) }
    val color = if (item.type == TxType.EXPENSE) ExpenseColor else IncomeColor

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CategoryIconCircle(cat, 40.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.note.ifBlank { cat?.title(lang) ?: "" },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(
                        onClick = {
                            val newType = if (item.type == TxType.EXPENSE) TxType.INCOME else TxType.EXPENSE
                            val newCat = VoiceParser.matchCategory(item.note.lowercase(), newType)
                                ?: Repo.fallbackCategory(newType)
                            onChange(item.copy(type = newType, categoryId = newCat?.id ?: item.categoryId))
                        },
                        label = { Text(if (item.type == TxType.EXPENSE) tr(S.expense) else tr(S.income), color = color) }
                    )
                    Box {
                        AssistChip(
                            onClick = { menu = true },
                            label = { Text(cat?.title(lang) ?: "—", maxLines = 1) }
                        )
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            Repo.categories.filter { it.type == item.type }.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.title(lang)) },
                                    leadingIcon = { CategoryIconCircle(c, 28.dp) },
                                    onClick = {
                                        onChange(item.copy(categoryId = c.id))
                                        menu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            OutlinedTextField(
                value = amountText,
                onValueChange = { v ->
                    amountText = v.filter { it.isDigit() || it == '.' || it == ',' }
                    parseAmount(amountText)?.let { onChange(item.copy(amount = it)) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = color),
                modifier = Modifier.width(104.dp)
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Outlined.Close, contentDescription = null)
            }
        }
    }
}
