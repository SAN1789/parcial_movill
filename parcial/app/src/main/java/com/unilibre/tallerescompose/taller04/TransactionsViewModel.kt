package com.unilibre.tallerescompose.taller04

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UseCase to handle logic for adding a transaction with validation.
 */
class AgregarTransaccionUseCase @Inject constructor(
    private val dao: TransactionDao
) {
    suspend operator fun invoke(description: String, amount: Double): Result<Unit> {
        if (description.isBlank()) return Result.failure(Exception("Descripción vacía"))
        if (amount <= 0) return Result.failure(Exception("Monto debe ser > 0"))
        
        val transaction = TransactionEntity(description = description, amount = amount)
        dao.insertTransaction(transaction)
        return Result.success(Unit)
    }
}

/**
 * ViewModel for managing Transactions.
 */
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val dao: TransactionDao,
    private val agregarUseCase: AgregarTransaccionUseCase
) : ViewModel() {

    // stateIn transforms Flow to StateFlow, sharing it between observers
    val transactions = dao.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep active for 5s after last subscriber leaves
            initialValue = emptyList()
        )

    fun addTransaction(desc: String, amount: Double) {
        viewModelScope.launch {
            agregarUseCase(desc, amount)
        }
    }
}
