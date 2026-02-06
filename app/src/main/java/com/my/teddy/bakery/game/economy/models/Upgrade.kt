package com.my.teddy.bakery.game.economy.models

/**
 * 게임 내 업그레이드 종류
 * 
 * @param id 업그레이드 고유 ID
 * @param name 업그레이드 이름
 * @param description 업그레이드 설명
 * @param baseCost 초기 비용
 */
sealed class Upgrade(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Int
) {
    /**
     * 레시피 업그레이드 - 빵 기본 가격 증가
     */
    object RecipeUpgrade : Upgrade(
        id = "recipe",
        name = "좋은 레시피",
        description = "빵 가격 +10%",
        baseCost = 500
    )
    
    /**
     * 좋은 오븐 - Perfect 판정 범위 증가
     */
    object BetterOven : Upgrade(
        id = "oven_quality",
        name = "좋은 오븐",
        description = "Perfect 판정 범위 증가",
        baseCost = 1000
    )
    
    /**
     * 대형 오븐 - 플레이당 빵 개수 증가
     */
    object BiggerOven : Upgrade(
        id = "oven_size",
        name = "대형 오븐",
        description = "플레이당 더 많은 빵 생산",
        baseCost = 2000
    )
}

/**
 * 업그레이드의 현재 상태
 * 
 * @param upgrade 업그레이드 종류
 * @param level 현재 레벨
 * @param currentCost 현재 업그레이드 비용
 */
data class UpgradeState(
    val upgrade: Upgrade,
    val level: Int = 0,
    val currentCost: Int = upgrade.baseCost
)
