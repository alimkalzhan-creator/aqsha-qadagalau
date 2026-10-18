package kz.aqsha.qadagalau.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kz.aqsha.qadagalau.data.Lang
import java.util.Locale

/** One phrase in Kazakh, Russian, English and Turkish. */
class T(val kk: String, val ru: String, val en: String, val tr: String) {
    fun get(l: Lang): String = when (l) {
        Lang.KK -> kk
        Lang.RU -> ru
        Lang.EN -> en
        Lang.TR -> tr
    }

    fun f(l: Lang, vararg args: Any): String = String.format(Locale.ROOT, get(l), *args)
}

val LocalLang = staticCompositionLocalOf { Lang.RU }

@Composable
fun tr(t: T): String = t.get(LocalLang.current)

@Composable
fun trf(t: T, vararg args: Any): String = t.f(LocalLang.current, *args)

object S {
    const val APP = "Aqsha Qadagalau"
    val subtitle = T("Ақшаны бақылау", "Контроль денег", "Money control", "Para kontrolü")

    // navigation
    val home = T("Басты бет", "Главная", "Home", "Ana sayfa")
    val categories = T("Санаттар", "Категории", "Categories", "Kategoriler")
    val recurring = T("Тұрақты шығындар", "Постоянные расходы", "Fixed expenses", "Sabit giderler")
    val analytics = T("Талдау және кеңестер", "Аналитика и советы", "Analytics & tips", "Analiz ve öneriler")
    val voice = T("Дауыспен енгізу", "Голосовой ввод", "Voice input", "Sesli giriş")
    val history = T("Тарих", "История", "History", "Geçmiş")
    val settings = T("Баптаулар", "Настройки", "Settings", "Ayarlar")

    // common
    val income = T("Кіріс", "Доход", "Income", "Gelir")
    val expense = T("Шығыс", "Расход", "Expense", "Gider")
    val incomes = T("Кірістер", "Доходы", "Income", "Gelirler")
    val expenses = T("Шығыстар", "Расходы", "Expenses", "Giderler")
    val balance = T("Баланс", "Баланс", "Balance", "Bakiye")
    val amount = T("Сома", "Сумма", "Amount", "Tutar")
    val category = T("Санат", "Категория", "Category", "Kategori")
    val note = T("Ескертпе", "Заметка", "Note", "Not")
    val date = T("Күні", "Дата", "Date", "Tarih")
    val save = T("Сақтау", "Сохранить", "Save", "Kaydet")
    val cancel = T("Болдырмау", "Отмена", "Cancel", "İptal")
    val delete = T("Жою", "Удалить", "Delete", "Sil")
    val add = T("Қосу", "Добавить", "Add", "Ekle")
    val name = T("Атауы", "Название", "Name", "Ad")
    val icon = T("Белгіше", "Иконка", "Icon", "Simge")
    val color = T("Түс", "Цвет", "Color", "Renk")
    val more = T("Барлығы", "Ещё", "More", "Daha")
    val today = T("Бүгін", "Сегодня", "Today", "Bugün")
    val noData = T("Бұл айда жазба жоқ", "В этом месяце нет записей", "No records this month", "Bu ay kayıt yok")
    val newExpense = T("Жаңа шығыс", "Новый расход", "New expense", "Yeni gider")
    val newIncome = T("Жаңа кіріс", "Новый доход", "New income", "Yeni gelir")
    val deleteTxQ = T("Жазбаны жою керек пе?", "Удалить запись?", "Delete this record?", "Kayıt silinsin mi?")

    // categories
    val newCategory = T("Жаңа санат", "Новая категория", "New category", "Yeni kategori")
    val editCategory = T("Санатты өзгерту", "Изменить категорию", "Edit category", "Kategoriyi düzenle")
    val deleteCategoryQ = T(
        "Санатты жою керек пе? Оның жазбалары «Басқа» санатына ауысады.",
        "Удалить категорию? Её записи перейдут в «Прочее».",
        "Delete this category? Its records will move to “Other”.",
        "Kategori silinsin mi? Kayıtları “Diğer”e taşınacak."
    )
    val cantDeleteLast = T(
        "Соңғы санатты жоюға болмайды",
        "Нельзя удалить последнюю категорию",
        "You can't delete the last category",
        "Son kategori silinemez"
    )

