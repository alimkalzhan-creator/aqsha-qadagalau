package kz.aqsha.qadagalau.data

import kz.aqsha.qadagalau.i18n.T

object Defaults {

    data class DefCat(val key: String, val icon: String, val color: Long, val type: TxType)

    val categories = listOf(
        DefCat("food", "basket", 0xFFE5739A, TxType.EXPENSE),
        DefCat("cafe", "restaurant", 0xFFF0A040, TxType.EXPENSE),
        DefCat("home", "home", 0xFF5B9BD5, TxType.EXPENSE),
        DefCat("utilities", "bolt", 0xFF3FA7A3, TxType.EXPENSE),
        DefCat("credit", "card", 0xFFC98B5B, TxType.EXPENSE),
        DefCat("transport", "taxi", 0xFFE0B020, TxType.EXPENSE),
        DefCat("car", "car", 0xFF6E7B95, TxType.EXPENSE),
        DefCat("phone", "phone", 0xFF9C8DC1, TxType.EXPENSE),
        DefCat("clothes", "shirt", 0xFFB06BD6, TxType.EXPENSE),
        DefCat("health", "health", 0xFFE05A5A, TxType.EXPENSE),
        DefCat("shopping", "bag", 0xFFC9A46E, TxType.EXPENSE),
        DefCat("gifts", "gift", 0xFFB8939B, TxType.EXPENSE),
        DefCat("sport", "sport", 0xFF5FB3AC, TxType.EXPENSE),
        DefCat("other", "tag", 0xFF9E9E9E, TxType.EXPENSE),
        DefCat("salary", "payments", 0xFF4CAF7D, TxType.INCOME),
        DefCat("business", "work", 0xFF2E8B57, TxType.INCOME),
        DefCat("gift_in", "gift", 0xFFB06BD6, TxType.INCOME),
        DefCat("other_in", "savings", 0xFF7E9C8A, TxType.INCOME)
    )

    val names: Map<String, T> = mapOf(
        "food" to T("Азық-түлік", "Продукты", "Groceries", "Market"),
        "cafe" to T("Кафе", "Кафе и рестораны", "Cafes", "Kafe ve restoran"),
        "home" to T("Тұрғын үй", "Жильё", "Housing", "Konut"),
        "utilities" to T("Коммуналдық", "Коммуналка", "Utilities", "Faturalar"),
        "credit" to T("Несиелер", "Кредиты", "Loans", "Krediler"),
        "transport" to T("Көлік, такси", "Транспорт", "Transport", "Ulaşım"),
        "car" to T("Автокөлік", "Автомобиль", "Car", "Araba"),
        "phone" to T("Байланыс", "Связь", "Phone", "Telefon"),
        "clothes" to T("Киім", "Одежда", "Clothes", "Giyim"),
        "health" to T("Денсаулық", "Здоровье", "Health", "Sağlık"),
        "shopping" to T("Сатып алу", "Покупки", "Shopping", "Alışveriş"),
        "gifts" to T("Сыйлықтар", "Подарки", "Gifts", "Hediyeler"),
        "sport" to T("Спорт", "Спорт", "Sport", "Spor"),
        "other" to T("Басқа", "Прочее", "Other", "Diğer"),
        "salary" to T("Жалақы", "Зарплата", "Salary", "Maaş"),
        "business" to T("Бизнес", "Бизнес", "Business", "İş"),
        "gift_in" to T("Сыйлық", "Подарки", "Gifts", "Hediye"),
        "other_in" to T("Басқа кіріс", "Прочий доход", "Other income", "Diğer gelir")
    )

