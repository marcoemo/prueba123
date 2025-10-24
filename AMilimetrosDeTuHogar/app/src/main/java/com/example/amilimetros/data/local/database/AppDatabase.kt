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
    version = 1, // ✅ Versión inicial limpia
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
                    .fallbackToDestructiveMigration() // ✅ Para desarrollo
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
                // Admin
                db.userDao().insert(
                    UserEntity(
                        name = "Admin",
                        email = "admin@amilimetros.com",
                        phone = "+56911111111",
                        password = "Admin123!_",
                        isAdmin = true
                    )
                )

                // Usuario demo
                db.userDao().insert(
                    UserEntity(
                        name = "Usuario Demo",
                        email = "user@demo.com",
                        phone = "+56922222222",
                        password = "User123!_",
                        isAdmin = false
                    )
                )

                // Productos
                listOf(
                    ProductEntity(name = "Alimento Perro 15kg", description = "Premium adulto", price = 35990.0, stock = 50, category = "Alimento"),
                    ProductEntity(name = "Alimento Gato 10kg", description = "Premium adulto", price = 28990.0, stock = 40, category = "Alimento"),
                    ProductEntity(name = "Arena Gatos 10kg", description = "Aglomerante", price = 12990.0, stock = 60, category = "Higiene"),
                    ProductEntity(name = "Pelota Interactiva", description = "Goma resistente", price = 8990.0, stock = 100, category = "Juguetes"),
                    ProductEntity(name = "Collar Ajustable", description = "Nylon resistente", price = 6990.0, stock = 75, category = "Accesorios"),
                    ProductEntity(name = "Cama Grande", description = "Acolchada 80x60cm", price = 45990.0, stock = 30, category = "Accesorios"),
                    ProductEntity(name = "Rascador Gatos", description = "Sisal 60cm", price = 25990.0, stock = 20, category = "Accesorios"),
                    ProductEntity(name = "Shampoo Hipoalergénico", description = "500ml", price = 9990.0, stock = 45, category = "Higiene")
                ).forEach { db.productDao().insert(it) }

                // Animales
                listOf(
                    AnimalEntity(name = "Max", species = "Perro", breed = "Labrador", age = 3, description = "Perro cariñoso y juguetón, ideal para familias", isAdopted = false),
                    AnimalEntity(name = "Luna", species = "Gato", breed = "Siamés", age = 2, description = "Gata tranquila y afectuosa", isAdopted = false),
                    AnimalEntity(name = "Rocky", species = "Perro", breed = "Pastor Alemán", age = 5, description = "Perro guardián, entrenado y leal", isAdopted = false),
                    AnimalEntity(name = "Mimi", species = "Gato", breed = "Persa", age = 1, description = "Gatita juguetona y curiosa", isAdopted = false),
                    AnimalEntity(name = "Toby", species = "Perro", breed = "Beagle", age = 4, description = "Enérgico y amigable", isAdopted = false),
                    AnimalEntity(name = "Nala", species = "Gato", breed = "Común Europeo", age = 3, description = "Independiente pero cariñosa", isAdopted = false)
                ).forEach { db.animalDao().insert(it) }

            } catch (e: Exception) {
                android.util.Log.e("AppDatabase", "Error seeding database", e)
            }
        }
    }
}