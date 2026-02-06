package com.my.teddy.bakery.ui.screens.rhythm

import android.util.Log
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
 * 판정 이벤트 (타임스탬프 포함)
 * 같은 판정이 연속으로 나와도 애니메이션이 재생되도록 함
 */
data class JudgementEvent(
    val judgement: Judgement,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 리듬 게임 화면의 UI 상태
 * 
 * currentTime은 별도 StateFlow로 관리하여 불필요한 객체 생성 방지
 */
data class RhythmUiState(
    val notes: List<Note> = emptyList(),
    val score: Int = 0,
    val combo: Int = 0,
    val judgementEvent: JudgementEvent? = null,
    val isPlaying: Boolean = false,
    val isGameComplete: Boolean = false,
    val perfectCount: Int = 0,
    val goodCount: Int = 0,
    val missCount: Int = 0,
    val coinsEarned: Int = 0,
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
    
    // 게임 상태 (변경이 적음)
    private val _uiState = MutableStateFlow(RhythmUiState())
    val uiState: StateFlow<RhythmUiState> = _uiState.asStateFlow()
    
    // 현재 시간 (매 프레임 변경, 별도 관리)
    private val _currentTime = MutableStateFlow(0f)
    val currentTime: StateFlow<Float> = _currentTime.asStateFlow()
    
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
     * 
     * currentTime은 매 프레임 업데이트하지만,
     * uiState는 실제 변경이 있을 때만 업데이트
     */
    private suspend fun runGameLoop() {
        var lastTime = System.currentTimeMillis()
        
        while (_uiState.value.isPlaying) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - lastTime) / 1000f
            lastTime = currentTime
            
            // 엔진 업데이트
            val state = rhythmEngine.update(deltaTime)
            
            // 시간만 업데이트 (매 프레임, 빠름)
            _currentTime.value = state.currentTime
            
            // 게임 상태는 변경이 있을 때만 업데이트 (최적화)
            val hasStateChange = 
                state.notes != _uiState.value.notes ||
                state.score != _uiState.value.score ||
                state.combo != _uiState.value.combo ||
                state.perfectCount != _uiState.value.perfectCount ||
                state.goodCount != _uiState.value.goodCount ||
                state.missCount != _uiState.value.missCount ||
                state.lastJudgement != null ||  // 판정이 발생하면 항상 업데이트
                state.isPlaying != _uiState.value.isPlaying
            
            if (hasStateChange) {
                _uiState.update {
                    it.copy(
                        notes = state.notes,
                        score = state.score,
                        combo = state.combo,
                        perfectCount = state.perfectCount,
                        goodCount = state.goodCount,
                        missCount = state.missCount,
                        judgementEvent = state.lastJudgement?.let { judgement ->
                            JudgementEvent(judgement)
                        },
                        isPlaying = state.isPlaying
                    )
                }
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
        
        Log.d("RhythmViewModel", "노트 입력: id=${note.id}, 현재=${currentTime}ms, 노트=${noteTime}ms, 차이=${kotlin.math.abs(currentTime - noteTime)}ms")
        
        val judgement = judgementSystem.judge(currentTime, noteTime)
        Log.d("RhythmViewModel", "판정 결과: $judgement")
        
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
            it.copy(isGameComplete = true, coinsEarned = coinsEarned)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        rhythmEngine.reset()
    }
}
