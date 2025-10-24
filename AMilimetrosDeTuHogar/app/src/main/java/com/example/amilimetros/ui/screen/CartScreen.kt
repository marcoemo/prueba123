package com.example.amilimetros.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.amilimetros.data.local.cart.CartItemEntity
import com.example.amilimetros.data.local.storage.UserPreferences
import com.example.amilimetros.ui.viewmodel.CartViewModel
import com.example.amilimetros.ui.viewmodel.CartViewModelFactory
import com.example.amilimetros.data.repository.CartRepository
import com.example.amilimetros.data.local.database.AppDatabase

@Composable
fun CartScreen() {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val userId by userPrefs.userId.collectAsStateWithLifecycle(0L)

    // Crear ViewModel
    val db = remember { AppDatabase.getInstance(context) }
    val cartRepo = remember { CartRepository(db.cartDao()) }
    val vm: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepo, userId)
    )

    val cartItems by vm.cartItems.collectAsStateWithLifecycle()
    val total by vm.total.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "🛍️ Mi Carrito",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onIncrement = { vm.incrementQuantity(item) },
                            onDecrement = { vm.decrementQuantity(item) },
                            onRemove = { vm.removeItem(item) }
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // ========== TOTAL ==========
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${total ?: 0.0}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ========== BOTONES ==========
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Vaciar Carrito")
                    }

                    Button(
                        onClick = { /* TODO: Procesar compra */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Comprar")
                    }
                }
            }
        }
    }

    // ========== DIÁLOGO CONFIRMAR VACIAR ==========
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Vaciar Carrito") },
            text = { Text("¿Deseas eliminar todos los productos del carrito?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearCart()
                    showClearDialog = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItemEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "$${item.productPrice} c/u",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Subtotal: $${item.productPrice * item.quantity}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Control de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onDecrement) {
                        Icon(Icons.Filled.Remove, contentDescription = "Disminuir")
                    }

                    Text(
                        text = "${item.quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onIncrement) {
                        Icon(Icons.Filled.Add, contentDescription = "Aumentar")
                    }
                }

                // Botón eliminar
                FilledTonalButton(
                    onClick = onRemove,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}