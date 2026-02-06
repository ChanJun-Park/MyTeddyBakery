package com.my.teddy.bakery.game.economy

import com.my.teddy.bakery.game.economy.models.Upgrade
import com.my.teddy.bakery.game.economy.models.UpgradeEffect
import com.my.teddy.bakery.game.economy.models.UpgradeState

/**
 * 업그레이드 관리 시스템
 * 
 * 업그레이드 구매, 비용 계산, 효과 적용을 담당
 */
class UpgradeManager {
    
    companion object {
        private const val COST_MULTIPLIER = 1.5f // 레벨당 비용 증가율
    }
    
    /**
     * 업그레이드 구매 가능 여부 확인
     * 
     * @param coins 현재 보유 코인
     * @param upgradeState 업그레이드 상태
     * @return 구매 가능 여부
     */
    fun canAfford(coins: Int, upgradeState: UpgradeState): Boolean {
        return coins >= upgradeState.currentCost
    }
    
    /**
     * 다음 레벨 업그레이드 비용 계산
     * 
     * @param currentCost 현재 비용
     * @return 다음 레벨 비용
     */
    fun calculateNextCost(currentCost: Int): Int {
        return (currentCost * COST_MULTIPLIER).toInt()
    }
    
    /**
     * 업그레이드 효과 계산
     * 
     * @param upgrade 업그레이드 종류
     * @param level 업그레이드 레벨
     * @return 적용될 효과
     */
    fun applyUpgrade(upgrade: Upgrade, level: Int): UpgradeEffect {
        return when (upgrade) {
            is Upgrade.RecipeUpgrade -> {
                // 레벨당 10% 가격 증가
                UpgradeEffect.PriceIncrease(1.0f + (level * 0.1f))
            }
            is Upgrade.BetterOven -> {
                // 레벨당 10ms 판정 범위 증가
                UpgradeEffect.JudgementWindowIncrease(80f + (level * 10f))
            }
            is Upgrade.BiggerOven -> {
                // 레벨당 빵 1개 증가
                UpgradeEffect.BreadsPerPlayIncrease(1 + level)
            }
        }
    }
    
    /**
     * 모든 업그레이드의 초기 상태 생성
     */
    fun createInitialUpgrades(): List<UpgradeState> {
        return listOf(
            UpgradeState(upgrade = Upgrade.RecipeUpgrade),
            UpgradeState(upgrade = Upgrade.BetterOven),
            UpgradeState(upgrade = Upgrade.BiggerOven)
        )
    }
}
