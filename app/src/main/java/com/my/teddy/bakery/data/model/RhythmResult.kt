package com.my.teddy.bakery.data.model

/**
 * 리듬 게임 플레이 결과 데이터
 */
data class RhythmResult(
    val score: Int,
    val accuracy: Float,
    val perfectCount: Int,
    val goodCount: Int,
    val missCount: Int,
    val maxCombo: Int,
    val coinsEarned: Int
)
