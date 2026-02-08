package com.my.teddy.bakery.ui.screens.baking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 인터랙션 버튼들
 * 
 * 뭉치기, 치대기, 두드리기 버튼 제공
 */
@Composable
fun InteractionButtons(
    onKnead: () -> Unit,
    onFold: () -> Unit,
    onPound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 뭉치기 버튼
        Button(
            onClick = onKnead,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800) // 주황색
            )
        ) {
            Text(
                text = "뭉치기",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
        
        // 치대기 버튼
        Button(
            onClick = onFold,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8BC34A) // 연두색
            )
        ) {
            Text(
                text = "치대기",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
        
        // 두드리기 버튼
        Button(
            onClick = onPound,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF03A9F4) // 하늘색
            )
        ) {
            Text(
                text = "두드리기",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}
