package com.my.teddy.bakery.game.rhythm

import com.my.teddy.bakery.game.rhythm.models.Note
import com.my.teddy.bakery.game.rhythm.models.RhythmState
import com.my.teddy.bakery.game.rhythm.models.Judgement
import com.my.teddy.bakery.game.rhythm.models.JudgementResult
import com.my.teddy.bakery.game.rhythm.models.NoteType

/**
 * 순서 기반 게임 엔진
 * 
 * 타이밍 대신 순서대로 노트를 처리하는 게임 로직
 */
class RhythmEngine(
    private val songDuration: Float = 25f
) {
    private var currentTime: Float = 0f
    
    private var allNotes: List<Note> = emptyList()
    private var currentNoteIndex: Int = 0
    
    private var currentState = RhythmState()
    
    /**
     * 게임 초기화
     */
    fun initialize(notes: List<Note>) {
        allNotes = notes
        currentNoteIndex = 0
        currentTime = 0f
        currentState = RhythmState(
            isPlaying = true, 
            allNotes = notes,
            currentNoteIndex = 0
        )
    }
    
    /**
     * 프레임 업데이트
     * 
     * @param deltaTime 이전 프레임과의 시간 차이 (초)
     * @return 현재 게임 상태
     */
    fun update(deltaTime: Float): RhythmState {
        if (!currentState.isPlaying) return currentState
        
        currentTime += deltaTime
        
        // 게임 종료 체크 (시간 또는 모든 노트 완료)
        if (currentTime >= songDuration || currentNoteIndex >= allNotes.size) {
            currentState = currentState.copy(isPlaying = false)
            return currentState
        }
        
        currentState = currentState.copy(
            currentTime = currentTime,
            currentNoteIndex = currentNoteIndex
        )
        
        return currentState
    }
    
    /**
     * 인터랙션 처리
     * 
     * @param inputType 플레이어가 수행한 인터랙션 타입
     * @param judgement 판정 결과
     */
    fun processInteraction(inputType: NoteType, judgement: Judgement) {
        val currentNote = getCurrentNote() ?: return
        
        // 올바른 인터랙션인 경우 다음 노트로 진행
        if (judgement == Judgement.CORRECT) {
            currentNoteIndex++
        }
        
        val judgementResult = JudgementResult(currentNote.id, judgement)
        
        currentState = when (judgement) {
            Judgement.CORRECT -> currentState.copy(
                correctCount = currentState.correctCount + 1,
                score = currentState.score + judgement.score,
                currentNoteIndex = currentNoteIndex,
                lastJudgementResult = judgementResult
            )
            Judgement.WRONG -> currentState.copy(
                wrongCount = currentState.wrongCount + 1,
                score = currentState.score + judgement.score,
                lastJudgementResult = judgementResult
            )
        }
    }
    
    /**
     * 현재 수행해야 할 노트 반환
     */
    fun getCurrentNote(): Note? {
        return if (currentNoteIndex < allNotes.size) {
            allNotes[currentNoteIndex]
        } else {
            null
        }
    }
    
    /**
     * 게임 리셋
     */
    fun reset() {
        currentTime = 0f
        currentNoteIndex = 0
        currentState = RhythmState()
    }
    
    /**
     * 현재 상태 반환
     */
    fun getCurrentState(): RhythmState = currentState
    
    /**
     * 현재 시간 반환
     */
    fun getCurrentTime(): Float = currentTime
}
