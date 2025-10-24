package com.example.amilimetros.data.local.adoption

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adoption_forms")
data class AdoptionFormEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val animalId: Long,
    val animalName: String,
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val reason: String, // Motivo de adopción
    val hasBalconyNets: Boolean, // ¿Tiene mallas en ventanas/balcón?
    val livesInApartment: Boolean, // ¿Vive en departamento?
    val photoUri: String?, // URI de la foto tomada/seleccionada
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "Pendiente" // Pendiente, Aprobado, Rechazado
)