package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.product.ProductEntity
import com.example.amilimetros.data.repository.ProductRepository
import com.example.amilimetros.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductUiState(
    val isLoading: Boolean = true,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    // Lista de productos (Flow que se actualiza automáticamente)
    val products = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Agregar producto al carrito
    fun addToCart(userId: Long, product: ProductEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = cartRepository.addToCart(userId, product, 1)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    successMsg = "${product.name} agregado al carrito",
                    errorMsg = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al agregar al carrito",
                    successMsg = null
                )
            }
        }
    }

    // Admin: Agregar producto
    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            val result = productRepository.addProduct(product)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(successMsg = "Producto agregado", errorMsg = null)
            } else {
                _uiState.value.copy(errorMsg = "Error al agregar producto", successMsg = null)
            }
        }
    }

    // Admin: Actualizar producto
    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            val result = productRepository.updateProduct(product)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(successMsg = "Producto actualizado", errorMsg = null)
            } else {
                _uiState.value.copy(errorMsg = "Error al actualizar", successMsg = null)
            }
        }
    }

    // Admin: Eliminar producto
    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            val result = productRepository.deleteProduct(product)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(successMsg = "Producto eliminado", errorMsg = null)
            } else {
                _uiState.value.copy(errorMsg = "Error al eliminar", successMsg = null)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMsg = null, errorMsg = null)
    }
}