package com.my.teddy.bakery.ui.screens.bakery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.teddy.bakery.data.repository.GameDataRepository
import com.my.teddy.bakery.game.economy.BreadPriceCalculator
import com.my.teddy.bakery.game.economy.UpgradeManager
import com.my.teddy.bakery.game.economy.models.UpgradeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 베이커리 화면의 UI 상태
 */
data class BakeryUiState(
    val coins: Int = 0,
    val upgrades: List<UpgradeState> = emptyList(),
    val basePrice: Int = BreadPriceCalculator.BASE_PRICE,
    val breadsPerPlay: Int = 1,
    val isLoading: Boolean = true
)

/**
 * 베이커리 화면의 ViewModel
 */
@HiltViewModel
class BakeryViewModel @Inject constructor(
    private val repository: GameDataRepository,
    private val upgradeManager: UpgradeManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BakeryUiState())
    val uiState: StateFlow<BakeryUiState> = _uiState.asStateFlow()
    
    init {
        loadGameState()
    }
    
    /**
     * 게임 상태 로드
     */
    private fun loadGameState() {
        viewModelScope.launch {
            repository.gameState.collect { gameState ->
                // 업그레이드 상태 초기화
                val upgrades = upgradeManager.createInitialUpgrades().map { upgrade ->
                    val level = when (upgrade.upgrade.id) {
                        "recipe" -> gameState.recipeLevel
                        "oven_quality" -> gameState.ovenQualityLevel
                        "oven_size" -> gameState.ovenSizeLevel
                        else -> 0
                    }
                    
                    // 현재 비용 계산
                    var cost = upgrade.upgrade.baseCost
                    repeat(level) {
                        cost = upgradeManager.calculateNextCost(cost)
                    }
                    
                    upgrade.copy(level = level, currentCost = cost)
                }
                
                // 업그레이드 효과 적용
                val priceMultiplier = 1.0f + (gameState.recipeLevel * 0.1f)
                val basePrice = (BreadPriceCalculator.BASE_PRICE * priceMultiplier).toInt()
                val breadsPerPlay = 1 + gameState.ovenSizeLevel
                
                _uiState.update {
                    it.copy(
                        coins = gameState.coins,
                        upgrades = upgrades,
                        basePrice = basePrice,
                        breadsPerPlay = breadsPerPlay,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    /**
     * 업그레이드 구매
     */
    fun purchaseUpgrade(upgradeState: UpgradeState) {
        viewModelScope.launch {
            if (upgradeManager.canAfford(_uiState.value.coins, upgradeState)) {
                repository.purchaseUpgrade(
                    upgradeId = upgradeState.upgrade.id,
                    cost = upgradeState.currentCost,
                    currentLevel = upgradeState.level
                )
            }
        }
    }
}
