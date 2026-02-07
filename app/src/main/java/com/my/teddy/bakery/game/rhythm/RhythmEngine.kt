package com.my.teddy.bakery.game.rhythm

import com.my.teddy.bakery.game.rhythm.models.Note
import com.my.teddy.bakery.game.rhythm.models.RhythmState
import com.my.teddy.bakery.game.rhythm.models.Judgement
import com.my.teddy.bakery.game.rhythm.models.JudgementResult

/**
 * 리듬 게임의 핵심 엔진
 * 
 * 게임 시간 관리, 노트 업데이트, 상태 관리를 담당
 */
class RhythmEngine(
    private val bpm: Int = 120,
    private val songDuration: Float = 25f
) {
    private var currentTime: Float = 0f
    private val beatInterval = 60f / bpm // 0.5초 at 120 BPM
    
    private var allNotes: List<Note> = emptyList()
    private var activeNotes: MutableList<Note> = mutableListOf()
    private var processedNoteIds: MutableSet<Int> = mutableSetOf()
    
    private var currentState = RhythmState()
    
    /**
     * 게임 초기화
     */
    fun initialize(notes: List<Note>) {
        allNotes = notes.sortedBy { it.time }
        activeNotes.clear()
        processedNoteIds.clear()
        currentTime = 0f
        currentState = RhythmState(isPlaying = true, notes = notes)
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
        
        // 게임 종료 체크
        if (currentTime >= songDuration) {
            currentState = currentState.copy(isPlaying = false)
            return currentState
        }
        
        // 활성 노트 업데이트
        updateActiveNotes()
        
        // Miss 자동 판정 (노트가 판정선을 지나갔을 때)
        checkMissedNotes()
        
        currentState = currentState.copy(
            currentTime = currentTime,
            notes = activeNotes
        )
        
        return currentState
    }
    
    /**
     * Miss된 노트 자동 체크
     * 판정선을 지나간 노트들을 Miss 처리
     */
    private fun checkMissedNotes() {
        val missWindow = 0.2f // 200ms 허용 범위
        
        allNotes.filter { note ->
            !processedNoteIds.contains(note.id) &&
            (currentTime - note.time) > missWindow
        }.forEach { note ->
            processNote(note.id, Judgement.MISS)
        }
    }
    
    /**
     * 현재 시간 기준으로 활성 노트 업데이트
     */
    private fun updateActiveNotes() {
        // 화면에 보여줄 노트들 추가 (현재 시간 기준 앞뒤 3초)
        val visibleWindow = 3f
        
        activeNotes.clear()
        activeNotes.addAll(
            allNotes.filter { note ->
                note.time >= currentTime - 0.5f && 
                note.time <= currentTime + visibleWindow &&
                !processedNoteIds.contains(note.id)
            }
        )
    }
    
    /**
     * 노트 처리 (판정 후 호출)
     */
    fun processNote(noteId: Int, judgement: Judgement) {
        processedNoteIds.add(noteId)
        
        val newCombo = if (judgement != Judgement.MISS) {
            currentState.combo + 1
        } else {
            0
        }
        
        val judgementResult = JudgementResult(noteId, judgement)
        
        currentState = when (judgement) {
            Judgement.PERFECT -> currentState.copy(
                perfectCount = currentState.perfectCount + 1,
                score = currentState.score + judgement.score,
                combo = newCombo,
                lastJudgementResult = judgementResult
            )
            Judgement.GOOD -> currentState.copy(
                goodCount = currentState.goodCount + 1,
                score = currentState.score + judgement.score,
                combo = newCombo,
                lastJudgementResult = judgementResult
            )
            Judgement.MISS -> currentState.copy(
                missCount = currentState.missCount + 1,
                combo = 0,
                lastJudgementResult = judgementResult
            )
        }
    }
    
    /**
     * 게임 리셋
     */
    fun reset() {
        currentTime = 0f
        activeNotes.clear()
        processedNoteIds.clear()
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
