package com.tahsen.cal

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Bg = Color(0xFF0B0D12)
private val Panel = Color(0xFF151922)
private val PanelAlt = Color(0xFF222838)
private val Accent = Color(0xFFB8A1FF)
private val MainText = Color(0xFFF3F2F8)
private val Muted = Color(0xFF969CAD)

data class CalEvent(val id: Long, val title: String, val date: String, val time: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CalTheme { CalendarScreen() } }
    }
}

@Composable
private fun CalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent, onPrimary = Bg, background = Bg,
            surface = Panel, onSurface = MainText, secondary = Color(0xFF8ED8CB)
        ),
        content = content
    )
}

@Composable
private fun CalendarScreen() {
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    var selected by remember { mutableStateOf(today) }
    var events by remember { mutableStateOf(readEvents(context)) }
    var dialogOpen by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00 AM") }

    fun saveEvents(next: List<CalEvent>) {
        events = next
        val json = JSONArray()
        next.forEach { event ->
            json.put(JSONObject().apply {
                put("id", event.id)
                put("title", event.title)
                put("date", event.date)
                put("time", event.time)
            })
        }
        context.getSharedPreferences("cal_data", Context.MODE_PRIVATE)
            .edit().putString("events", json.toString()).apply()
    }

    val dayEvents = events.filter { it.date == selected.toString() }.sortedBy { it.time }
    val monthLabel = month.format(DateTimeFormatter.ofPattern("MMMM", Locale.getDefault()))
    val dateLabel = selected.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))

    Scaffold(
        containerColor = Bg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { title = ""; time = "09:00 AM"; dialogOpen = true },
                containerColor = Accent, contentColor = Bg, shape = RoundedCornerShape(18.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Add event") }
        }
    ) { inset ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(inset),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("YOUR TIME, ORGANIZED", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("Cal", color = MainText, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }
                    Surface(shape = RoundedCornerShape(16.dp), color = Panel, modifier = Modifier.size(54.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = Accent, modifier = Modifier.size(27.dp))
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(monthLabel, color = MainText, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
                        Text(month.year.toString(), color = Muted, fontSize = 14.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { month = month.minusMonths(1) }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Previous month", tint = Muted, modifier = Modifier.size(17.dp))
                        }
                        IconButton(onClick = { month = month.plusMonths(1) }) {
                            Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next month", tint = Muted, modifier = Modifier.size(17.dp))
                        }
                        IconButton(onClick = { month = YearMonth.from(today); selected = today }) {
                            Icon(Icons.Default.Today, contentDescription = "Go to today", tint = Accent)
                        }
                    }
                }
            }
            item {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Panel).padding(horizontal = 9.dp, vertical = 16.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                            Text(day, Modifier.weight(1f), textAlign = TextAlign.Center, color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    val offset = month.atDay(1).dayOfWeek.value - 1
                    val total = ((offset + month.lengthOfMonth() + 6) / 7) * 7
                    for (week in 0 until total / 7) {
                        Row(Modifier.fillMaxWidth()) {
                            for (column in 0..6) {
                                val number = week * 7 + column - offset + 1
                                val valid = number in 1..month.lengthOfMonth()
                                val date = if (valid) month.atDay(number) else null
                                val chosen = date == selected
                                val isToday = date == today
                                val hasEvents = date != null && events.any { it.date == date.toString() }
                                Box(
                                    Modifier.weight(1f).aspectRatio(0.78f).padding(2.dp)
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(if (chosen) Accent else if (isToday) PanelAlt else Color.Transparent)
                                        .clickable(enabled = valid) { selected = date!! },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (valid) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(number.toString(), color = if (chosen) Bg else MainText, fontSize = 14.sp, fontWeight = if (chosen || isToday) FontWeight.Bold else FontWeight.Normal)
                                            Box(Modifier.size(4.dp).clip(CircleShape).background(if (!hasEvents) Color.Transparent else if (chosen) Bg else Accent))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(dateLabel, color = MainText, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                        Text(if (selected == today) "Today" else selected.year.toString(), color = Muted, fontSize = 13.sp)
                    }
                    Surface(color = PanelAlt, shape = RoundedCornerShape(12.dp)) {
                        Text(dayEvents.size.toString() + if (dayEvents.size == 1) " event" else " events", color = Accent, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            if (dayEvents.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Panel).padding(vertical = 28.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Muted, modifier = Modifier.size(30.dp))
                        Text("Nothing planned", color = MainText, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text("Your day is clear. Add an event when you're ready.", color = Muted, fontSize = 13.sp, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(dayEvents, key = { it.id }) { event ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Panel).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(Modifier.width(4.dp).height(48.dp).clip(CircleShape).background(Accent))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(event.title, color = MainText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            Text(event.time, color = Muted, fontSize = 13.sp)
                        }
                        IconButton(onClick = { saveEvents(events.filterNot { it.id == event.id }) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete event", tint = Muted)
                        }
                    }
                }
            }
        }
    }

    if (dialogOpen) {
        AlertDialog(
            onDismissRequest = { dialogOpen = false },
            containerColor = Panel,
            titleContentColor = MainText,
            textContentColor = Muted,
            title = { Text("New event", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(selected.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.getDefault())), color = Accent, fontSize = 13.sp)
                    OutlinedTextField(
                        value = title, onValueChange = { title = it }, label = { Text("Event title") }, singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, focusedLabelColor = Accent, cursorColor = Accent)
                    )
                    OutlinedTextField(
                        value = time, onValueChange = { time = it }, label = { Text("Time (e.g. 09:30 AM)") }, singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, focusedLabelColor = Accent, cursorColor = Accent)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (title.isNotBlank()) {
                        saveEvents(events + CalEvent(System.currentTimeMillis(), title.trim(), selected.toString(), time.trim().ifBlank { "All day" }))
                        dialogOpen = false
                    }
                }) { Text("Save event", color = Accent, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { dialogOpen = false }) { Text("Cancel", color = Muted) } }
        )
    }
}

private fun readEvents(context: Context): List<CalEvent> {
    val raw = context.getSharedPreferences("cal_data", Context.MODE_PRIVATE).getString("events", "[]") ?: "[]"
    return try {
        val array = JSONArray(raw)
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            CalEvent(item.optLong("id"), item.optString("title"), item.optString("date"), item.optString("time"))
        }
    } catch (_: Exception) {
        emptyList()
    }
}
