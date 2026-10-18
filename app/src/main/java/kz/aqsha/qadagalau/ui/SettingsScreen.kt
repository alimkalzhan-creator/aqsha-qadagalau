package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.Currency
import kz.aqsha.qadagalau.data.Lang
import kz.aqsha.qadagalau.data.Repo
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.tr
import java.util.Locale

private fun parseHex(s: String): Long? {
    val h = s.trim().removePrefix("#")
    if (h.length != 6) return null
    val v = h.toLongOrNull(16) ?: return null
    return 0xFF000000 or v
}

private fun toHex(c: Long): String = String.format(Locale.ROOT, "#%06X", c and 0xFFFFFFL)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val s = Repo.settings
    var confirmReset by remember { mutableStateOf(false) }

    SubScreen(title = tr(S.settings), onBack = onBack) { pad ->
        Column(
            Modifier
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
                .padding(bottom = 32.dp)
        ) {
            // ---------- language ----------
            SectionTitle(tr(S.language))
            AppCard {
                Lang.entries.forEach { l ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { Repo.updateSettings(s.copy(lang = l)) }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = s.lang == l, onClick = { Repo.updateSettings(s.copy(lang = l)) })
                        Text(l.label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // ---------- currency ----------
            SectionTitle(tr(S.currency))
            AppCard {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Currency.entries.forEach { c ->
                        FilterChip(
                            selected = s.currency == c,
                            onClick = { Repo.updateSettings(s.copy(currency = c)) },
                            label = { Text("${c.symbol}  ${c.code}") }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(tr(S.rates), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Currency.entries.filter { it != Currency.KZT }.forEach { c -> RateField(c) }
                Spacer(Modifier.height(6.dp))
                Text(tr(S.ratesHint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // ---------- appearance ----------
            SectionTitle(tr(S.appearance))
            AppCard {
                Text(tr(S.interfaceColor), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrimaryPresets.forEach { c ->
                        ColorSwatch(Color(c), s.primary == c) { Repo.updateSettings(s.copy(primary = c)) }
                    }
                }
                Spacer(Modifier.height(10.dp))
                HexField(initial = toHex(s.primary)) { Repo.updateSettings(Repo.settings.copy(primary = it)) }

                Spacer(Modifier.height(18.dp))
                Text(tr(S.textColor), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // "Auto" chip
                    Box(
                        Modifier
                            .height(40.dp)
                            .clip(CircleShape)
                            .border(
                                if (s.textColor == null) 3.dp else 1.dp,
                                MaterialTheme.colorScheme.onBackground.copy(alpha = if (s.textColor == null) 1f else 0.2f),
                                CircleShape
                            )
                            .clickable { Repo.updateSettings(s.copy(textColor = null)) }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) { Text(tr(S.auto)) }
                    TextPresets.forEach { c ->
                        ColorSwatch(Color(c), s.textColor == c) { Repo.updateSettings(s.copy(textColor = c)) }
                    }
                }
                Spacer(Modifier.height(10.dp))
                HexField(initial = s.textColor?.let { toHex(it) } ?: "") {
                    Repo.updateSettings(Repo.settings.copy(textColor = it))
                }

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tr(S.darkMode), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Switch(
                        checked = s.dark,
                        onCheckedChange = { Repo.updateSettings(s.copy(dark = it)) },
                        colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(tr(S.preview), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(12.dp)
                    ) { Text(S.APP, color = Color.White, fontWeight = FontWeight.Bold) }
                    Text(
                        tr(S.previewText),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // ---------- data ----------
            SectionTitle(tr(S.data))
            OutlinedButton(
                onClick = { confirmReset = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ExpenseColor)
            ) { Text(tr(S.resetData)) }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            text = { Text(tr(S.resetQ)) },
            confirmButton = {
                TextButton(onClick = {
                    Repo.resetAll()
                    confirmReset = false
                }) { Text(tr(S.delete), color = ExpenseColor) }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text(tr(S.cancel)) } }
        )
    }
}

@Composable
private fun RateField(c: Currency) {
    var text by remember(c) {
        val r = Repo.settings.rates[c] ?: c.defaultRate
        mutableStateOf(if (r % 1.0 == 0.0) r.toLong().toString() else r.toString())
    }
    OutlinedTextField(
        value = text,
        onValueChange = { v ->
            text = v.filter { it.isDigit() || it == '.' || it == ',' }
            text.replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }?.let { r ->
                val cur = Repo.settings
                Repo.updateSettings(cur.copy(rates = cur.rates + (c to r)))
            }
        },
        label = { Text("1 ${c.code} (${c.symbol}) =") },
        suffix = { Text("₸") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun HexField(initial: String, onValid: (Long) -> Unit) {
    var text by remember(initial) { mutableStateOf(initial) }
    val valid = text.isEmpty() || parseHex(text) != null
    OutlinedTextField(
        value = text,
        onValueChange = { v ->
            text = v.take(7)
            parseHex(text)?.let(onValid)
        },
        label = { Text(tr(S.customHex)) },
        placeholder = { Text("#6DBE86") },
        isError = !valid,
        singleLine = true,
        leadingIcon = {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(parseHex(text)?.let { Color(it) } ?: Color.Transparent)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}
