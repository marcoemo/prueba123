package com.example.amilimetros.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.amilimetros.data.local.product.ProductEntity
import com.example.amilimetros.data.local.animal.AnimalEntity
import com.example.amilimetros.ui.viewmodel.ProductViewModel
import com.example.amilimetros.ui.viewmodel.AnimalViewModel

@Composable
fun AdminScreen(
    productVm: ProductViewModel,
    animalVm: AnimalViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Productos", "Animales")

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Panel de Administrador",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> AdminProductsTab(productVm)
            1 -> AdminAnimalsTab(animalVm)
        }
    }
}

// ========== TAB DE PRODUCTOS ==========
@Composable
private fun AdminProductsTab(vm: ProductViewModel) {
    val products by vm.products.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar Producto")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                AdminProductCard(
                    product = product,
                    onEdit = { editingProduct = product },
                    onDelete = { vm.deleteProduct(product) }
                )
            }
        }
    }

    // Diálogo agregar
    if (showAddDialog) {
        ProductDialog(
            product = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { product ->
                vm.addProduct(product)
                showAddDialog = false
            }
        )
    }

    // Diálogo editar
    editingProduct?.let { product ->
        ProductDialog(
            product = product,
            onDismiss = { editingProduct = null },
            onConfirm = { updated ->
                vm.updateProduct(updated)
                editingProduct = null
            }
        )
    }
}

@Composable
private fun AdminProductCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${product.price} • Stock: ${product.stock}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Deseas eliminar ${product.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ========== TAB DE ANIMALES ==========
@Composable
private fun AdminAnimalsTab(vm: AnimalViewModel) {
    val animals by vm.allAnimals.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAnimal by remember { mutableStateOf<AnimalEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar Animal")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(animals) { animal ->
                AdminAnimalCard(
                    animal = animal,
                    onEdit = { editingAnimal = animal },
                    onDelete = { vm.deleteAnimal(animal) }
                )
            }
        }
    }

    // Diálogo agregar
    if (showAddDialog) {
        AnimalDialog(
            animal = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { animal ->
                vm.addAnimal(animal)
                showAddDialog = false
            }
        )
    }

    // Diálogo editar
    editingAnimal?.let { animal ->
        AnimalDialog(
            animal = animal,
            onDismiss = { editingAnimal = null },
            onConfirm = { updated ->
                vm.updateAnimal(updated)
                editingAnimal = null
            }
        )
    }
}

@Composable
private fun AdminAnimalCard(
    animal: AnimalEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${animal.species} • ${animal.breed} • ${animal.age} años",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (animal.isAdopted) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Adoptado") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Animal") },
            text = { Text("¿Deseas eliminar a ${animal.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ========== DIÁLOGOS ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
    product: ProductEntity?,
    onDismiss: () -> Unit,
    onConfirm: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "Alimento") }

    val categories = listOf("Alimento", "Juguetes", "Accesorios", "Higiene", "Salud")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Agregar Producto" else "Editar Producto") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    val stockValue = stock.toIntOrNull() ?: 0
                    onConfirm(
                        ProductEntity(
                            id = product?.id ?: 0L,
                            name = name,
                            description = description,
                            price = priceValue,
                            stock = stockValue,
                            category = category
                        )
                    )
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalDialog(
    animal: AnimalEntity?,
    onDismiss: () -> Unit,
    onConfirm: (AnimalEntity) -> Unit
) {
    var name by remember { mutableStateOf(animal?.name ?: "") }
    var species by remember { mutableStateOf(animal?.species ?: "Perro") }
    var breed by remember { mutableStateOf(animal?.breed ?: "") }
    var age by remember { mutableStateOf(animal?.age?.toString() ?: "") }
    var description by remember { mutableStateOf(animal?.description ?: "") }

    val speciesList = listOf("Perro", "Gato", "Ave", "Conejo", "Otro")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (animal == null) "Agregar Animal" else "Editar Animal") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = species,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Especie") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        speciesList.forEach { sp ->
                            DropdownMenuItem(
                                text = { Text(sp) },
                                onClick = {
                                    species = sp
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Raza") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Edad (años)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val ageValue = age.toIntOrNull() ?: 0
                    onConfirm(
                        AnimalEntity(
                            id = animal?.id ?: 0L,
                            name = name,
                            species = species,
                            breed = breed,
                            age = ageValue,
                            description = description,
                            isAdopted = animal?.isAdopted ?: false
                        )
                    )
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}