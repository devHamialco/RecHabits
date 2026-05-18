package com.rechabits.app.presentation.ui.screen.habitedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rechabits.app.domain.model.Schedule
import com.rechabits.app.presentation.viewmodel.HabitEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEditScreen(
    habitId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: HabitEditViewModel = hiltViewModel()
) {
    LaunchedEffect(habitId) {
        if (habitId != null && habitId > 0) {
            viewModel.loadHabit(habitId)
        }
    }

    LaunchedEffect(viewModel.uiState.saveSuccess) {
        if (viewModel.uiState.saveSuccess) {
            onNavigateBack()
            viewModel.resetSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (viewModel.uiState.formState.isEditing) "Editar hábito" else "Nuevo hábito",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Name field
            OutlinedTextField(
                value = viewModel.uiState.formState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Nombre del hábito") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Icon picker
            Text("Ícono", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            IconPicker(
                selectedIcon = viewModel.uiState.formState.iconId,
                onIconSelected = { viewModel.updateIcon(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Color picker
            Text("Color", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ColorPicker(
                selectedColor = viewModel.uiState.formState.colorHex,
                onColorSelected = { viewModel.updateColor(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target amount (for quantitative habits)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cantidad objetivo", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(
                        "1 para hábitos sí/no, más para cuantitativos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedTextField(
                    value = viewModel.uiState.formState.targetAmount.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { amount ->
                            viewModel.updateTargetAmount(amount)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(80.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Unit field
            OutlinedTextField(
                value = viewModel.uiState.formState.unit,
                onValueChange = { viewModel.updateUnit(it) },
                label = { Text("Unidad (ej: ml, km, piezas)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Schedule editor
            Text("Recordatorios", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ScheduleEditor(
                schedules = viewModel.uiState.formState.schedules,
                onAddSchedule = { hour, minute -> viewModel.addSchedule(hour, minute) },
                onRemoveSchedule = { viewModel.removeSchedule(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            viewModel.uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Save button
            Button(
                onClick = { viewModel.saveHabit() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !viewModel.uiState.isSaving
            ) {
                Text(
                    if (viewModel.uiState.isSaving) "Guardando..." else "Guardar",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun IconPicker(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    val icons = listOf(
        "water_drop" to "\uD83D\uDCA7",
        "run" to "\uD83C\uDFC3\u200D\u2640\uFE0F",
        "apple" to "\uD83C\uDF4E",
        "tooth" to "\uD83E\uDBB7",
        "moon" to "\uD83C\uDF19",
        "sun" to "\u2600\uFE0F",
        "cream" to "\uD83E\uDDF4",
        "shield" to "\uD83D\uDEE1\uFE0F",
        "plant" to "\uD83C\uDF31"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        icons.forEach { (id, emoji) ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = if (id == selectedIcon) 2.dp else 1.dp,
                        color = if (id == selectedIcon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { onIconSelected(id) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "#6650a4" to "Violeta",
        "#1E88E5" to "Azul",
        "#43A047" to "Verde",
        "#E53935" to "Rojo",
        "#FB8C00" to "Naranja",
        "#FDD835" to "Amarillo",
        "#8D6E63" to "Marrón",
        "#EC407A" to "Rosa"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        colors.forEach { (hex, _) ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(hex)),
                        shape = CircleShape
                    )
                    .border(
                        width = if (hex == selectedColor) 3.dp else 0.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(hex) }
            )
        }
    }
}

@Composable
fun ScheduleEditor(
    schedules: List<Schedule>,
    onAddSchedule: (Int, Int) -> Unit,
    onRemoveSchedule: (Schedule) -> Unit
) {
    Column {
        // Existing schedules
        schedules.forEach { schedule ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = schedule.toTimeString(),
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onRemoveSchedule(schedule) }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove")
                }
            }
        }

        // Add button
        Button(
            onClick = { onAddSchedule(8, 0) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Agregar recordatorio (8:00 AM)")
        }

        Text(
            "Toca para agregar, luego edita la hora",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
