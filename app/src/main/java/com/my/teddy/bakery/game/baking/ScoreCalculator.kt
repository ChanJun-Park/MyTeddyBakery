package com.my.teddy.bakery.game.baking

import com.my.teddy.bakery.game.baking.models.Judgement

/**
 * 스코어 및 정확도 계산 시스템
 */
class ScoreCalculator {
    
    /**
     * 정확도 계산
     * 
     * @param correctCount 올바른 인터랙션 개수
     * @param wrongCount 잘못된 인터랙션 개수
     * @return 정확도 (0.0 ~ 1.0)
     */
    fun calculateAccuracy(
        correctCount: Int,
        wrongCount: Int
    ): Float {
        val totalNotes = correctCount + wrongCount
        if (totalNotes == 0) return 0f
        
        return correctCount.toFloat() / totalNotes
    }
    
    /**
     * 총 점수 계산
     */
    fun calculateScore(
        correctCount: Int,
        wrongCount: Int
    ): Int {
        return (correctCount * Judgement.CORRECT.score) + 
               (wrongCount * Judgement.WRONG.score)
    }
}