    // settings
    val general = T("Жалпы", "Общие", "General", "Genel")
    val appearance = T("Сыртқы түрі", "Оформление", "Appearance", "Görünüm")
    val data = T("Деректер", "Данные", "Data", "Veriler")
    val language = T("Тіл", "Язык", "Language", "Dil")
    val currency = T("Валюта", "Валюта", "Currency", "Para birimi")
    val rates = T("Валюта бағамы (теңгемен)", "Курсы валют (в тенге)", "Exchange rates (in tenge)", "Döviz kurları (tenge)")
    val ratesHint = T(
        "Сомалар теңгемен сақталады және таңдалған валютаға аударылады.",
        "Суммы хранятся в тенге и пересчитываются в выбранную валюту.",
        "Amounts are stored in tenge and converted to the selected currency.",
        "Tutarlar tenge olarak saklanır ve seçilen para birimine çevrilir."
    )
    val interfaceColor = T("Интерфейс түсі", "Цвет интерфейса", "Interface color", "Arayüz rengi")
    val textColor = T("Мәтін түсі", "Цвет текста", "Text color", "Metin rengi")
    val auto = T("Авто", "Авто", "Auto", "Otomatik")
    val darkMode = T("Қараңғы тақырып", "Тёмная тема", "Dark theme", "Koyu tema")
    val customHex = T("Өз түсіңіз (HEX)", "Свой цвет (HEX)", "Custom color (HEX)", "Özel renk (HEX)")
    val preview = T("Алдын ала қарау", "Предпросмотр", "Preview", "Önizleme")
    val previewText = T(
        "Мәтін осылай көрінеді",
        "Так будет выглядеть текст",
        "This is how text will look",
        "Metin böyle görünecek"
    )
    val resetData = T("Барлық деректерді өшіру", "Сбросить все данные", "Reset all data", "Tüm verileri sıfırla")
    val resetQ = T(
        "Барлық жазбалар, санаттар мен төлемдер жойылады. Жалғастыру керек пе?",
        "Все записи, категории и платежи будут удалены. Продолжить?",
        "All records, categories and payments will be deleted. Continue?",
        "Tüm kayıtlar, kategoriler ve ödemeler silinecek. Devam edilsin mi?"
    )

    // analytics
    val whereMoney = T("Ақша қайда кетеді", "Куда уходят деньги", "Where your money goes", "Paranız nereye gidiyor")
    val tips = T("Үнемдеу кеңестері", "Советы по экономии", "Saving tips", "Tasarruf önerileri")
    val savingsRate = T("Жинақ үлесі", "Доля сбережений", "Savings rate", "Tasarruf oranı")
    val dailyAvg = T("Күніне орташа", "В среднем в день", "Daily average", "Günlük ortalama")
    val forecast = T("Ай соңына болжам", "Прогноз на месяц", "Month forecast", "Ay sonu tahmini")
    val vsPrev = T("өткен аймен", "к прошлому месяцу", "vs last month", "geçen aya göre")

    // recurring
    val monthlyTotal = T("Айына барлығы", "Всего в месяц", "Monthly total", "Aylık toplam")
    val paid = T("Төленді", "Оплачено", "Paid", "Ödendi")
    val remaining = T("Қалды", "Осталось", "Remaining", "Kalan")
    val pay = T("Төлеу", "Оплатить", "Pay", "Öde")
    val dayOfMonth = T("Төлем күні (1–31)", "День оплаты (1–31)", "Payment day (1–31)", "Ödeme günü (1–31)")
    val dayN = T("%d-күні", "%d числа", "on day %d", "ayın %d'i")
    val inDays = T("%d күннен кейін", "через %d дн.", "in %d days", "%d gün sonra")
    val overdue = T("Мерзімі өтті", "Просрочено", "Overdue", "Gecikti")
    val groupCredit = T("Несиелер", "Кредиты", "Loans", "Krediler")
    val groupFood = T("Тамақ", "Еда", "Food", "Gıda")
    val groupUtil = T("Коммуналдық қызметтер", "Коммунальные услуги", "Utilities", "Faturalar")
    val groupOther = T("Басқа", "Другое", "Other", "Diğer")
    val newPayment = T("Жаңа төлем", "Новый платёж", "New payment", "Yeni ödeme")
    val editPayment = T("Төлемді өзгерту", "Изменить платёж", "Edit payment", "Ödemeyi düzenle")
    val ofIncome = T("кірістің %d%%-ы", "%d%% от дохода", "%d%% of income", "gelirin %%%d'i")
    val recurringEmpty = T(
        "Төлем қосу үшін + басыңыз",
        "Нажмите +, чтобы добавить платёж",
        "Tap + to add a payment",
        "Ödeme eklemek için + tuşuna basın"
    )
    val payHint = T(
        "«Төлеу» басылғанда шығыс автоматты түрде жазылады.",
        "При нажатии «Оплатить» расход записывается автоматически.",
        "Tapping “Pay” records the expense automatically.",
        "“Öde”ye basınca gider otomatik kaydedilir."
    )

