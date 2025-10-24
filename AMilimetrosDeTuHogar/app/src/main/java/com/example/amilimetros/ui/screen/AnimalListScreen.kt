package com.example.amilimetros.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.amilimetros.data.local.animal.AnimalEntity
import com.example.amilimetros.ui.viewmodel.AnimalViewModel

@Composable
fun AnimalListScreen(
    vm: AnimalViewModel,
    onNavigateToAdoptionForm: (Long) -> Unit  // ✅ NUEVO parámetro
) {
    val animals by vm.availableAnimals.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMsg) {
        uiState.successMsg?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMsg) {
        uiState.errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "🐾 Animales en Adopción",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            if (animals.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay animales disponibles para adopción")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(animals) { animal ->
                        AnimalCard(
                            animal = animal,
                            onAdopt = { onNavigateToAdoptionForm(animal.id) }  // ✅ CAMBIADO
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimalCard(
    animal: AnimalEntity,
    onAdopt: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${animal.species} • ${animal.breed}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${animal.age} año${if (animal.age != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = animal.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(12.dp))


                Button(
                    onClick = onAdopt,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Solicitar Adopción de ${animal.name}")
                }
            }

            Spacer(Modifier.width(12.dp))

            animal.imageUrl?.let { url ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .crossfade(true)
                        .build(),
                    contentDescription = animal.name,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}