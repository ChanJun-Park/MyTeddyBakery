package com.my.teddy.bakery.ui.screens.rhythm.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.teddy.bakery.game.rhythm.models.Judgement
import com.my.teddy.bakery.game.rhythm.models.JudgementResult
import kotlinx.coroutines.delay

/**
 * 판정 결과 표시 컴포넌트
 *
 * Perfect/Good/Miss를 애니메이션과 함께 표시
 */
@Composable
fun JudgementDisplay(
    judgementResult: JudgementResult?,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 1f))
    ) {
        Crossfade(
            targetState = judgementResult
        ) { currentState ->
            var showAnimation by remember { mutableStateOf(false) }

            // 판정 이벤트가 발생할 때마다 애니메이션 트리거
            // timestamp가 다르므로 같은 판정이 연속으로 나와도 재실행됨
            LaunchedEffect(Unit) {
                if (currentState != null) {
                    showAnimation = true
                    delay(500) // 0.5초 후 사라짐
                    showAnimation = false
                }
            }

            currentState?.let {
                AnimatedVisibility(
                    visible = showAnimation,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = getJudgementText(currentState.judgement),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = getJudgementColor(currentState.judgement)
                        )
                        Text(
                            text = "+${currentState.judgement.score}",
                            fontSize = 24.sp,
                            color = getJudgementColor(currentState.judgement)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 판정에 따른 텍스트 반환
 */
private fun getJudgementText(judgement: Judgement): String {
    return when (judgement) {
        Judgement.PERFECT -> "PERFECT!"
        Judgement.GOOD -> "GOOD"
        Judgement.MISS -> "MISS"
    }
}

/**
 * 판정에 따른 색상 반환
 */
@Composable
private fun getJudgementColor(judgement: Judgement): Color {
    return when (judgement) {
        Judgement.PERFECT -> Color(0xFFFFD700) // 금색
        Judgement.GOOD -> Color(0xFF4CAF50) // 녹색
        Judgement.MISS -> Color(0xFFF44336) // 빨간색
    }
}