    // voice
    val tapToSpeak = T("Басып, сөйлеңіз", "Нажмите и говорите", "Tap and speak", "Dokun ve konuş")
    val voiceHint = T(
        "Мысалы: «Такси 1500, түскі ас 3200 және жалақы 250 мың»",
        "Например: «Такси 1500, обед 3200 и зарплата 250 тысяч»",
        "For example: “Taxi 1500, lunch 3200 and salary 250 thousand”",
        "Örneğin: “Taksi 1500, öğle yemeği 3200 ve maaş 250 bin”"
    )
    val orType = T("Немесе мәтінмен жазыңыз", "Или напишите текстом", "Or type it", "Ya da yazın")
    val recognize = T("Талдау", "Распознать", "Parse", "Ayrıştır")
    val addAll = T("Барлығын қосу", "Добавить все", "Add all", "Tümünü ekle")
    val added = T("%d жазба қосылды", "Добавлено записей: %d", "%d records added", "%d kayıt eklendi")
    val nothingFound = T(
        "Сома табылмады. Санат пен соманы атаңыз.",
        "Не нашёл сумму. Назовите категорию и сумму.",
        "No amount found. Say a category and an amount.",
        "Tutar bulunamadı. Bir kategori ve tutar söyleyin."
    )
    val noRecognizer = T(
        "Құрылғыда дауысты тану қызметі жоқ (Google қолданбасын орнатыңыз).",
        "На устройстве нет службы распознавания речи (установите приложение Google).",
        "No speech recognition on this device (install the Google app).",
        "Cihazda konuşma tanıma yok (Google uygulamasını yükleyin)."
    )
    val monthSummary = T("Ай қорытындысы", "Итоги месяца", "Month summary", "Ay özeti")
    val found = T("Табылды", "Найдено", "Found", "Bulunan")

