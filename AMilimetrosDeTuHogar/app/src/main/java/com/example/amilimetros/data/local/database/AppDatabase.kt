package com.example.amilimetros.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.amilimetros.data.local.user.*
import com.example.amilimetros.data.local.product.*
import com.example.amilimetros.data.local.animal.*
import com.example.amilimetros.data.local.cart.*
import com.example.amilimetros.data.local.adoption.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.amilimetros.data.local.logo.*

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        AnimalEntity::class,
        CartItemEntity::class,
        AdoptionFormEntity::class ,
                LogoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun animalDao(): AnimalDao
    abstract fun cartDao(): CartDao
    abstract fun adoptionFormDao(): AdoptionFormDao
    abstract fun logoDao(): LogoDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "tienda_app.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    // Si cambias versión y no quieres migraciones, conservar fallback (ya estaba)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                // Lanzar un seed seguro: si la BD está vacía, popularla
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val userCount = instance.userDao().count()
                        if (userCount == 0) {
                            seedDatabase(instance)
                        }
                    } catch (e: Exception) {
                        // opcional: loguea el error si quieres
                        // Log.e("AppDatabase", "Error al seedear DB", e)
                    }
                }

                instance
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            // ========== USUARIOS ==========
            db.userDao().insert(
                UserEntity(
                    name = "Admin",
                    email = "admin@tienda.cl",
                    phone = "+56911111111",
                    password = "Admin123!_",
                    isAdmin = true
                )
            )

            db.userDao().insert(
                UserEntity(
                    name = "Cliente Demo",
                    email = "cliente@tienda.cl",
                    phone = "+56922222222",
                    password = "Cliente123!_",
                    isAdmin = false
                )
            )

            // ========== PRODUCTOS ==========
            val products = listOf(
                ProductEntity(
                    name = "Alimento Premium Perro",
                    description = "Alimento balanceado para perros adultos 15kg",
                    price = 35990.0,

                    category = "Alimento"
                ),
                ProductEntity(
                    name = "Pelota Interactiva",
                    description = "Pelota de goma resistente con sonido",
                    price = 8990.0,

                    category = "Juguetes"
                ),
                ProductEntity(
                    name = "Collar Ajustable",
                    description = "Collar ajustable de nylon para perros",
                    price = 6990.0,

                    category = "Accesorios"
                ),
                ProductEntity(
                    name = "Arena para Gatos",
                    description = "Arena sanitaria aglomerante 10kg",
                    price = 12990.0,

                    category = "Alimento"
                ),
                ProductEntity(
                    name = "Rascador para Gatos",
                    description = "Rascador de sisal con plataforma",
                    price = 25990.0,

                    category = "Accesorios"
                )
            )
            products.forEach { db.productDao().insert(it) }

            // ========== ANIMALES ==========
            val animals = listOf(
                AnimalEntity(
                    name = "Max",
                    species = "Perro",
                    breed = "Labrador",
                    age = 3,
                    description = "Perro cariñoso y juguetón, ideal para familias",
                    isAdopted = false
                ),
                AnimalEntity(
                    name = "Luna",
                    species = "Gato",
                    breed = "Siamés",
                    age = 2,
                    description = "Gata tranquila y afectuosa",
                    isAdopted = false
                ),
                AnimalEntity(
                    name = "Rocky",
                    species = "Perro",
                    breed = "Pastor Alemán",
                    age = 5,
                    description = "Perro guardián, entrenado y leal",
                    isAdopted = false
                ),
                AnimalEntity(
                    name = "Mimi",
                    species = "Gato",
                    breed = "Persa",
                    age = 1,
                    description = "Gatita juguetona y curiosa",
                    isAdopted = false
                )
            )
            animals.forEach { db.animalDao().insert(it) }
        }
    }
}
