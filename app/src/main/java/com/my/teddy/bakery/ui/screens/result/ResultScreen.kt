package com.my.teddy.bakery.ui.screens.result

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.my.teddy.bakery.game.economy.models.BreadQuality

/**
 * 게임 결과 화면
 * 
 * 정확도, 획득 코인, 빵 품질을 표시하고 다음 액션 선택
 */
@Composable
fun ResultScreen(
    accuracy: Float,
    coinsEarned: Int,
    onPlayAgain: () -> Unit,
    onBackToBakery: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // 결과 설정
    LaunchedEffect(accuracy, coinsEarned) {
        viewModel.setResult(accuracy, coinsEarned)
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 제목
            Text(
                text = "🍞 베이킹 결과",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 빵 품질 표시
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getBreadEmoji(uiState.breadQuality),
                        style = MaterialTheme.typography.displayLarge
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = getBreadQualityText(uiState.breadQuality),
                        style = MaterialTheme.typography.titleLarge,
                        color = getBreadQualityColor(uiState.breadQuality)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 정확도 표시
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "정확도",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${(uiState.accuracy * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "획득 코인",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "💰 ${uiState.coinsEarned}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 버튼들
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "🔄 다시 플레이",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onBackToBakery,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "🏠 베이커리로 돌아가기",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * 빵 품질에 따른 이모지 반환
 */
private fun getBreadEmoji(quality: BreadQuality): String {
    return when (quality) {
        BreadQuality.EXCELLENT -> "🌟🍞"
        BreadQuality.GOOD -> "✨🍞"
        BreadQuality.NORMAL -> "🍞"
        BreadQuality.POOR -> "🔥🍞"
    }
}

/**
 * 빵 품질에 따른 텍스트 반환
 */
private fun getBreadQualityText(quality: BreadQuality): String {
    return when (quality) {
        BreadQuality.EXCELLENT -> "최고급 빵!"
        BreadQuality.GOOD -> "고급 빵"
        BreadQuality.NORMAL -> "일반 빵"
        BreadQuality.POOR -> "탄 빵..."
    }
}

/**
 * 빵 품질에 따른 색상 반환
 */
@Composable
private fun getBreadQualityColor(quality: BreadQuality): androidx.compose.ui.graphics.Color {
    return when (quality) {
        BreadQuality.EXCELLENT -> MaterialTheme.colorScheme.primary
        BreadQuality.GOOD -> MaterialTheme.colorScheme.secondary
        BreadQuality.NORMAL -> MaterialTheme.colorScheme.onSurface
        BreadQuality.POOR -> MaterialTheme.colorScheme.error
    }
}
