package kz.aqsha.qadagalau.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

/** Icons a category can use. Keys are stored in the database. */
val AppIcons: LinkedHashMap<String, ImageVector> = linkedMapOf(
    "basket" to Icons.Outlined.ShoppingBasket,
    "restaurant" to Icons.Outlined.Restaurant,
    "home" to Icons.Outlined.Home,
    "bolt" to Icons.Outlined.Bolt,
    "card" to Icons.Outlined.CreditCard,
    "taxi" to Icons.Outlined.LocalTaxi,
    "car" to Icons.Outlined.DirectionsCar,
    "phone" to Icons.Outlined.Phone,
    "shirt" to Icons.Outlined.Checkroom,
    "health" to Icons.Outlined.MedicalServices,
    "bag" to Icons.Outlined.ShoppingBag,
    "gift" to Icons.Outlined.CardGiftcard,
    "sport" to Icons.Outlined.SportsSoccer,
    "tag" to Icons.Outlined.Sell,
    "payments" to Icons.Outlined.Payments,
    "work" to Icons.Outlined.Work,
    "savings" to Icons.Outlined.Savings,
    "bar" to Icons.Outlined.LocalBar,
    "cafe" to Icons.Outlined.LocalCafe,
    "fastfood" to Icons.Outlined.Fastfood,
    "water" to Icons.Outlined.WaterDrop,
    "wifi" to Icons.Outlined.Wifi,
    "bank" to Icons.Outlined.AccountBalance,
    "money" to Icons.Outlined.AttachMoney,
    "school" to Icons.Outlined.School,
    "child" to Icons.Outlined.ChildCare,
    "pets" to Icons.Outlined.Pets,
    "flight" to Icons.Outlined.Flight,
    "movie" to Icons.Outlined.Movie,
    "game" to Icons.Outlined.SportsEsports,
    "fitness" to Icons.Outlined.FitnessCenter,
    "build" to Icons.Outlined.Build,
    "gas" to Icons.Outlined.LocalGasStation,
    "bus" to Icons.Outlined.DirectionsBus,
    "spa" to Icons.Outlined.Spa,
    "party" to Icons.Outlined.Celebration,
    "pharmacy" to Icons.Outlined.LocalPharmacy,
    "hospital" to Icons.Outlined.LocalHospital,
    "apartment" to Icons.Outlined.Apartment,
    "smartphone" to Icons.Outlined.Smartphone,
    "laptop" to Icons.Outlined.Laptop,
    "subs" to Icons.Outlined.Subscriptions,
    "receipt" to Icons.Outlined.Receipt,
    "diamond" to Icons.Outlined.Diamond,
    "handshake" to Icons.Outlined.Handshake,
    "face" to Icons.Outlined.Face,
    "category" to Icons.Outlined.Category
)

fun iconFor(key: String): ImageVector = AppIcons[key] ?: Icons.Outlined.Category
