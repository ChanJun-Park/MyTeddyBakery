package com.my.teddy.bakery.ui.screens.rhythm.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.my.teddy.bakery.game.rhythm.models.Note
import com.my.teddy.bakery.game.rhythm.models.NoteType

/**
 * 노트 레인 컴포넌트
 * 
 * 노트가 위에서 아래로 떨어지는 시각적 표현과 입력 처리
 */
@Composable
fun NoteLane(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    currentTime: Float,
    onNoteTap: (Note) -> Unit,
    onNoteSwipe: (Note, SwipeDirection) -> Unit
) {
    val updatedCurrentTime by rememberUpdatedState(currentTime)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.05f))
            .pointerInput(Unit) {
                // 모든 터치를 탭으로 처리 (MVP)
                while(true) {
                    detectTapGestures { offset ->
                        Log.d("NoteLane", "터치 감지: y=${offset.y}, 현재시간=$updatedCurrentTime, 활성노트=${notes.size}개")

                        val tappedNote = findNoteAtPosition(
                            notes = notes,
                            currentTime = updatedCurrentTime,
                            touchY = offset.y,
                            laneHeight = size.height.toFloat()
                        )

                        if (tappedNote != null) {
                            Log.d("NoteLane", "노트 히트! id=${tappedNote.id}, type=${tappedNote.type}, time=${tappedNote.time}")
                            onNoteTap(tappedNote)
                        } else {
                            Log.d("NoteLane", "히트 가능한 노트 없음")
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val laneHeight = size.height
            val judgementLineY = laneHeight * 0.8f // 판정선은 하단 80% 위치
            
            // 판정선 그리기
            drawLine(
                color = Color.Red,
                start = Offset(0f, judgementLineY),
                end = Offset(size.width, judgementLineY),
                strokeWidth = 4.dp.toPx()
            )
            
            // 노트 그리기
            notes.forEach { note ->
                val noteY = calculateNoteY(
                    noteTime = note.time,
                    currentTime = currentTime,
                    laneHeight = laneHeight,
                    judgementLineY = judgementLineY
                )
                
                // 화면 내의 노트만 그리기
                if (noteY in -100f..laneHeight + 100f) {
                    drawNote(
                        noteType = note.type,
                        x = size.width / 2f,
                        y = noteY,
                        size = 80.dp.toPx()
                    )
                }
            }
        }
        
        // 판정선 하단에 터치 가이드
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.1f))
        )
    }
}

/**
 * 노트의 Y 위치 계산
 * 
 * @param noteTime 노트 타이밍 (초)
 * @param currentTime 현재 시간 (초)
 * @param laneHeight 레인 높이
 * @param judgementLineY 판정선 Y 위치
 * @return 계산된 Y 위치
 */
private fun calculateNoteY(
    noteTime: Float,
    currentTime: Float,
    laneHeight: Float,
    judgementLineY: Float
): Float {
    val timeUntilNote = noteTime - currentTime
    val noteSpeed = 500f // 픽셀/초
    
    // 노트는 현재 시간으로부터 떨어진 시간만큼 위에 위치
    return judgementLineY - (timeUntilNote * noteSpeed)
}

/**
 * Canvas에 노트 그리기
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawNote(
    noteType: NoteType,
    x: Float,
    y: Float,
    size: Float
) {
    val color = when (noteType) {
        NoteType.TAP -> Color(0xFF4CAF50)
        NoteType.HOLD -> Color(0xFF2196F3)
        NoteType.SWIPE_LEFT -> Color(0xFFFFC107)
        NoteType.SWIPE_RIGHT -> Color(0xFFFF9800)
    }
    
    when (noteType) {
        NoteType.TAP -> {
            // 원형 노트
            drawCircle(
                color = color,
                radius = size / 2,
                center = Offset(x, y)
            )
        }
        NoteType.HOLD -> {
            // 사각형 노트 (길게)
            drawRect(
                color = color,
                topLeft = Offset(x - size / 2, y - size),
                size = Size(size, size * 2)
            )
        }
        NoteType.SWIPE_LEFT -> {
            // 왼쪽 화살표
            drawCircle(
                color = color,
                radius = size / 2,
                center = Offset(x, y)
            )
            // 화살표 표시는 향후 추가
        }
        NoteType.SWIPE_RIGHT -> {
            // 오른쪽 화살표
            drawCircle(
                color = color,
                radius = size / 2,
                center = Offset(x, y)
            )
            // 화살표 표시는 향후 추가
        }
    }
}

/**
 * 터치 위치에 있는 노트 찾기
 * 
 * 판정선 근처의 노트를 찾음 (실제 터치 Y 위치는 무시하고 타이밍만 체크)
 */
private fun findNoteAtPosition(
    notes: List<Note>,
    currentTime: Float,
    touchY: Float,
    laneHeight: Float
): Note? {
    val judgementLineY = laneHeight * 0.8f
    
    // 판정선 근처에 있는 노트 중 가장 가까운 노트 찾기
    return notes
        .filter { note ->
            val noteY = calculateNoteY(note.time, currentTime, laneHeight, judgementLineY)
            // 판정선 기준 위아래 150픽셀 이내의 노트만 고려
            val distanceFromLine = kotlin.math.abs(noteY - judgementLineY)
            distanceFromLine < 150f
        }
        .minByOrNull { note ->
            // 판정선에 가장 가까운 노트 선택
            val noteY = calculateNoteY(note.time, currentTime, laneHeight, judgementLineY)
            kotlin.math.abs(noteY - judgementLineY)
        }
}

/**
 * 스와이프 방향
 */
enum class SwipeDirection {
    LEFT, RIGHT
}
