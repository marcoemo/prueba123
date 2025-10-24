package com.example.amilimetros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amilimetros.data.local.animal.AnimalEntity
import com.example.amilimetros.data.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AnimalUiState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

class AnimalViewModel(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalUiState())
    val uiState: StateFlow<AnimalUiState> = _uiState

    // Lista de animales disponibles para adopción (Flow que se actualiza automáticamente)
    val availableAnimals = animalRepository.getAvailableAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lista de TODOS los animales (para admin)
    val allAnimals = animalRepository.getAllAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ========== ADOPTAR ANIMAL ==========
    fun adoptAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = animalRepository.adoptAnimal(animal)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isLoading = false,
                    successMsg = "¡${animal.name} ha sido adoptado! 🎉",
                    errorMsg = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMsg = "Error al adoptar el animal",
                    successMsg = null
                )
            }
        }
    }

    // ========== ADMIN: AGREGAR ANIMAL ==========
    fun addAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            val result = animalRepository.addAnimal(animal)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    successMsg = "Animal agregado correctamente",
                    errorMsg = null
                )
            } else {
                _uiState.value.copy(
                    errorMsg = "Error al agregar animal",
                    successMsg = null
                )
            }
        }
    }

    // ========== ADMIN: ACTUALIZAR ANIMAL ==========
    fun updateAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            val result = animalRepository.updateAnimal(animal)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    successMsg = "Animal actualizado correctamente",
                    errorMsg = null
                )
            } else {
                _uiState.value.copy(
                    errorMsg = "Error al actualizar animal",
                    successMsg = null
                )
            }
        }
    }

    // ========== ADMIN: ELIMINAR ANIMAL ==========
    fun deleteAnimal(animal: AnimalEntity) {
        viewModelScope.launch {
            val result = animalRepository.deleteAnimal(animal)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    successMsg = "Animal eliminado correctamente",
                    errorMsg = null
                )
            } else {
                _uiState.value.copy(
                    errorMsg = "Error al eliminar animal",
                    successMsg = null
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMsg = null, errorMsg = null)
    }
}