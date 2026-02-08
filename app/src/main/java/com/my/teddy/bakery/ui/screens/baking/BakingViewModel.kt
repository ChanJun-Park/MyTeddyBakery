package com.my.teddy.bakery.ui.screens.baking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.teddy.bakery.data.model.BakingResult
import com.my.teddy.bakery.data.repository.GameDataRepository
import com.my.teddy.bakery.game.economy.BreadPriceCalculator
import com.my.teddy.bakery.game.baking.JudgementSystem
import com.my.teddy.bakery.game.baking.NoteManager
import com.my.teddy.bakery.game.baking.BakingEngine
import com.my.teddy.bakery.game.baking.ScoreCalculator
import com.my.teddy.bakery.game.baking.models.JudgementResult
import com.my.teddy.bakery.game.baking.models.Note
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
 * 빵 만들기 게임 화면의 UI 상태
 *
 * currentTime은 별도 StateFlow로 관리하여 불필요한 객체 생성 방지
 */
data class BakingUiState(
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
 * 빵 만들기 게임 화면의 ViewModel
 */
@HiltViewModel
class BakingViewModel @Inject constructor(
    private val repository: GameDataRepository,
    private val bakingEngine: BakingEngine,
    private val judgementSystem: JudgementSystem,
    private val scoreCalculator: ScoreCalculator,
    private val noteManager: NoteManager,
    private val breadPriceCalculator: BreadPriceCalculator
) : ViewModel() {

    // 게임 상태 (변경이 적음)
    private val _uiState = MutableStateFlow(BakingUiState())
    val uiState: StateFlow<BakingUiState> = _uiState.asStateFlow()

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
            // 동작 생성
            val notes = noteManager.generateNotes(duration = 25f, bpm = 120)
            bakingEngine.initialize(notes)

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
            val state = bakingEngine.update(deltaTime)

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
    fun onInteraction(inputType: com.my.teddy.bakery.game.baking.models.NoteType) {
        val currentNote = bakingEngine.getCurrentNote()
        if (currentNote == null) {
            Log.d("BakingViewModel", "현재 수행할 동작이 없음")
            return
        }
        
        Log.d("BakingViewModel", "인터랙션: 입력=${inputType}, 기대=${currentNote.type}, 동작ID=${currentNote.id}")
        
        val judgement = judgementSystem.judge(inputType, currentNote.type)
        Log.d("BakingViewModel", "판정 결과: noteId=${currentNote.id}, $judgement")
        
        bakingEngine.processInteraction(inputType, judgement)
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
        val result = BakingResult(
            score = state.score,
            accuracy = accuracy,
            correctCount = state.correctCount,
            wrongCount = state.wrongCount,
            coinsEarned = coinsEarned
        )

        repository.saveBakingResult(result)

        _uiState.update {
            it.copy(isGameComplete = true, coinsEarned = coinsEarned)
        }
    }

    override fun onCleared() {
        super.onCleared()
        bakingEngine.reset()
    }
}
