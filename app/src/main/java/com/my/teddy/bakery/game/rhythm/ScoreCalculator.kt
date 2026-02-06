package com.my.teddy.bakery.game.rhythm

import com.my.teddy.bakery.game.rhythm.models.Judgement

/**
 * 스코어 및 정확도 계산 시스템
 */
class ScoreCalculator {
    
    /**
     * 정확도 계산
     * 
     * @param perfectCount Perfect 판정 개수
     * @param goodCount Good 판정 개수
     * @param missCount Miss 판정 개수
     * @return 정확도 (0.0 ~ 1.0)
     */
    fun calculateAccuracy(
        perfectCount: Int,
        goodCount: Int,
        missCount: Int
    ): Float {
        val totalNotes = perfectCount + goodCount + missCount
        if (totalNotes == 0) return 0f
        
        val earnedScore = (perfectCount * Judgement.PERFECT.score) + 
                         (goodCount * Judgement.GOOD.score)
        val maxScore = totalNotes * Judgement.PERFECT.score
        
        return earnedScore.toFloat() / maxScore
    }
    
    /**
     * 총 점수 계산
     */
    fun calculateScore(
        perfectCount: Int,
        goodCount: Int,
        missCount: Int
    ): Int {
        return (perfectCount * Judgement.PERFECT.score) + 
               (goodCount * Judgement.GOOD.score)
    }
}
