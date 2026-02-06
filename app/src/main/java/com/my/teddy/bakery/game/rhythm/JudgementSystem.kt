package com.my.teddy.bakery.game.rhythm

import com.my.teddy.bakery.game.rhythm.models.Judgement
import kotlin.math.abs

/**
 * 리듬 게임의 판정 시스템
 * 
 * 노트 타이밍과 입력 타이밍의 차이를 계산하여 판정 결과를 반환
 */
class JudgementSystem(
    private var perfectWindow: Float = PERFECT_WINDOW_DEFAULT,
    private var goodWindow: Float = GOOD_WINDOW_DEFAULT
) {
    
    companion object {
        const val PERFECT_WINDOW_DEFAULT = 80f // ms
        const val GOOD_WINDOW_DEFAULT = 160f // ms
    }
    
    /**
     * 입력 타이밍을 판정
     * 
     * @param tapTime 플레이어의 입력 시간 (ms)
     * @param noteTime 노트의 타이밍 (ms)
     * @return 판정 결과
     */
    fun judge(tapTime: Float, noteTime: Float): Judgement {
        val diff = abs(tapTime - noteTime)
        return when {
            diff <= perfectWindow -> Judgement.PERFECT
            diff <= goodWindow -> Judgement.GOOD
            else -> Judgement.MISS
        }
    }
    
    /**
     * Perfect 판정 범위 업데이트 (업그레이드용)
     */
    fun updatePerfectWindow(newWindow: Float) {
        perfectWindow = newWindow
    }
    
    /**
     * 현재 Perfect 판정 범위 반환
     */
    fun getPerfectWindow(): Float = perfectWindow
}
