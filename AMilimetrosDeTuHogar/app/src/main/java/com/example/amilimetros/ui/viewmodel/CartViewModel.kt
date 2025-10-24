package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.cart.CartItemEntity
import com.example.amilimetros.data.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val userId: Long
) : ViewModel() {

    // Flow de items del carrito (se actualiza automáticamente)
    val cartItems = cartRepository.getCartItems(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow del total (se calcula automáticamente)
    val total = cartRepository.getCartTotal(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ========== INCREMENTAR CANTIDAD ==========
    fun incrementQuantity(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item, item.quantity + 1)
        }
    }

    // ========== DECREMENTAR CANTIDAD ==========
    fun decrementQuantity(item: CartItemEntity) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                cartRepository.updateQuantity(item, item.quantity - 1)
            } else {
                // Si llega a 0, eliminar el item
                cartRepository.removeFromCart(item)
            }
        }
    }

    // ========== ELIMINAR ITEM ==========
    fun removeItem(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item)
        }
    }

    // ========== VACIAR CARRITO ==========
    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart(userId)
        }
    }

    // ========== PROCESAR COMPRA (TODO: implementar lógica de pago) ==========
    fun checkout() {
        viewModelScope.launch {
            // TODO: Aquí irías a una pantalla de pago o procesarías la orden
            // Por ahora solo vaciamos el carrito
            cartRepository.clearCart(userId)
        }
    }
}