package com.my.teddy.bakery.game.rhythm

import com.my.teddy.bakery.game.rhythm.models.Judgement
import com.my.teddy.bakery.game.rhythm.models.NoteType
import kotlin.math.abs

/**
 * 순서 기반 인터랙션 판정 시스템
 * 
 * 타이밍 대신 올바른 노트 타입으로 인터랙션했는지만 체크
 */
class JudgementSystem {
    
    /**
     * 인터랙션 판정
     * 
     * @param inputType 플레이어가 수행한 인터랙션 타입
     * @param expectedType 수행해야 할 노트 타입
     * @return 판정 결과
     */
    fun judge(inputType: NoteType, expectedType: NoteType): Judgement {
        return if (inputType == expectedType) {
            Judgement.CORRECT
        } else {
            Judgement.WRONG
        }
    }
}
