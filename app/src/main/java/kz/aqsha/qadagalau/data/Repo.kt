package kz.aqsha.qadagalau.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.min

/** Single source of truth. Everything is observable by Compose and saved to SharedPreferences. */
object Repo {
    private lateinit var prefs: SharedPreferences

    val categories = mutableStateListOf<Category>()
    val transactions = mutableStateListOf<Transaction>()
    val recurring = mutableStateListOf<Recurring>()

    var settings by mutableStateOf(Settings())
        private set

    fun init(ctx: Context) {
        if (::prefs.isInitialized) return
        prefs = ctx.getSharedPreferences("aqsha_data", Context.MODE_PRIVATE)
        loadSettings()
        loadData()
    }

    // ---------- currency ----------
    fun toBase(display: Double): Double = display * settings.rate()
    fun toDisplay(kzt: Double): Double = kzt / settings.rate()

    // ---------- transactions ----------
    fun addTx(t: Transaction) {
        transactions.add(t); saveData()
    }

    fun deleteTx(id: String) {
        val t = transactions.firstOrNull { it.id == id }
        transactions.removeAll { it.id == id }
        // If it was a fixed payment, un-mark that month as paid.
        val rid = t?.recurringId
        if (t != null && rid != null) {
            val i = recurring.indexOfFirst { it.id == rid }
            if (i >= 0) {
                val key = monthOf(t.date).toString()
                recurring[i] = recurring[i].copy(paidMonths = recurring[i].paidMonths - key)
            }
        }
        saveData()
    }

    // ---------- categories ----------
    fun category(id: String): Category? = categories.firstOrNull { it.id == id }

    fun upsertCategory(c: Category) {
        val i = categories.indexOfFirst { it.id == c.id }
        if (i >= 0) categories[i] = c else categories.add(c)
        saveData()
    }

    /** Returns false if it is the last category of its type. */
    fun deleteCategory(id: String): Boolean {
        val c = category(id) ?: return false
        val same = categories.filter { it.type == c.type && it.id != id }
        if (same.isEmpty()) return false
        val target = same.firstOrNull { it.key == "other" || it.key == "other_in" } ?: same.last()
        for (i in transactions.indices) {
            if (transactions[i].categoryId == id) transactions[i] = transactions[i].copy(categoryId = target.id)
        }
        categories.removeAll { it.id == id }
        saveData()
        return true
    }

    fun fallbackCategory(type: TxType): Category? {
        val key = if (type == TxType.EXPENSE) "other" else "other_in"
        return categories.firstOrNull { it.key == key && it.type == type }
            ?: categories.lastOrNull { it.type == type }
    }

    // ---------- recurring ----------
    fun upsertRecurring(r: Recurring) {
        val i = recurring.indexOfFirst { it.id == r.id }
        if (i >= 0) recurring[i] = r else recurring.add(r)
        saveData()
    }

    fun deleteRecurring(id: String) {
        recurring.removeAll { it.id == id }
        saveData()
    }

    fun isPaid(r: Recurring, month: YearMonth): Boolean = month.toString() in r.paidMonths

    fun togglePaid(r: Recurring, month: YearMonth) {
        val key = month.toString()
        val idx = recurring.indexOfFirst { it.id == r.id }
        if (idx < 0) return
        val cur = recurring[idx]
        if (key in cur.paidMonths) {
            transactions.removeAll { it.recurringId == cur.id && monthOf(it.date) == month }
            recurring[idx] = cur.copy(paidMonths = cur.paidMonths - key)
        } else {
            val cat = categoryForGroup(cur.group) ?: return
            val date = if (month == YearMonth.now()) LocalDate.now()
            else month.atDay(min(cur.day.coerceAtLeast(1), month.lengthOfMonth()))
            transactions.add(
                Transaction(
                    id = newId(), amountKzt = cur.amountKzt, categoryId = cat.id, note = cur.name,
                    date = date.toMillis(), type = TxType.EXPENSE, recurringId = cur.id
                )
            )
            recurring[idx] = cur.copy(paidMonths = cur.paidMonths + key)
        }
        saveData()
    }

    private fun categoryForGroup(g: RecurringGroup): Category? {
        val key = when (g) {
            RecurringGroup.CREDIT -> "credit"
            RecurringGroup.FOOD -> "food"
            RecurringGroup.UTILITIES -> "utilities"
            RecurringGroup.OTHER -> "other"
        }
        return categories.firstOrNull { it.key == key && it.type == TxType.EXPENSE }
            ?: fallbackCategory(TxType.EXPENSE)
    }

    // ---------- settings ----------
    fun updateSettings(s: Settings) {
        settings = s
        saveSettings()
    }

    fun resetAll() {
        transactions.clear(); recurring.clear(); categories.clear()
        seed()
        saveData()
    }

