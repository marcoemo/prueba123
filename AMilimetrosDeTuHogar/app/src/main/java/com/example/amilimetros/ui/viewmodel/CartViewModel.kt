package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.cart.CartItemEntity
import com.example.amilimetros.data.repository.CartRepository
import com.example.amilimetros.ui.notification.NotificationManager
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
            NotificationManager.showInfo("Cantidad actualizada")
        }
    }

    // ========== DECREMENTAR CANTIDAD ==========
    fun decrementQuantity(item: CartItemEntity) {
        viewModelScope.launch {
            if (item.quantity > 1) {
                cartRepository.updateQuantity(item, item.quantity - 1)
                NotificationManager.showInfo("Cantidad actualizada")
            } else {
                // Si llega a 0, eliminar el item
                cartRepository.removeFromCart(item)
                NotificationManager.showSuccess("Producto eliminado del carrito")
            }
        }
    }

    // ========== ELIMINAR ITEM ==========
    fun removeItem(item: CartItemEntity) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item)
            NotificationManager.showSuccess("Producto eliminado del carrito")
        }
    }

    // ========== VACIAR CARRITO ==========
    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart(userId)
            NotificationManager.showSuccess("Carrito vaciado")
        }
    }

    // ========== PROCESAR COMPRA ==========
    fun checkout() {
        viewModelScope.launch {
            cartRepository.clearCart(userId)
            NotificationManager.showSuccess("¡Compra realizada con éxito!")
        }
    }
}