package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.Category
import kz.aqsha.qadagalau.data.Repo
import kz.aqsha.qadagalau.data.TxType
import kz.aqsha.qadagalau.data.newId
import kz.aqsha.qadagalau.data.title
import kz.aqsha.qadagalau.i18n.LocalLang
import kz.aqsha.qadagalau.i18n.S
import kz.aqsha.qadagalau.i18n.tr

@Composable
fun CategoriesScreen(onBack: () -> Unit) {
    val lang = LocalLang.current
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val type = if (tab == 0) TxType.EXPENSE else TxType.INCOME
    var editing by remember { mutableStateOf<Category?>(null) }
    var creating by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<Category?>(null) }
    var error by remember { mutableStateOf(false) }

    SubScreen(
        title = tr(S.categories),
        onBack = onBack,
        fab = {
            FloatingActionButton(
                onClick = { creating = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) { Icon(Icons.Filled.Add, contentDescription = tr(S.add)) }
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            TabRow(
                selectedTabIndex = tab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text(tr(S.expenses)) })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text(tr(S.incomes)) })
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Repo.categories.filter { it.type == type }, key = { it.id }) { c ->
                    AppCard(Modifier.clickable { editing = c }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryIconCircle(c, 44.dp)
                            Spacer(Modifier.width(14.dp))
                            Text(
                                c.title(lang),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { editing = c }) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { deleting = c }) {
                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = ExpenseColor)
                            }
                        }
                    }
                }
            }
        }
    }

    if (creating || editing != null) {
        CategoryEditor(initial = editing, defaultType = type) {
            creating = false
            editing = null
        }
    }

    deleting?.let { c ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text(c.title(lang)) },
            text = { Text(tr(S.deleteCategoryQ)) },
            confirmButton = {
                TextButton(onClick = {
                    if (!Repo.deleteCategory(c.id)) error = true
                    deleting = null
                }) { Text(tr(S.delete), color = ExpenseColor) }
            },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text(tr(S.cancel)) } }
        )
    }

    if (error) {
        AlertDialog(
            onDismissRequest = { error = false },
            text = { Text(tr(S.cantDeleteLast)) },
            confirmButton = { TextButton(onClick = { error = false }) { Text("OK") } }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryEditor(initial: Category?, defaultType: TxType, onDismiss: () -> Unit) {
    val lang = LocalLang.current
    var name by remember { mutableStateOf(initial?.title(lang) ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: defaultType) }
    var icon by remember { mutableStateOf(initial?.icon ?: "tag") }
    var color by remember { mutableStateOf(initial?.color ?: CategoryPalette.first()) }
    val preview = Category("", null, name, icon, color, type)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) tr(S.newCategory) else tr(S.editCategory)) },
        text = {
            Column(
                Modifier
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryIconCircle(preview, 52.dp, selected = true)
                    Spacer(Modifier.width(12.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(tr(S.name)) },
                        singleLine = true
                    )
                }
                if (initial == null) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = type == TxType.EXPENSE,
                            onClick = { type = TxType.EXPENSE },
                            label = { Text(tr(S.expense)) }
                        )
                        FilterChip(
                            selected = type == TxType.INCOME,
                            onClick = { type = TxType.INCOME },
                            label = { Text(tr(S.income)) }
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(tr(S.icon), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppIcons.keys.forEach { k ->
                        val sel = k == icon
                        Box(
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (sel) Color(color) else Color(color).copy(alpha = 0.12f))
                                .clickable { icon = k },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                iconFor(k), contentDescription = null,
                                tint = if (sel) Color.White else Color(color),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(tr(S.color), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryPalette.forEach { c ->
                        ColorSwatch(Color(c), c == color) { color = c }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    val keepDefaultName = initial != null && initial.key != null && name == initial.title(lang)
                    Repo.upsertCategory(
                        Category(
                            id = initial?.id ?: newId(),
                            key = initial?.key,
                            name = if (keepDefaultName) null else name.trim(),
                            icon = icon,
                            color = color,
                            type = type
                        )
                    )
                    onDismiss()
                }
            ) { Text(tr(S.save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(tr(S.cancel)) } }
    )
}
