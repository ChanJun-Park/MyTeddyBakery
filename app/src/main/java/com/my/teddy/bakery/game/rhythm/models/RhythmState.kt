package com.my.teddy.bakery.game.rhythm.models

/**
 * 리듬 게임의 현재 상태를 나타내는 데이터
 */
data class RhythmState(
    val currentTime: Float = 0f,
    val isPlaying: Boolean = false,
    val notes: List<Note> = emptyList(),
    val score: Int = 0,
    val combo: Int = 0,
    val perfectCount: Int = 0,
    val goodCount: Int = 0,
    val missCount: Int = 0,
    val lastJudgementResult: JudgementResult? = null
)
