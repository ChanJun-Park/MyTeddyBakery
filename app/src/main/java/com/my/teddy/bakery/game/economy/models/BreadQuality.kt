package com.my.teddy.bakery.game.economy.models

/**
 * 빵의 품질 등급
 * 
 * @param multiplier 가격 배율
 */
enum class BreadQuality(val multiplier: Float) {
    EXCELLENT(1.5f),  // 95-100%
    GOOD(1.2f),       // 80-94%
    NORMAL(1.0f),     // 60-79%
    POOR(0.5f)        // <60%
}
