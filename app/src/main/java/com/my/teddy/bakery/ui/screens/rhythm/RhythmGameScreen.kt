package com.my.teddy.bakery.ui.screens.rhythm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.my.teddy.bakery.game.rhythm.models.Judgement

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
            
            // 임시로 계산된 코인 (실제로는 ViewModel에서 계산됨)
            val coinsEarned = (uiState.score * 1.5).toInt()
            
            onGameComplete(accuracy, coinsEarned)
        }
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 진행 상황
            LinearProgressIndicator(
                progress = { uiState.currentTime / 25f },
                modifier = Modifier.fillMaxWidth(),
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 시간 표시
            Text(
                text = "시간: ${uiState.currentTime.toInt()}초",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 노트 레인 영역 (임시)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "♪",
                            style = MaterialTheme.typography.displayLarge
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 판정 표시
                        uiState.judgement?.let { judgement ->
                            Text(
                                text = when (judgement) {
                                    Judgement.PERFECT -> "PERFECT!"
                                    Judgement.GOOD -> "GOOD"
                                    Judgement.MISS -> "MISS"
                                },
                                style = MaterialTheme.typography.headlineMedium,
                                color = when (judgement) {
                                    Judgement.PERFECT -> MaterialTheme.colorScheme.primary
                                    Judgement.GOOD -> MaterialTheme.colorScheme.secondary
                                    Judgement.MISS -> MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "활성 노트: ${uiState.notes.size}개",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 점수 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "점수: ${uiState.score}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "콤보: ${uiState.combo}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "P: ${uiState.perfectCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "G: ${uiState.goodCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "M: ${uiState.missCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
