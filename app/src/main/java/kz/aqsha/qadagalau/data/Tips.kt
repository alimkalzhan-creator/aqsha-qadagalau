package kz.aqsha.qadagalau.data

import kz.aqsha.qadagalau.i18n.S
import java.time.YearMonth
import kotlin.math.roundToInt

enum class TipKind { WARN, INFO, GOOD }

data class Tip(val text: String, val kind: TipKind)

/** Personal saving tips built from this month's numbers + a few general ones. */
fun buildTips(month: YearMonth, lang: Lang): List<Tip> {
    val st = statsFor(month)
    val prev = statsFor(month.minusMonths(1))
    val prevMap = prev.byCategory.associate { it.first.id to it.second }
    val out = mutableListOf<Tip>()

    fun byKey(k: String): Double = st.byCategory.filter { it.first.key == k }.sumOf { it.second }

    // 1. Spending more than earning
    if (st.income > 0 && st.expense > st.income) {
        val top = st.byCategory.firstOrNull()?.first?.title(lang) ?: "—"
        out += Tip(S.tipOverspend.f(lang, money(st.expense - st.income), top), TipKind.WARN)
    }

    // 2. One category eats too much
    val first = st.byCategory.firstOrNull()
    if (first != null && st.expense > 0 && st.byCategory.size > 1) {
        val p = (first.second / st.expense * 100).roundToInt()
        if (p >= 35) out += Tip(S.tipTopShare.f(lang, first.first.title(lang), p), TipKind.WARN)
    }

    // 3. Fast growth vs last month
    st.byCategory
        .mapNotNull { (c, v) ->
            val p = prevMap[c.id] ?: 0.0
            if (p > 0 && v > p * 1.25 && v - p > st.expense * 0.05) Triple(c, v, p) else null
        }
        .take(2)
        .forEach { (c, v, p) ->
            out += Tip(S.tipGrowth.f(lang, c.title(lang), ((v - p) / p * 100).roundToInt()), TipKind.WARN)
        }

    // 4. Category-specific advice
    if (st.expense > 0) {
        val cafe = byKey("cafe")
        if (cafe > st.expense * 0.10) out += Tip(S.tipCafe.f(lang, money(cafe)), TipKind.INFO)
        val transport = byKey("transport")
        if (transport > st.expense * 0.10) out += Tip(S.tipTransport.f(lang, money(transport)), TipKind.INFO)
        val food = byKey("food")
        if (food > st.expense * 0.30) out += Tip(S.tipFood.f(lang, money(food)), TipKind.INFO)
        val credit = byKey("credit")
        if (credit > 0 && (credit > st.expense * 0.25 || (st.income > 0 && credit > st.income * 0.2))) {
            out += Tip(S.tipCredit.f(lang, money(credit)), TipKind.WARN)
        }
    }

    // 5. Fixed payments vs income
    val fixed = Repo.recurring.sumOf { it.amountKzt }
    if (st.income > 0 && fixed > st.income * 0.5) {
        out += Tip(S.tipFixed.f(lang, (fixed / st.income * 100).roundToInt()), TipKind.WARN)
    }

    // 6. Savings rate
    if (st.income > 0) {
        val rate = ((st.income - st.expense) / st.income * 100).roundToInt()
        if (rate in 0..9) out += Tip(S.tipLowSavings.f(lang, rate), TipKind.INFO)
        else if (rate >= 20) out += Tip(S.tipGoodSavings.f(lang, rate), TipKind.GOOD)
    }

    // 7. General tips, rotated by month
    val general = listOf(S.general1, S.general2, S.general3, S.general4, S.general5)
    val start = month.monthValue % general.size
    for (i in 0 until 3) out += Tip(general[(start + i) % general.size].get(lang), TipKind.INFO)

    return out
}
