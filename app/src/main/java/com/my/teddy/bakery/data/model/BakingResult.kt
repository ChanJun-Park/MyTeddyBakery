package com.my.teddy.bakery.data.model

/**
 * 빵 만들기 게임 플레이 결과 데이터
 */
data class BakingResult(
    val score: Int,
    val accuracy: Float,
    val correctCount: Int,
    val wrongCount: Int,
    val coinsEarned: Int
)
