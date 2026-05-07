package com.unilibre.tallerescompose.taller02

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

/**
 * Data model for a task item.
 */
data class TaskItem(
    val id: Int,
    val title: String,
    val category: String,
    val isCompleted: Boolean = false
)

/**
 * Taller 02: Lists and Validations.
 * Demonstrates LazyRow, LazyColumn, Dialogs, and TextFields with state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen() {
    // State for the list of tasks
    var tasks by remember { mutableStateOf(listOf<TaskItem>()) }
    
    // State for the text field input
    var taskTitle by remember { mutableStateOf("") }
    var taskError by remember { mutableStateOf<String?>(null) }
    
    // State for category selection (LazyRow)
    val categories = listOf("General", "Trabajo", "Estudio", "Hogar")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    
    // State for the AlertDialog
    var selectedTaskForDialog by remember { mutableStateOf<TaskItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Listas y Validaciones") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Section 1: Validated TextField to add tasks
            Text("Nueva Tarea", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { 
                    taskTitle = it
                    if (it.isNotEmpty()) taskError = null 
                },
                label = { Text("Nombre de la tarea") },
                modifier = Modifier.fillMaxWidth(),
                isError = taskError != null,
                supportingText = { taskError?.let { Text(it) } },
                trailingIcon = {
                    IconButton(onClick = {
                        // Validation logic
                        if (taskTitle.isBlank()) {
                            taskError = "El título no puede estar vacío"
                        } else {
                            val newTask = TaskItem(
                                id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                                title = taskTitle,
                                category = selectedCategory
                            )
                            tasks = tasks + newTask
                            taskTitle = "" // Clear field
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: LazyRow for Categories
            Text("Filtrar por Categoría", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        leadingIcon = if (selectedCategory == category) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: LazyColumn for the list of tasks
            Text("Mis Tareas ($selectedCategory)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            val filteredTasks = tasks.filter { it.category == selectedCategory }
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { selectedTaskForDialog = task },
                        onToggleCompleted = {
                            tasks = tasks.map { 
                                if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it 
                            }
                        },
                        onDelete = {
                            tasks = tasks.filter { it.id != task.id }
                        }
                    )
                }
            }
        }
    }

    // Section 4: AlertDialog for task details
    selectedTaskForDialog?.let { task ->
        AlertDialog(
            onDismissRequest = { selectedTaskForDialog = null },
            confirmButton = {
                TextButton(onClick = { selectedTaskForDialog = null }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Detalle de Tarea") },
            text = {
                Column {
                    Text("Título: ${task.title}", fontWeight = FontWeight.Bold)
                    Text("Categoría: ${task.category}")
                    Text("Estado: ${if (task.isCompleted) "Completada" else "Pendiente"}")
                    Text("ID: #${task.id}")
                }
            },
            icon = { Icon(Icons.Default.Info, contentDescription = null) }
        )
    }
}

/**
 * Single card for the task list.
 */
@Composable
fun TaskCard(
    task: TaskItem, 
    onClick: () -> Unit, 
    onToggleCompleted: () -> Unit, 
    onDelete: () -> Unit
) {
    val alphaAnim by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.5f else 1f,
        label = "alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompleted() }
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(alphaAnim)
            ) {
                Text(
                    text = task.title, 
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Text(text = "Cat: ${task.category}", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete, 
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.Info, 
                    contentDescription = "Info", 
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
