package com.unilibre.tallerescompose.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Data class representing a Workshop (Taller) item in the menu.
 */
data class TallerItem(
    val id: String,
    val number: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

/**
 * Main Menu Screen showing navigable cards for each workshop.
 * @param onNavigateToTaller Callback to handle navigation when a card is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(onNavigateToTaller: (String) -> Unit) {
    // List of workshops to display
    val talleres = listOf(
        TallerItem("1", "01", "Perfil y Bienvenida", "UI básica, modificadores e iconos.", Icons.Default.Person, "taller01"),
        TallerItem("2", "02", "Listas y Validaciones", "LazyColumn, Dialogs y TextFields.", Icons.Default.List, "taller02"),
        TallerItem("3", "03", "Estado y Retrofit", "ViewModels, APIs y Lottie.", Icons.Default.Cloud, "taller03"),
        TallerItem("4", "04", "Room y Gráficos", "Persistencia local y Vico charts.", Icons.Default.Storage, "taller04"),
        TallerItem("5", "05", "IA y Cámara", "CameraX, ML Kit y Recetas con IA.", Icons.Default.AutoAwesome, "taller05")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Talleres Compose", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(talleres) { taller ->
                TallerCard(taller = taller, onClick = { onNavigateToTaller(taller.route) })
            }
        }
    }
}

/**
 * Individual card component for a workshop.
 */
@Composable
fun TallerCard(taller: TallerItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Workshop number circle/badge
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = taller.number,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and Description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taller.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = taller.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Leading Icon
            Icon(
                imageVector = taller.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
