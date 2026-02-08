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
import com.my.teddy.bakery.game.rhythm.models.JudgementResult
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
 *
 * currentTime은 별도 StateFlow로 관리하여 불필요한 객체 생성 방지
 */
data class RhythmUiState(
    val allNotes: List<Note> = emptyList(),
    val currentNoteIndex: Int = 0,
    val score: Int = 0,
    val isPlaying: Boolean = false,
    val isGameComplete: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
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

    private val _judgementResult = MutableStateFlow<JudgementResult?>(null)
    val judgementResult: StateFlow<JudgementResult?> = _judgementResult.asStateFlow()

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
                    allNotes = notes,
                    currentNoteIndex = 0,
                    isPlaying = true,
                    isGameComplete = false,
                    correctCount = 0,
                    wrongCount = 0,
                    score = 0
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
                state.currentNoteIndex != _uiState.value.currentNoteIndex ||
                state.score != _uiState.value.score ||
                state.correctCount != _uiState.value.correctCount ||
                state.wrongCount != _uiState.value.wrongCount ||
                state.lastJudgementResult != null ||  // 판정이 발생하면 항상 업데이트
                state.isPlaying != _uiState.value.isPlaying
            
            if (hasStateChange) {
                _uiState.update {
                    it.copy(
                        currentNoteIndex = state.currentNoteIndex,
                        score = state.score,
                        correctCount = state.correctCount,
                        wrongCount = state.wrongCount,
                        isPlaying = state.isPlaying
                    )
                }

                state.lastJudgementResult?.let { result ->
                    _judgementResult.update {
                        result
                    }
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
     * 인터랙션 처리
     * 
     * @param inputType 플레이어가 수행한 인터랙션 타입
     */
    fun onInteraction(inputType: com.my.teddy.bakery.game.rhythm.models.NoteType) {
        val currentNote = rhythmEngine.getCurrentNote()
        if (currentNote == null) {
            Log.d("RhythmViewModel", "현재 수행할 노트가 없음")
            return
        }
        
        Log.d("RhythmViewModel", "인터랙션: 입력=${inputType}, 기대=${currentNote.type}, 노트ID=${currentNote.id}")
        
        val judgement = judgementSystem.judge(inputType, currentNote.type)
        Log.d("RhythmViewModel", "판정 결과: noteId=${currentNote.id}, $judgement")
        
        rhythmEngine.processInteraction(inputType, judgement)
    }

    /**
     * 게임 완료 처리
     */
    private suspend fun onGameComplete() {
        val state = _uiState.value

        // 정확도 계산
        val accuracy = scoreCalculator.calculateAccuracy(
            correctCount = state.correctCount,
            wrongCount = state.wrongCount
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
            perfectCount = state.correctCount,
            goodCount = 0,
            missCount = state.wrongCount,
            maxCombo = 0,
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
