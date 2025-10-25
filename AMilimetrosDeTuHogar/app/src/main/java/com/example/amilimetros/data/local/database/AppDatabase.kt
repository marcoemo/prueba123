package com.example.amilimetros.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.amilimetros.data.local.user.*
import com.example.amilimetros.data.local.product.*
import com.example.amilimetros.data.local.animal.*
import com.example.amilimetros.data.local.cart.*
import com.example.amilimetros.data.local.adoption.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        AnimalEntity::class,
        CartItemEntity::class,
        AdoptionFormEntity::class
    ],
    version = 1, // ✅ VERSIÓN 1 (limpia)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun animalDao(): AnimalDao
    abstract fun cartDao(): CartDao
    abstract fun adoptionFormDao(): AdoptionFormDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "amilimetros_database"
                )
                    .fallbackToDestructiveMigration() // ✅ Recrea la BD si hay cambios
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            try {
                android.util.Log.d("AppDatabase", "🌱 Seeding database...")

                // ========== USUARIOS ==========
                val adminId = db.userDao().insert(
                    UserEntity(
                        name = "Admin",
                        email = "admin@amilimetros.com",
                        phone = "+56911111111",
                        password = "Admin123!_",
                        isAdmin = true
                    )
                )
                android.util.Log.d("AppDatabase", "✅ Admin created with id: $adminId")

                val userId = db.userDao().insert(
                    UserEntity(
                        name = "Usuario Demo",
                        email = "user@demo.com",
                        phone = "+56922222222",
                        password = "User123!_",
                        isAdmin = false
                    )
                )
                android.util.Log.d("AppDatabase", "✅ User created with id: $userId")

                // ========== PRODUCTOS ==========
                val products = listOf(
                    ProductEntity(name = "Alimento Perro 15kg", description = "Premium adulto", price = 35990.0,  category = "Alimento"),
                    ProductEntity(name = "Alimento Gato 10kg", description = "Premium adulto", price = 28990.0, category = "Alimento"),
                    ProductEntity(name = "Arena Gatos 10kg", description = "Aglomerante", price = 12990.0, category = "Higiene"),
                    ProductEntity(name = "Pelota Interactiva", description = "Goma resistente", price = 8990.0,  category = "Juguetes"),
                    ProductEntity(name = "Collar Ajustable", description = "Nylon resistente", price = 6990.0, category = "Accesorios"),
                    ProductEntity(name = "Cama Grande", description = "Acolchada 80x60cm", price = 45990.0, category = "Accesorios"),
                    ProductEntity(name = "Rascador Gatos", description = "Sisal 60cm", price = 25990.0,  category = "Accesorios"),
                    ProductEntity(name = "Shampoo Hipoalergénico", description = "500ml", price = 9990.0,  category = "Higiene")
                )

                products.forEach { product ->
                    val id = db.productDao().insert(product)
                    android.util.Log.d("AppDatabase", "✅ Product inserted: ${product.name} (id: $id)")
                }

                // ========== ANIMALES ==========
                val animals = listOf(
                    AnimalEntity(name = "Max", species = "Perro", breed = "Labrador", age = 3, description = "Perro cariñoso y juguetón, ideal para familias", isAdopted = false),
                    AnimalEntity(name = "Luna", species = "Gato", breed = "Siamés", age = 2, description = "Gata tranquila y afectuosa", isAdopted = false),
                    AnimalEntity(name = "Rocky", species = "Perro", breed = "Pastor Alemán", age = 5, description = "Perro guardián, entrenado y leal", isAdopted = false),
                    AnimalEntity(name = "Mimi", species = "Gato", breed = "Persa", age = 1, description = "Gatita juguetona y curiosa", isAdopted = false),
                    AnimalEntity(name = "Toby", species = "Perro", breed = "Beagle", age = 4, description = "Enérgico y amigable", isAdopted = false),
                    AnimalEntity(name = "Nala", species = "Gato", breed = "Común Europeo", age = 3, description = "Independiente pero cariñosa", isAdopted = false)
                )

                animals.forEach { animal ->
                    val id = db.animalDao().insert(animal)
                    android.util.Log.d("AppDatabase", "✅ Animal inserted: ${animal.name} (id: $id)")
                }

                android.util.Log.d("AppDatabase", "🎉 Database seeded successfully!")

            } catch (e: Exception) {
                android.util.Log.e("AppDatabase", "❌ Error seeding database", e)
            }
        }
    }
}