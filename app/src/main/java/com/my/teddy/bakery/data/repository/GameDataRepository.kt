package com.my.teddy.bakery.data.repository

import com.my.teddy.bakery.data.local.GameDataStore
import com.my.teddy.bakery.data.model.GameState
import com.my.teddy.bakery.data.model.BakingResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 게임 데이터 Repository
 * 
 * ViewModel과 데이터 소스 사이의 중재자 역할
 * 단일 진실 공급원(Single Source of Truth) 제공
 */
@Singleton
class GameDataRepository @Inject constructor(
    private val dataStore: GameDataStore
) {
    
    /**
     * 게임 상태 Flow
     */
    val gameState: Flow<GameState> = dataStore.gameStateFlow
    
    /**
     * 코인 추가
     * 
     * @param amount 추가할 코인 양
     */
    suspend fun addCoins(amount: Int) {
        dataStore.updateCoins(amount)
    }
    
    /**
     * 코인 차감 (업그레이드 구매 시)
     * 
     * @param amount 차감할 코인 양
     */
    suspend fun spendCoins(amount: Int) {
        dataStore.updateCoins(-amount)
    }
    
    /**
     * 업그레이드 구매
     * 
     * @param upgradeId 업그레이드 ID
     * @param cost 업그레이드 비용
     * @param currentLevel 현재 레벨
     */
    suspend fun purchaseUpgrade(upgradeId: String, cost: Int, currentLevel: Int) {
        // 코인 차감
        spendCoins(cost)
        // 레벨 업
        dataStore.updateUpgradeLevel(upgradeId, currentLevel + 1)
    }
    
    /**
     * 빵 만들기 게임 결과 저장 및 코인 추가
     * 
     * @param result 게임 결과
     */
    suspend fun saveBakingResult(result: BakingResult) {
        addCoins(result.coinsEarned)
    }
    
    /**
     * 게임 데이터 초기화 (디버그용)
     */
    suspend fun resetGameData() {
        dataStore.clearAll()
    }
}
