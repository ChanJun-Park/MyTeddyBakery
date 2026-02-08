package com.my.teddy.bakery.game.rhythm.models

/**
 * 리듬 게임의 현재 상태를 나타내는 데이터
 */
data class RhythmState(
    val currentTime: Float = 0f,
    val isPlaying: Boolean = false,
    val allNotes: List<Note> = emptyList(),          // 전체 노트 리스트
    val currentNoteIndex: Int = 0,                    // 현재 수행해야 할 노트 인덱스
    val score: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lastJudgementResult: JudgementResult? = null
)
