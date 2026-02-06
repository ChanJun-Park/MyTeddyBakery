package com.my.teddy.bakery.game.economy

import com.my.teddy.bakery.game.economy.models.BreadQuality

/**
 * 빵 가격 계산 시스템
 * 
 * 정확도를 빵 품질로 변환하고 최종 가격을 계산
 */
class BreadPriceCalculator {
    
    companion object {
        const val BASE_PRICE = 100
    }
    
    /**
     * 최종 빵 가격 계산
     * 
     * @param basePrice 기본 빵 가격
     * @param accuracy 플레이 정확도 (0.0 ~ 1.0)
     * @param breadsPerPlay 플레이당 빵 개수
     * @return 최종 획득 코인
     */
    fun calculatePrice(
        basePrice: Int,
        accuracy: Float,
        breadsPerPlay: Int
    ): Int {
        val quality = getQuality(accuracy)
        return (basePrice * quality.multiplier * breadsPerPlay).toInt()
    }
    
    /**
     * 정확도를 빵 품질로 변환
     * 
     * @param accuracy 정확도 (0.0 ~ 1.0)
     * @return 빵 품질 등급
     */
    fun getQuality(accuracy: Float): BreadQuality {
        return when {
            accuracy >= 0.95f -> BreadQuality.EXCELLENT
            accuracy >= 0.80f -> BreadQuality.GOOD
            accuracy >= 0.60f -> BreadQuality.NORMAL
            else -> BreadQuality.POOR
        }
    }
}
