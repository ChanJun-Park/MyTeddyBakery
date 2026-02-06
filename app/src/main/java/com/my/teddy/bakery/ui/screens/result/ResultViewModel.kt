package com.my.teddy.bakery.ui.screens.result

import androidx.lifecycle.ViewModel
import com.my.teddy.bakery.game.economy.BreadPriceCalculator
import com.my.teddy.bakery.game.economy.models.BreadQuality
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 결과 화면의 UI 상태
 */
data class ResultUiState(
    val accuracy: Float = 0f,
    val coinsEarned: Int = 0,
    val breadQuality: BreadQuality = BreadQuality.NORMAL
)

/**
 * 결과 화면의 ViewModel
 */
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val breadPriceCalculator: BreadPriceCalculator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()
    
    /**
     * 결과 데이터 설정
     */
    fun setResult(accuracy: Float, coinsEarned: Int) {
        val quality = breadPriceCalculator.getQuality(accuracy)
        
        _uiState.value = ResultUiState(
            accuracy = accuracy,
            coinsEarned = coinsEarned,
            breadQuality = quality
        )
    }
}
