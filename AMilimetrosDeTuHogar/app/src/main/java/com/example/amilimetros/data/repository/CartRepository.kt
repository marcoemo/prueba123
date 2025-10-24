package com.example.amilimetros.data.repository


import com.example.amilimetros.data.local.cart.CartDao
import com.example.amilimetros.data.local.cart.CartItemEntity
import com.example.amilimetros.data.local.product.ProductEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    fun getCartItems(userId: Long): Flow<List<CartItemEntity>> =
        cartDao.getCartByUserFlow(userId)

    fun getCartTotal(userId: Long): Flow<Double?> = cartDao.getTotalFlow(userId)

    suspend fun addToCart(userId: Long, product: ProductEntity, quantity: Int = 1): Result<Unit> {
        return try {
            val existing = cartDao.getItemByProduct(userId, product.id)
            if (existing != null) {
                // Incrementar cantidad
                cartDao.update(existing.copy(quantity = existing.quantity + quantity))
            } else {
                // Nuevo item
                cartDao.insert(
                    CartItemEntity(
                        userId = userId,
                        productId = product.id,
                        productName = product.name,
                        productPrice = product.price,
                        quantity = quantity,
                        imageUrl = product.imageUrl
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateQuantity(item: CartItemEntity, newQuantity: Int): Result<Unit> {
        return try {
            if (newQuantity <= 0) {
                cartDao.delete(item)
            } else {
                cartDao.update(item.copy(quantity = newQuantity))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(item: CartItemEntity): Result<Unit> {
        return try {
            cartDao.delete(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(userId: Long): Result<Unit> {
        return try {
            cartDao.clearCart(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}