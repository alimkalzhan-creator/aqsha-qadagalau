package kz.aqsha.qadagalau.data

/** amount is in the currently selected display currency. */
data class ParsedItem(
    val type: TxType,
    val amount: Double,
    val categoryId: String,
    val note: String,
    val uid: String = newId()
)

/**
 * Turns a phrase like "такси 1500, обед 3200 и зарплата 250 тысяч"
 * (or the same in Kazakh / English / Turkish) into separate records.
 */
object VoiceParser {

    private val separators = Regex(
        "\\s*,(?!\\d)\\s*|\\s*;\\s*|\\.\\s+|\\s+(?:и|а также|также|потом|ещё|еще|плюс|and|also|plus|then|ve|ayrıca|sonra|artı|және|тағы|содан соң)\\s+"
    )

    private val number = Regex(
        "(\\d+(?:[  .,]\\d{3})*(?:[.,]\\d{1,2})?)\\s*" +
            "(тысяч\\p{L}*|тыс\\.?|мың\\p{L}*|мын\\p{L}*|bin|thousand\\p{L}*|k(?!\\p{L})|к(?!\\p{L})|" +
            "млн|миллион\\p{L}*|million\\p{L}*|milyon)?"
    )

    private val incomeWords = listOf(
        "зарплат", "получил", "получила", "доход", "заработ", "премия", "премию", "аванс", "перевели мне", "вернули",
        "жалақы", "жалакы", "табыс", "кіріс", "түсті", "сыйақы", "айлық",
        "salary", "income", "earned", "received", "got paid", "bonus", "refund",
        "maaş", "maas", "gelir", "kazandım", "ikramiye", "iade"
    )

    private val stopWords = setOf(
        "на", "за", "в", "по", "и", "я", "мне", "потратил", "потратила", "заплатил", "заплатила", "купил", "купила",
        "тенге", "тг", "тнг", "теңге", "₸", "рублей", "рубля", "рубль", "руб", "₽", "долларов", "доллара", "доллар", "$",
        "лир", "лиры", "лира", "₺", "tl", "lira", "for", "on", "the", "a", "spent", "paid", "bought",
        "tenge", "rubles", "dollars", "için", "ödedim", "harcadım", "үшін", "төледім", "жұмсадым"
    )

    fun parse(text: String): List<ParsedItem> {
        val lower = text.lowercase()
        val out = mutableListOf<ParsedItem>()
        for (raw in separators.split(lower)) {
            val seg = raw.trim()
            if (seg.isEmpty()) continue
            val matches = number.findAll(seg).toList()
            if (matches.isEmpty()) continue
            if (matches.size == 1) {
                val m = matches[0]
                addItem(out, seg.removeRange(m.range), m)
            } else {
                val head = cleanLabel(seg.substring(0, matches.first().range.first))
                val tail = cleanLabel(seg.substring(matches.last().range.last + 1))
                if (head.isEmpty() && tail.isNotEmpty()) {
                    // "1500 такси 3200 обед" — label goes after each number
                    matches.forEachIndexed { i, m ->
                        val end = if (i + 1 < matches.size) matches[i + 1].range.first else seg.length
                        addItem(out, seg.substring(m.range.last + 1, end), m, skipTiny = true)
                    }
                } else {
                    // "такси 1500 обед 3200" — label goes before each number
                    var prevEnd = 0
                    for (m in matches) {
                        addItem(out, seg.substring(prevEnd, m.range.first), m, skipTiny = true)
                        prevEnd = m.range.last + 1
                    }
                }
            }
        }
        return out
    }

    private fun addItem(out: MutableList<ParsedItem>, labelRaw: String, m: MatchResult, skipTiny: Boolean = false) {
        val base = toNumber(m.groupValues[1]) ?: return
        val amount = base * multiplier(m.groupValues[2])
        if (amount <= 0) return
        // "за 2 дня потратил 5000" — ignore small counters when a phrase has several numbers
        if (skipTiny && amount < 10) return
        val label = cleanLabel(labelRaw)
        val type = detectType(label)
        val cat = matchCategory(label, type) ?: Repo.fallbackCategory(type) ?: return
        out += ParsedItem(type, amount, cat.id, label.replaceFirstChar { it.uppercase() })
    }

    private fun toNumber(raw: String): Double? {
        var s = raw.replace(" ", "").replace(" ", "")
        val dec = Regex("[.,](\\d{1,2})$").find(s)
        var decimals = ""
        if (dec != null) {
            decimals = dec.groupValues[1]
            s = s.substring(0, dec.range.first)
        }
        s = s.replace(".", "").replace(",", "")
        return (if (decimals.isEmpty()) s else "$s.$decimals").toDoubleOrNull()
    }

    private fun multiplier(word: String): Double {
        val w = word.lowercase()
        return when {
            w.isEmpty() -> 1.0
            w.startsWith("млн") || w.startsWith("миллион") || w.startsWith("million") || w.startsWith("milyon") -> 1_000_000.0
            else -> 1000.0
        }
    }

    private fun cleanLabel(raw: String): String =
        raw.split(Regex("\\s+"))
            .map { it.trim(',', '.', '!', '?', ':', '-', '—') }
            .filter { it.isNotEmpty() && it !in stopWords }
            .joinToString(" ")

    private fun detectType(label: String): TxType {
        if (incomeWords.any { label.contains(it) }) return TxType.INCOME
        val inc = matchCategory(label, TxType.INCOME)
        val exp = matchCategory(label, TxType.EXPENSE)
        return if (inc != null && exp == null) TxType.INCOME else TxType.EXPENSE
    }

    private fun stem(w: String): String {
        val s = w.lowercase().trim()
        return when {
            s.length >= 7 -> s.dropLast(2)
            s.length >= 5 -> s.dropLast(1)
            else -> s
        }
    }

    private fun keywords(c: Category): List<String> {
        val list = mutableListOf<String>()
        c.name?.let { n -> list += n.split(Regex("\\s+")) }
        c.key?.let { k ->
            Defaults.names[k]?.let { t -> listOf(t.kk, t.ru, t.en, t.tr).forEach { list += it.split(Regex("[\\s,]+")) } }
            Defaults.aliases[k]?.let { list += it }
        }
        return list
    }

    fun matchCategory(label: String, type: TxType): Category? {
        if (label.isBlank()) return null
        var best: Category? = null
        var bestLen = 0
        for (c in Repo.categories) {
            if (c.type != type) continue
            for (kw in keywords(c)) {
                val st = stem(kw)
                if (st.length < 3) continue
                if (label.contains(st) && st.length > bestLen) {
                    best = c
                    bestLen = st.length
                }
            }
        }
        return best
    }
}
