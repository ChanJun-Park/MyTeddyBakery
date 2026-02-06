package com.my.teddy.bakery.ui.screens.rhythm.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 점수 및 콤보 표시 컴포넌트
 */
@Composable
fun ScoreDisplay(
    score: Int,
    combo: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 점수
            Column {
                Text(
                    text = "점수",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 콤보
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "콤보",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${combo}x",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (combo > 0) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 판정 카운트 표시 컴포넌트
 */
@Composable
fun JudgementCountDisplay(
    perfectCount: Int,
    goodCount: Int,
    missCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            JudgementCountItem("Perfect", perfectCount, MaterialTheme.colorScheme.primary)
            JudgementCountItem("Good", goodCount, MaterialTheme.colorScheme.secondary)
            JudgementCountItem("Miss", missCount, MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun JudgementCountItem(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