    // ---------- persistence ----------
    private fun seed() {
        Defaults.categories.forEach {
            categories.add(Category(newId(), it.key, null, it.icon, it.color, it.type))
        }
    }

    private fun saveData() {
        val o = JSONObject()
        o.put("cats", JSONArray().apply {
            categories.forEach { c ->
                put(JSONObject().apply {
                    put("id", c.id)
                    put("key", c.key ?: JSONObject.NULL)
                    put("name", c.name ?: JSONObject.NULL)
                    put("icon", c.icon)
                    put("color", c.color)
                    put("type", c.type.name)
                })
            }
        })
        o.put("txs", JSONArray().apply {
            transactions.forEach { t ->
                put(JSONObject().apply {
                    put("id", t.id)
                    put("amount", t.amountKzt)
                    put("cat", t.categoryId)
                    put("note", t.note)
                    put("date", t.date)
                    put("type", t.type.name)
                    put("rec", t.recurringId ?: JSONObject.NULL)
                })
            }
        })
        o.put("rec", JSONArray().apply {
            recurring.forEach { r ->
                put(JSONObject().apply {
                    put("id", r.id)
                    put("name", r.name)
                    put("amount", r.amountKzt)
                    put("group", r.group.name)
                    put("day", r.day)
                    put("paid", JSONArray(r.paidMonths.toList()))
                })
            }
        })
        prefs.edit().putString("data", o.toString()).apply()
    }

    private fun JSONObject.str(k: String): String? = if (!has(k) || isNull(k)) null else getString(k)

    private fun loadData() {
        val raw = prefs.getString("data", null)
        if (raw == null) {
            seed(); saveData(); return
        }
        try {
            val o = JSONObject(raw)
            val cats = o.getJSONArray("cats")
            for (i in 0 until cats.length()) {
                val j = cats.getJSONObject(i)
                categories.add(
                    Category(
                        id = j.getString("id"), key = j.str("key"), name = j.str("name"),
                        icon = j.getString("icon"), color = j.getLong("color"),
                        type = TxType.valueOf(j.getString("type"))
                    )
                )
            }
            val txs = o.getJSONArray("txs")
            for (i in 0 until txs.length()) {
                val j = txs.getJSONObject(i)
                transactions.add(
                    Transaction(
                        id = j.getString("id"), amountKzt = j.getDouble("amount"),
                        categoryId = j.getString("cat"), note = j.optString("note", ""),
                        date = j.getLong("date"), type = TxType.valueOf(j.getString("type")),
                        recurringId = j.str("rec")
                    )
                )
            }
            val rec = o.optJSONArray("rec") ?: JSONArray()
            for (i in 0 until rec.length()) {
                val j = rec.getJSONObject(i)
                val paidArr = j.optJSONArray("paid") ?: JSONArray()
                val paid = (0 until paidArr.length()).map { paidArr.getString(it) }.toSet()
                recurring.add(
                    Recurring(
                        id = j.getString("id"), name = j.getString("name"),
                        amountKzt = j.getDouble("amount"),
                        group = RecurringGroup.valueOf(j.getString("group")),
                        day = j.optInt("day", 1), paidMonths = paid
                    )
                )
            }
            if (categories.none { it.type == TxType.EXPENSE } || categories.none { it.type == TxType.INCOME }) {
                categories.clear(); seed()
            }
        } catch (e: Exception) {
            categories.clear(); transactions.clear(); recurring.clear()
            seed()
        }
    }

    private fun saveSettings() {
        val s = settings
        val o = JSONObject()
        o.put("lang", s.lang.name)
        o.put("currency", s.currency.name)
        o.put("primary", s.primary)
        o.put("text", s.textColor ?: JSONObject.NULL)
        o.put("dark", s.dark)
        o.put("rates", JSONObject().apply { s.rates.forEach { (c, r) -> put(c.name, r) } })
        prefs.edit().putString("settings", o.toString()).apply()
    }

    private fun loadSettings() {
        val raw = prefs.getString("settings", null) ?: return
        try {
            val o = JSONObject(raw)
            val ratesObj = o.optJSONObject("rates")
            val rates = Currency.entries.associateWith { c ->
                ratesObj?.optDouble(c.name, c.defaultRate)?.takeIf { it > 0 } ?: c.defaultRate
            }
            settings = Settings(
                lang = runCatching { Lang.valueOf(o.getString("lang")) }.getOrDefault(Lang.RU),
                currency = runCatching { Currency.valueOf(o.getString("currency")) }.getOrDefault(Currency.KZT),
                primary = o.optLong("primary", 0xFF6DBE86),
                textColor = if (o.isNull("text")) null else o.getLong("text"),
                dark = o.optBoolean("dark", false),
                rates = rates
            )
        } catch (_: Exception) {
        }
    }
}
