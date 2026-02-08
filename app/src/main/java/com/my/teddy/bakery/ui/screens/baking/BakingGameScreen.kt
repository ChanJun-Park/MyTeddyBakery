package com.my.teddy.bakery.ui.screens.baking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.my.teddy.bakery.ui.screens.baking.components.CurrentNoteDisplay
import com.my.teddy.bakery.ui.screens.baking.components.GameProgressBar
import com.my.teddy.bakery.ui.screens.baking.components.InteractionButtons
import com.my.teddy.bakery.ui.screens.baking.components.JudgementCountDisplay
import com.my.teddy.bakery.ui.screens.baking.components.JudgementDisplay
import com.my.teddy.bakery.ui.screens.baking.components.ScoreDisplay

/**
 * 빵 만들기 게임 화면
 * 
 * 순서대로 표시되는 동작의 타입에 맞게 인터랙션 수행
 */
@Composable
fun BakingGameScreen(
    onGameComplete: (accuracy: Float, coinsEarned: Int) -> Unit,
    viewModel: BakingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()
    val judgementEvent by viewModel.judgementResult.collectAsStateWithLifecycle()
    
    // 게임 시작
    LaunchedEffect(Unit) {
        viewModel.startGame()
    }
    
    // 게임 완료 체크
    LaunchedEffect(uiState.isGameComplete) {
        if (uiState.isGameComplete) {
            val total = uiState.correctCount + uiState.wrongCount
            val accuracy = if (total > 0) {
                uiState.correctCount.toFloat() / total
            } else {
                0f
            }
            
            onGameComplete(accuracy, uiState.coinsEarned)
        }
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 상단: 진행 상황 바
            GameProgressBar(
                currentTime = currentTime,
                totalTime = 25f,
                modifier = Modifier.padding(16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 점수 표시
            ScoreDisplay(
                score = uiState.score,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 중앙: 판정 표시
            JudgementDisplay(
                judgementResult = judgementEvent,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 현재 수행할 동작 표시
            CurrentNoteDisplay(
                notes = uiState.allNotes,
                currentIndex = uiState.currentNoteIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 인터랙션 버튼들 (뭉치기, 치대기, 두드리기)
            InteractionButtons(
                onKnead = { viewModel.onInteraction(com.my.teddy.bakery.game.baking.models.NoteType.KNEAD) },
                onFold = { viewModel.onInteraction(com.my.teddy.bakery.game.baking.models.NoteType.FOLD) },
                onPound = { viewModel.onInteraction(com.my.teddy.bakery.game.baking.models.NoteType.POUND) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 하단: 판정 카운트
            JudgementCountDisplay(
                correctCount = uiState.correctCount,
                wrongCount = uiState.wrongCount,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
