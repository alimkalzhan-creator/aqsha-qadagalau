package kz.aqsha.qadagalau.data

import java.util.Locale
import java.util.UUID

enum class TxType { EXPENSE, INCOME }

enum class Lang(val tag: String, val label: String) {
    KK("kk-KZ", "Қазақша"),
    RU("ru-RU", "Русский"),
    EN("en-US", "English"),
    TR("tr-TR", "Türkçe");

    fun locale(): Locale = Locale.forLanguageTag(tag)
}

/** defaultRate = how many tenge one unit of the currency costs (editable in settings). */
enum class Currency(val code: String, val symbol: String, val defaultRate: Double) {
    KZT("KZT", "₸", 1.0),
    RUB("RUB", "₽", 6.3),
    USD("USD", "$", 510.0),
    TRY("TRY", "₺", 14.5)
}

/**
 * key != null means a built-in category whose name is translated automatically.
 * name != null means the user typed their own name.
 */
data class Category(
    val id: String,
    val key: String?,
    val name: String?,
    val icon: String,
    val color: Long,
    val type: TxType
)

/** All amounts are stored in tenge (KZT) and converted for display. */
data class Transaction(
    val id: String,
    val amountKzt: Double,
    val categoryId: String,
    val note: String,
    val date: Long,
    val type: TxType,
    val recurringId: String? = null
)

enum class RecurringGroup { CREDIT, FOOD, UTILITIES, OTHER }

data class Recurring(
    val id: String,
    val name: String,
    val amountKzt: Double,
    val group: RecurringGroup,
    val day: Int,
    val paidMonths: Set<String> = emptySet()
)

data class Settings(
    val lang: Lang = Lang.RU,
    val currency: Currency = Currency.KZT,
    val primary: Long = 0xFF6DBE86,
    /** null = automatic text color */
    val textColor: Long? = null,
    val dark: Boolean = false,
    val rates: Map<Currency, Double> = Currency.entries.associateWith { it.defaultRate }
) {
    fun rate(): Double = rates[currency]?.takeIf { it > 0 } ?: currency.defaultRate
}

fun newId(): String = UUID.randomUUID().toString()