    /** Extra words (all four languages) used by the voice parser to find the category. */
    val aliases: Map<String, List<String>> = mapOf(
        "food" to listOf(
            "продукт", "магазин", "еда", "супермаркет", "хлеб", "молоко", "мясо", "овощ", "фрукт",
            "азық", "дүкен", "нан", "сүт", "ет", "көкөніс", "тамақ",
            "grocer", "food", "supermarket", "bread", "milk", "meat",
            "gıda", "süt", "ekmek", "et ", "sebze", "meyve", "bakkal"
        ),
        "cafe" to listOf(
            "кафе", "ресторан", "обед", "ужин", "завтрак", "кофе", "пицц", "бургер", "шаурм", "доставк",
            "түскі", "кешкі", "таңғы", "мейрамхана",
            "cafe", "restaurant", "lunch", "dinner", "breakfast", "coffee", "pizza",
            "kahve", "restoran", "öğle", "akşam yemeği", "kahvaltı"
        ),
        "home" to listOf(
            "аренд", "квартир", "жиль", "ипотек",
            "жалдау", "пәтер", "үй ",
            "rent", "house", "apartment", "flat",
            "kira", "ev ", "daire"
        ),
        "utilities" to listOf(
            "коммун", "свет", "газ", "вода", "электр", "отоплен", "квартплат",
            "коммуналд", "жарық", "жылу",
            "utilit", "electric", "water", "heating", "bill",
            "fatura", "elektrik", "doğalgaz", "aidat"
        ),
        "credit" to listOf(
            "кредит", "займ", "рассрочк", "долг", "ипотек",
            "несие", "қарыз", "бөліп төлеу",
            "loan", "credit", "mortgage", "debt", "instal",
            "kredi", "borç", "taksit"
        ),
        "transport" to listOf(
            "такси", "автобус", "метро", "проезд", "транспорт", "яндекс", "uber",
            "taxi", "bus", "metro", "transport",
            "taksi", "otobüs", "ulaşım"
        ),
        "car" to listOf(
            "бензин", "заправ", "машин", "авто", "парковк", "мойк", "шиномонтаж",
            "жанармай", "көлік жуу",
            "fuel", "petrol", "gasoline", "parking", "car ",
            "benzin", "araba", "otopark", "yakıt"
        ),
        "phone" to listOf(
            "телефон", "связь", "мобильн", "интернет", "сотов",
            "байланыс", "ұялы",
            "phone", "mobile", "internet",
            "telefon", "internet", "hat"
        ),
        "clothes" to listOf(
            "одежд", "обув", "куртк", "футболк", "джинс", "платье",
            "киім", "аяқ киім",
            "clothes", "shoes", "shirt", "jacket", "dress",
            "kıyafet", "ayakkabı", "giyim", "elbise"
        ),
        "health" to listOf(
            "аптек", "лекарств", "врач", "больниц", "стоматолог", "анализ", "клиник",
            "дәрі", "дәріхана", "дәрігер", "аурухана", "емхана",
            "pharmac", "medicine", "doctor", "hospital", "dentist",
            "eczane", "ilaç", "doktor", "hastane", "diş"
        ),
        "shopping" to listOf(
            "покупк", "шопинг", "маркетплейс", "kaspi", "каспи", "wildberries", "ozon", "техник",
            "сатып", "shopping", "purchase", "alışveriş"
        ),
        "gifts" to listOf("подар", "сыйлық", "gift", "present", "hediye"),
        "sport" to listOf(
            "спорт", "фитнес", "зал", "тренаж", "бассейн", "тренировк",
            "sport", "gym", "fitness", "pool",
            "spor", "salon", "havuz"
        ),
        "other" to listOf("прочее", "другое", "разное", "басқа", "other", "misc", "diğer"),
        "salary" to listOf(
            "зарплат", "аванс", "оклад", "жалақы", "жалакы", "айлық",
            "salary", "wage", "paycheck", "maaş", "maas"
        ),
        "business" to listOf(
            "бизнес", "продаж", "клиент", "фриланс", "заказ", "выручк",
            "сауда", "тапсырыс", "business", "sales", "freelance", "client",
            "satış", "müşteri", "sipariş"
        ),
        "gift_in" to listOf("подарили", "сыйлады", "gifted me", "hediye aldım"),
        "other_in" to listOf("кэшбэк", "кешбэк", "процент", "cashback", "interest", "faiz", "басқа кіріс")
    )
}
