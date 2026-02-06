package com.my.teddy.bakery.ui.screens.rhythm.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.teddy.bakery.game.rhythm.models.Judgement

/**
 * 판정 결과 표시 컴포넌트
 * 
 * Perfect/Good/Miss를 애니메이션과 함께 표시
 */
@Composable
fun JudgementDisplay(
    judgement: Judgement?,
    modifier: Modifier = Modifier
) {
    var currentJudgement by remember { mutableStateOf<Judgement?>(null) }
    var showAnimation by remember { mutableStateOf(false) }
    
    // 판정이 변경될 때마다 애니메이션 트리거
    LaunchedEffect(judgement) {
        if (judgement != null) {
            currentJudgement = judgement
            showAnimation = true
            kotlinx.coroutines.delay(500) // 0.5초 후 사라짐
            showAnimation = false
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = showAnimation,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            currentJudgement?.let { judgement ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getJudgementText(judgement),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = getJudgementColor(judgement)
                    )
                    Text(
                        text = "+${judgement.score}",
                        fontSize = 24.sp,
                        color = getJudgementColor(judgement)
                    )
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