    // tips (positional args so word order can differ by language)
    val tipOverspend = T(
        "Осы айда шығыс кірістен %1\$s артық. Қысқартуды «%2\$s» санатынан бастаңыз.",
        "В этом месяце расходы превышают доходы на %1\$s. Начните сокращение с категории «%2\$s».",
        "Spending exceeds income by %1\$s this month. Start cutting with “%2\$s”.",
        "Bu ay giderler gelirleri %1\$s aşıyor. Kesintiye “%2\$s” ile başlayın."
    )
    val tipTopShare = T(
        "«%1\$s» — барлық шығыстың %2\$d%%-ы. Оған айлық лимит қойып, аптасына бір рет тексеріңіз.",
        "На «%1\$s» уходит %2\$d%% всех расходов. Поставьте месячный лимит и проверяйте его раз в неделю.",
        "“%1\$s” takes %2\$d%% of all spending. Set a monthly limit and check it weekly.",
        "Harcamaların %%%2\$d'i “%1\$s” kategorisine gidiyor. Aylık limit belirleyip haftada bir kontrol edin."
    )
    val tipGrowth = T(
        "«%1\$s» шығыны өткен аймен салыстырғанда %2\$d%% өсті. Не өзгергенін тексеріңіз.",
        "Расходы на «%1\$s» выросли на %2\$d%% к прошлому месяцу. Проверьте, что изменилось.",
        "“%1\$s” spending grew %2\$d%% vs last month. Check what changed.",
        "“%1\$s” harcaması geçen aya göre %%%2\$d arttı. Neyin değiştiğine bakın."
    )
    val tipCafe = T(
        "Кафеге %s кетті. Аптасына 2–3 рет үйде тамақ дайындасаңыз, осы соманың жартысына дейін үнемдейсіз.",
        "На кафе ушло %s. Готовьте дома 2–3 раза в неделю — так можно сэкономить до половины этой суммы.",
        "Cafes cost you %s. Cooking at home 2–3 times a week can save up to half of that.",
        "Kafelere %s harcandı. Haftada 2–3 kez evde yemek yaparak bunun yarısına kadar tasarruf edebilirsiniz."
    )
    val tipTransport = T(
        "Көлік пен таксиге %s кетті. Қысқа жолдарға қоғамдық көлікті пайдаланыңыз немесе жаяу жүріңіз.",
        "На транспорт и такси ушло %s. Для коротких поездок используйте общественный транспорт или ходите пешком.",
        "Transport and taxis cost %s. Use public transport or walk for short trips.",
        "Ulaşım ve taksiye %s gitti. Kısa mesafelerde toplu taşıma kullanın veya yürüyün."
    )
    val tipFood = T(
        "Азық-түлікке %s кетті. Дүкенге тізіммен және тоқ қарынмен барыңыз — артық сатып алу азаяды.",
        "На продукты ушло %s. Ходите в магазин со списком и не на голодный желудок — лишних покупок станет меньше.",
        "Groceries cost %s. Shop with a list and never hungry — fewer impulse buys.",
        "Markete %s gitti. Listeyle ve tok karnına alışveriş yapın — gereksiz alımlar azalır."
    )
    val tipCredit = T(
        "Несиелерге %s кетті. Пайызы ең жоғары несиені мерзімінен бұрын өтеңіз — бұл артық төлемді азайтады.",
        "На кредиты ушло %s. Досрочно гасите кредит с самой высокой ставкой — это сильнее всего снижает переплату.",
        "Loans cost %s. Pay off the highest-rate loan early — it cuts overpayment the most.",
        "Kredilere %s gitti. En yüksek faizli krediyi erken kapatın — fazla ödemeyi en çok bu azaltır."
    )
    val tipFixed = T(
        "Тұрақты төлемдер кірістің %d%%-ын алады. Несиелерді қайта қаржыландыруды және қажетсіз жазылымдардан бас тартуды қарастырыңыз.",
        "Постоянные платежи забирают %d%% дохода. Рассмотрите рефинансирование кредитов и отказ от ненужных подписок.",
        "Fixed payments take %d%% of income. Consider refinancing loans and cancelling unused subscriptions.",
        "Sabit ödemeler gelirin %%%d'ini alıyor. Kredileri yapılandırmayı ve gereksiz abonelikleri iptal etmeyi düşünün."
    )
    val tipLowSavings = T(
        "Сіз кірістің тек %d%%-ын жинайсыз. 50/30/20 ережесін қолданып көріңіз: 50%% — қажеттіліктер, 30%% — қалаулар, 20%% — жинақ.",
        "Вы откладываете лишь %d%% дохода. Попробуйте правило 50/30/20: 50%% — нужды, 30%% — желания, 20%% — сбережения.",
        "You save only %d%% of income. Try the 50/30/20 rule: 50%% needs, 30%% wants, 20%% savings.",
        "Gelirin yalnızca %%%d'ini biriktiriyorsunuz. 50/30/20 kuralını deneyin: %%50 ihtiyaç, %%30 istek, %%20 birikim."
    )
    val tipGoodSavings = T(
        "Тамаша! Кірістің %d%%-ын үнемдедіңіз. Артығын депозитке салыңыз.",
        "Отлично! Вы сохранили %d%% дохода. Переведите излишек на депозит.",
        "Great! You kept %d%% of your income. Move the surplus to a savings deposit.",
        "Harika! Gelirin %%%d'ini korudunuz. Fazlasını mevduata aktarın."
    )
    val general1 = T(
        "Жалақы түскен күні бірден 10%-ын жинақ шотына аударыңыз.",
        "В день зарплаты сразу переводите 10% на сберегательный счёт.",
        "On payday, move 10% to savings right away.",
        "Maaş günü hemen %10'unu birikim hesabına aktarın."
    )
    val general2 = T(
        "Ірі сатып алу алдында 24 сағат күтіңіз — көп нәрсе қажет болмай шығады.",
        "Перед крупной покупкой подождите 24 часа — многое окажется ненужным.",
        "Wait 24 hours before a big purchase — many wants fade.",
        "Büyük bir alışverişten önce 24 saat bekleyin — çoğu istek geçer."
    )
    val general3 = T(
        "Жазылымдарды ай сайын тексеріп, пайдаланбайтындарын өшіріңіз.",
        "Раз в месяц проверяйте подписки и отключайте неиспользуемые.",
        "Review subscriptions monthly and cancel the unused ones.",
        "Abonelikleri her ay gözden geçirip kullanmadıklarınızı iptal edin."
    )
    val general4 = T(
        "Ең көп жұмсайтын санаттарыңызға кэшбэк қосыңыз.",
        "Подключите кэшбэк на категории, где тратите больше всего.",
        "Turn on cashback for your top spending categories.",
        "En çok harcadığınız kategorilerde nakit iadesini açın."
    )
    val general5 = T(
        "Қаржы жастығы — 3–6 айлық шығын. Оны бөлек шотта ұстаңыз.",
        "Финансовая подушка — это 3–6 месячных расходов. Держите её на отдельном счёте.",
        "An emergency fund is 3–6 months of expenses. Keep it in a separate account.",
        "Acil durum fonu 3–6 aylık giderdir. Ayrı bir hesapta tutun."
    )
}
