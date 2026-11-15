package com.example.biztrack.budget

//now loads the saved budget on startup and persists updates to Room-David
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.biztrack.FinancialRepository

class BudgetViewModel : ViewModel() {

    private val _totalBudget = mutableStateOf(0.0)
    val totalBudget: State<Double> = _totalBudget

    private val _totalSpent = mutableStateOf(0.0)
    val totalSpent: State<Double> = _totalSpent

    init {
        _totalBudget.value = FinancialRepository.getBudget()
        refreshSpending()
    }

    fun refreshSpending() {
        _totalSpent.value = FinancialRepository.getTotalExpenses()
    }

    val remainingBalance: Double
        get() = (_totalBudget.value - _totalSpent.value)
            .coerceAtLeast(0.0)

    val spendingProgress: Float
        get() = if (_totalBudget.value > 0) {
            (_totalSpent.value / _totalBudget.value)
                .toFloat()
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    fun setBudget(amount: Double) {
        if (amount >= 0) {
            _totalBudget.value = amount
            FinancialRepository.saveBudget(amount)
        }
    }
}