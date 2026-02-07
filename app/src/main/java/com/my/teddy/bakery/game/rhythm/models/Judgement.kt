package com.my.teddy.bakery.game.rhythm.models

/**
 * 리듬 게임 판정 결과
 * 
 * @param score 해당 판정의 점수
 */
enum class Judgement(val score: Int) {
    PERFECT(100),
    GOOD(50),
    MISS(0)
}

/**
 * 판정 결과 (노트 정보 포함)
 * 
 * @param noteId 판정된 노트의 ID
 * @param judgement 판정 결과
 */
data class JudgementResult(
    val noteId: Int,
    val judgement: Judgement
)
