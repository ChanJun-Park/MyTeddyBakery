package com.my.teddy.bakery.game.baking.models

/**
 * 빵 만들기 게임의 현재 상태를 나타내는 데이터
 */
data class BakingState(
    val currentTime: Float = 0f,
    val isPlaying: Boolean = false,
    val allNotes: List<Note> = emptyList(),          // 전체 동작 리스트
    val currentNoteIndex: Int = 0,                    // 현재 수행해야 할 동작 인덱스
    val score: Int = 0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lastJudgementResult: JudgementResult? = null
)
