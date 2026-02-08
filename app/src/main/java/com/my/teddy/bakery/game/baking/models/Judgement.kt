package com.my.teddy.bakery.game.baking.models

/**
 * 인터랙션 판정 결과
 * 
 * @param score 해당 판정의 점수
 */
enum class Judgement(val score: Int) {
    CORRECT(100),   // 올바른 인터랙션
    WRONG(0)        // 잘못된 인터랙션
}

/**
 * 판정 결과 (동작 정보 포함)
 * 
 * @param noteId 판정된 동작의 ID
 * @param judgement 판정 결과
 */
data class JudgementResult(
    val noteId: Int,
    val judgement: Judgement
)
