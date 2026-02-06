package com.my.teddy.bakery.game.economy.models

/**
 * 업그레이드 효과를 나타내는 sealed class
 */
sealed class UpgradeEffect {
    /**
     * 빵 가격 증가 효과
     * @param multiplier 가격 배율 (1.0 = 100%)
     */
    data class PriceIncrease(val multiplier: Float) : UpgradeEffect()
    
    /**
     * 판정 범위 증가 효과
     * @param windowMs Perfect 판정 범위 (밀리초)
     */
    data class JudgementWindowIncrease(val windowMs: Float) : UpgradeEffect()
    
    /**
     * 플레이당 빵 개수 증가 효과
     * @param count 빵 개수
     */
    data class BreadsPerPlayIncrease(val count: Int) : UpgradeEffect()
}
