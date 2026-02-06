package com.my.teddy.bakery.game.rhythm

import com.my.teddy.bakery.game.rhythm.models.Note
import com.my.teddy.bakery.game.rhythm.models.NoteType

/**
 * 노트 생성 및 관리 시스템
 */
class NoteManager {
    
    /**
     * 게임용 노트 패턴 생성
     * 
     * MVP에서는 하드코딩된 패턴 사용
     * 추후 JSON 파일에서 로드하도록 확장 가능
     * 
     * @param duration 게임 길이 (초)
     * @param bpm 곡의 BPM
     * @return 생성된 노트 리스트
     */
    fun generateNotes(duration: Float, bpm: Int = 120): List<Note> {
        val notes = mutableListOf<Note>()
        val beatInterval = 60f / bpm // 120 BPM = 0.5초
        
        var id = 0
        var beatTime = 2f // 2초 후부터 시작 (준비 시간)
        
        // TAP 노트만 생성 (MVP용)
        while (beatTime < duration - 1f) {
            notes.add(Note(id++, NoteType.TAP, beatTime))
            beatTime += beatInterval
        }
        
        return notes
    }
}
