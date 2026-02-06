package com.my.teddy.bakery.ui.screens.rhythm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.teddy.bakery.data.model.RhythmResult
import com.my.teddy.bakery.data.repository.GameDataRepository
import com.my.teddy.bakery.game.economy.BreadPriceCalculator
import com.my.teddy.bakery.game.rhythm.JudgementSystem
import com.my.teddy.bakery.game.rhythm.NoteManager
import com.my.teddy.bakery.game.rhythm.RhythmEngine
import com.my.teddy.bakery.game.rhythm.ScoreCalculator
import com.my.teddy.bakery.game.rhythm.models.Judgement
import com.my.teddy.bakery.game.rhythm.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 리듬 게임 화면의 UI 상태
 */
data class RhythmUiState(
    val notes: List<Note> = emptyList(),
    val currentTime: Float = 0f,
    val score: Int = 0,
    val combo: Int = 0,
    val judgement: Judgement? = null,
    val isPlaying: Boolean = false,
    val isGameComplete: Boolean = false,
    val perfectCount: Int = 0,
    val goodCount: Int = 0,
    val missCount: Int = 0
)

/**
 * 리듬 게임 화면의 ViewModel
 */
@HiltViewModel
class RhythmViewModel @Inject constructor(
    private val repository: GameDataRepository,
    private val rhythmEngine: RhythmEngine,
    private val judgementSystem: JudgementSystem,
    private val scoreCalculator: ScoreCalculator,
    private val noteManager: NoteManager,
    private val breadPriceCalculator: BreadPriceCalculator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RhythmUiState())
    val uiState: StateFlow<RhythmUiState> = _uiState.asStateFlow()
    
    /**
     * 게임 시작
     */
    fun startGame() {
        viewModelScope.launch {
            // 노트 생성
            val notes = noteManager.generateNotes(duration = 25f, bpm = 120)
            rhythmEngine.initialize(notes)
            
            _uiState.update {
                it.copy(
                    notes = notes,
                    isPlaying = true,
                    isGameComplete = false
                )
            }
            
            // 게임 루프 시작
            runGameLoop()
        }
    }
    
    /**
     * 게임 루프 실행
     */
    private suspend fun runGameLoop() {
        var lastTime = System.currentTimeMillis()
        
        while (_uiState.value.isPlaying) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - lastTime) / 1000f
            lastTime = currentTime
            
            // 엔진 업데이트
            val state = rhythmEngine.update(deltaTime)
            
            _uiState.update {
                it.copy(
                    currentTime = state.currentTime,
                    notes = state.notes,
                    score = state.score,
                    combo = state.combo,
                    perfectCount = state.perfectCount,
                    goodCount = state.goodCount,
                    missCount = state.missCount,
                    judgement = state.lastJudgement,
                    isPlaying = state.isPlaying
                )
            }
            
            // 게임 종료 체크
            if (!state.isPlaying) {
                onGameComplete()
                break
            }
            
            delay(16) // 약 60 FPS
        }
    }
    
    /**
     * 노트 입력 처리
     */
    fun onNoteTap(note: Note) {
        val currentTime = rhythmEngine.getCurrentTime() * 1000 // 초 -> ms
        val noteTime = note.time * 1000
        
        val judgement = judgementSystem.judge(currentTime, noteTime)
        rhythmEngine.processNote(note.id, judgement)
    }
    
    /**
     * 게임 완료 처리
     */
    private suspend fun onGameComplete() {
        val state = _uiState.value
        
        // 정확도 계산
        val accuracy = scoreCalculator.calculateAccuracy(
            perfectCount = state.perfectCount,
            goodCount = state.goodCount,
            missCount = state.missCount
        )
        
        // 게임 상태에서 업그레이드 정보 가져오기
        val gameState = repository.gameState.first()
        val priceMultiplier = 1.0f + (gameState.recipeLevel * 0.1f)
        val basePrice = (BreadPriceCalculator.BASE_PRICE * priceMultiplier).toInt()
        val breadsPerPlay = 1 + gameState.ovenSizeLevel
        
        // 코인 계산
        val coinsEarned = breadPriceCalculator.calculatePrice(
            basePrice = basePrice,
            accuracy = accuracy,
            breadsPerPlay = breadsPerPlay
        )
        
        // 결과 저장
        val result = RhythmResult(
            score = state.score,
            accuracy = accuracy,
            perfectCount = state.perfectCount,
            goodCount = state.goodCount,
            missCount = state.missCount,
            maxCombo = state.combo,
            coinsEarned = coinsEarned
        )
        
        repository.saveRhythmResult(result)
        
        _uiState.update {
            it.copy(isGameComplete = true)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        rhythmEngine.reset()
    }
}
