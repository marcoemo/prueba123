package com.example.amilimetros.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit,
    onHome: () -> Unit,
    onProducts: () -> Unit,
    onAnimals: () -> Unit,
    onCart: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(
                text = "🐾 A Mil Kilómetros",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = "Inicio")
            }
            IconButton(onClick = onProducts) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Productos")
            }
            IconButton(onClick = onAnimals) {
                Icon(Icons.Filled.Pets, contentDescription = "Animales")
            }
            IconButton(onClick = onCart) {
                Icon(Icons.Filled.ShoppingBag, contentDescription = "Carrito")
            }
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Más")
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Inicio") },
                    onClick = { showMenu = false; onHome() },
                    leadingIcon = { Icon(Icons.Filled.Home, null) }
                )
                DropdownMenuItem(
                    text = { Text("Productos") },
                    onClick = { showMenu = false; onProducts() },
                    leadingIcon = { Icon(Icons.Filled.ShoppingCart, null) }
                )
                DropdownMenuItem(
                    text = { Text("Animales") },
                    onClick = { showMenu = false; onAnimals() },
                    leadingIcon = { Icon(Icons.Filled.Pets, null) }
                )
                DropdownMenuItem(
                    text = { Text("Carrito") },
                    onClick = { showMenu = false; onCart() },
                    leadingIcon = { Icon(Icons.Filled.ShoppingBag, null) }
                )
            }
        }
    )
}