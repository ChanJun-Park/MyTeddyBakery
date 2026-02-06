package com.my.teddy.bakery.data.model

/**
 * 게임의 전체 상태를 나타내는 데이터 모델
 * DataStore에 저장되는 영구 데이터
 */
data class GameState(
    val coins: Int = 0,
    val recipeLevel: Int = 0,
    val ovenQualityLevel: Int = 0,
    val ovenSizeLevel: Int = 0
)
