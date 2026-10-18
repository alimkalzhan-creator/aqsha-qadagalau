package kz.aqsha.qadagalau.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kz.aqsha.qadagalau.data.Category
import kz.aqsha.qadagalau.data.label
import kz.aqsha.qadagalau.i18n.LocalLang
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.primary,
    titleContentColor = Color.White,
    navigationIconContentColor = Color.White,
    actionIconContentColor = Color.White
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubScreen(
    title: String,
    onBack: () -> Unit,
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = appBarColors()
            )
        },
        floatingActionButton = fab,
        containerColor = MaterialTheme.colorScheme.background,
        content = content
    )
}

@Composable
fun MonthSwitcher(month: YearMonth, onChange: (YearMonth) -> Unit) {
    val lang = LocalLang.current
    val primary = MaterialTheme.colorScheme.primary
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onChange(month.minusMonths(1)) }) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = null, tint = primary)
        }
        Text(
            month.label(lang),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = primary,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = { onChange(month.plusMonths(1)) }) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = primary)
        }
    }
}

@Composable
fun CategoryIconCircle(cat: Category?, size: Dp = 40.dp, selected: Boolean = false) {
    val c = cat?.let { Color(it.color) } ?: Color.Gray
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (selected) c else c.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            iconFor(cat?.icon ?: "tag"),
            contentDescription = null,
            tint = if (selected) Color.White else c,
            modifier = Modifier.size(size * 0.56f)
        )
    }
}

@Composable
fun ColorSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    val ring = MaterialTheme.colorScheme.onBackground
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                BorderStroke(if (selected) 3.dp else 1.dp, if (selected) ring else ring.copy(alpha = 0.15f)),
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                Icons.Filled.Check, contentDescription = null,
                tint = if (color.luminance() > 0.6f) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** Ring chart like on the main screen. */
@Composable
fun DonutChart(
    segments: List<Pair<Color, Double>>,
    modifier: Modifier = Modifier,
    thickness: Float = 0.19f,
    center: @Composable ColumnScope.() -> Unit = {}
) {
    val track = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = size.minDimension * thickness
            val d = size.minDimension - stroke
            val topLeft = Offset((size.width - d) / 2f, (size.height - d) / 2f)
            val arc = Size(d, d)
            drawArc(track, 0f, 360f, false, topLeft, arc, style = Stroke(stroke))
            val total = segments.sumOf { it.second }
            if (total > 0) {
                var start = -90f
                segments.forEach { (color, v) ->
                    val sweep = (v / total * 360.0).toFloat()
                    drawArc(color, start, sweep, false, topLeft, arc, style = Stroke(stroke))
                    start += sweep
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, content = center)
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun StatColumn(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
