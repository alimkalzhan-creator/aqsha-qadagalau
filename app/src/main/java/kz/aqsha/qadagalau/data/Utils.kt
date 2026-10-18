package kz.aqsha.qadagalau.data

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// ---------- dates ----------
fun LocalDate.toMillis(): Long =
    atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun monthOf(ms: Long): YearMonth = YearMonth.from(ms.toLocalDate())

fun YearMonth.label(lang: Lang): String {
    val loc = lang.locale()
    return format(DateTimeFormatter.ofPattern("LLLL yyyy", loc))
        .replaceFirstChar { it.titlecase(loc) }
}

fun LocalDate.label(lang: Lang): String {
    val loc = lang.locale()
    return format(DateTimeFormatter.ofPattern("d MMMM, EEEE", loc))
}

// ---------- money ----------
private val moneyFormat: DecimalFormat by lazy {
    val sym = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }
    DecimalFormat("#,##0.00", sym)
}

/** Formats an amount stored in tenge in the currently selected currency. */
fun money(kzt: Double): String {
    val s = Repo.settings
    return moneyFormat.format(kzt / s.rate()) + " " + s.currency.symbol
}

/** Amount already in display currency, for text fields. */
fun plainNumber(v: Double): String =
    if (v % 1.0 == 0.0) v.toLong().toString() else String.format(Locale.US, "%.2f", v)

fun parseAmount(text: String): Double? =
    text.replace(" ", "").replace(' '.toString(), "").replace(',', '.').toDoubleOrNull()

// ---------- statistics ----------
data class MonthStats(
    val income: Double,
    val expense: Double,
    val byCategory: List<Pair<Category, Double>>
) {
    val balance: Double get() = income - expense
}

fun statsFor(month: YearMonth): MonthStats {
    val txs = Repo.transactions.filter { monthOf(it.date) == month }
    val income = txs.filter { it.type == TxType.INCOME }.sumOf { it.amountKzt }
    val exp = txs.filter { it.type == TxType.EXPENSE }
    val byCat = exp.groupBy { it.categoryId }
        .mapNotNull { (id, list) ->
            val cat = Repo.category(id) ?: Repo.fallbackCategory(TxType.EXPENSE)
            cat?.let { it to list.sumOf { t -> t.amountKzt } }
        }
        .groupBy({ it.first.id }) { it }
        .map { (_, pairs) -> pairs.first().first to pairs.sumOf { it.second } }
        .sortedByDescending { it.second }
    return MonthStats(income, exp.sumOf { it.amountKzt }, byCat)
}

fun Category.title(lang: Lang): String =
    name?.takeIf { it.isNotBlank() }
        ?: key?.let { Defaults.names[it]?.get(lang) }
        ?: "—"
