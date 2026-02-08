package com.my.teddy.bakery.ui.screens.rhythm.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.my.teddy.bakery.game.rhythm.models.Note
import com.my.teddy.bakery.game.rhythm.models.NoteType

/**
 * 현재 수행해야 할 노트를 표시하는 컴포넌트
 */
@Composable
fun CurrentNoteDisplay(
    notes: List<Note>,
    currentIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (currentIndex < notes.size) {
            val currentNote = notes[currentIndex]
            val upcomingNotes = notes.drop(currentIndex + 1).take(3)
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 현재 노트 (크게 표시)
                Text(
                    text = "현재 수행할 동작",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                NoteIcon(
                    noteType = currentNote.type,
                    isLarge = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 다음 노트들 (작게 표시)
                if (upcomingNotes.isNotEmpty()) {
                    Text(
                        text = "다음 동작",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        upcomingNotes.forEach { note ->
                            NoteIcon(
                                noteType = note.type,
                                isLarge = false
                            )
                        }
                    }
                }
            }
        } else {
            // 모든 노트 완료
            Text(
                text = "완료!",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 노트 타입에 맞는 아이콘 표시
 */
@Composable
private fun NoteIcon(
    noteType: NoteType,
    isLarge: Boolean
) {
    val size = if (isLarge) 120.dp else 48.dp
    val color = when (noteType) {
        NoteType.KNEAD -> Color(0xFFFF9800) // 주황색
        NoteType.FOLD -> Color(0xFF8BC34A)  // 연두색
        NoteType.POUND -> Color(0xFF03A9F4) // 하늘색
    }
    val text = when (noteType) {
        NoteType.KNEAD -> "뭉치기"
        NoteType.FOLD -> "치대기"
        NoteType.POUND -> "두드리기"
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(color, CircleShape)
                .border(4.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = if (isLarge) {
                    MaterialTheme.typography.headlineMedium
                } else {
                    MaterialTheme.typography.labelMedium
                },
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
