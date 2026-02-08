package com.my.teddy.bakery.game.baking

import com.my.teddy.bakery.game.baking.models.Note
import com.my.teddy.bakery.game.baking.models.NoteType

/**
 * 동작 생성 및 관리 시스템
 */
class NoteManager {
    
    /**
     * 게임용 동작 패턴 생성
     * 
     * MVP에서는 하드코딩된 패턴 사용
     * 추후 JSON 파일에서 로드하도록 확장 가능
     * 
     * @param duration 게임 길이 (초)
     * @param bpm 동작의 BPM (속도)
     * @return 생성된 동작 리스트
     */
    fun generateNotes(duration: Float, bpm: Int = 120): List<Note> {
        val notes = mutableListOf<Note>()
        val beatInterval = 60f / bpm // 120 BPM = 0.5초
        
        var id = 0
        var beatTime = 2f // 2초 후부터 시작 (준비 시간)
        
        // 뭉치기, 치대기, 두드리기 동작을 순환하며 생성
        val noteTypes = listOf(NoteType.KNEAD, NoteType.FOLD, NoteType.POUND)
        
        while (beatTime < duration - 1f) {
            // 순서대로 동작 타입을 순환
            val noteType = noteTypes[id % noteTypes.size]
            notes.add(Note(id++, noteType, beatTime))
            beatTime += beatInterval
        }
        
        return notes
    }
}
