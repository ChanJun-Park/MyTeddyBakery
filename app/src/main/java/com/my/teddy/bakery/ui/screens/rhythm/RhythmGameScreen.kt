package com.my.teddy.bakery.ui.screens.rhythm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.my.teddy.bakery.ui.screens.rhythm.components.*

/**
 * 리듬 게임 화면
 * 
 * 노트를 표시하고 플레이어 입력을 받아 판정 처리
 */
@Composable
fun RhythmGameScreen(
    onGameComplete: (accuracy: Float, coinsEarned: Int) -> Unit,
    viewModel: RhythmViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()
    
    // 게임 시작
    LaunchedEffect(Unit) {
        viewModel.startGame()
    }
    
    // 게임 완료 체크
    LaunchedEffect(uiState.isGameComplete) {
        if (uiState.isGameComplete) {
            // 정확도 계산
            val total = uiState.perfectCount + uiState.goodCount + uiState.missCount
            val accuracy = if (total > 0) {
                ((uiState.perfectCount * 100 + uiState.goodCount * 50).toFloat() / (total * 100))
            } else {
                0f
            }
            
            // ViewModel에서 계산된 코인 사용
            val coinsEarned = (uiState.score * 1.5).toInt()
            
            onGameComplete(accuracy, coinsEarned)
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
            
            // 점수 및 콤보 표시
            ScoreDisplay(
                score = uiState.score,
                combo = uiState.combo,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 중앙: 판정 표시
            JudgementDisplay(
                judgementEvent = uiState.judgementEvent,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // 노트 레인 (가장 중요한 영역)
            NoteLane(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                notes = uiState.notes,
                currentTime = currentTime,
                onNoteTap = { note ->
                    viewModel.onNoteTap(note)
                },
                onNoteSwipe = { note, direction ->
                    viewModel.onNoteTap(note) // 임시로 탭과 동일하게 처리
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 하단: 판정 카운트
            JudgementCountDisplay(
                perfectCount = uiState.perfectCount,
                goodCount = uiState.goodCount,
                missCount = uiState.missCount,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
