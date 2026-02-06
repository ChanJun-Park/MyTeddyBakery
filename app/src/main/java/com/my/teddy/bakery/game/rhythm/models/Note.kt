package com.my.teddy.bakery.game.rhythm.models

/**
 * 리듬 게임의 노트 타입
 */
enum class NoteType {
    TAP,        // 탭 노트
    HOLD,       // 홀드 노트
    SWIPE_LEFT, // 왼쪽 스와이프
    SWIPE_RIGHT // 오른쪽 스와이프
}

/**
 * 리듬 게임 노트 데이터
 * 
 * @param id 노트 고유 ID
 * @param type 노트 타입
 * @param time 노트가 판정선에 도달하는 시간 (초 단위)
 * @param lane 레인 번호 (MVP에서는 0만 사용, 향후 확장용)
 */
data class Note(
    val id: Int,
    val type: NoteType,
    val time: Float,
    val lane: Int = 0
)
