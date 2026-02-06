package com.my.teddy.bakery.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.my.teddy.bakery.data.model.GameState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore를 사용한 로컬 데이터 저장소
 * 
 * 게임 상태를 영구적으로 저장하고 불러오기
 */
class GameDataStore(private val context: Context) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("game_data")
        
        private val COINS_KEY = intPreferencesKey("coins")
        private val RECIPE_LEVEL_KEY = intPreferencesKey("recipe_level")
        private val OVEN_QUALITY_LEVEL_KEY = intPreferencesKey("oven_quality_level")
        private val OVEN_SIZE_LEVEL_KEY = intPreferencesKey("oven_size_level")
    }
    
    /**
     * 게임 상태 Flow
     */
    val gameStateFlow: Flow<GameState> = context.dataStore.data.map { prefs ->
        GameState(
            coins = prefs[COINS_KEY] ?: 0,
            recipeLevel = prefs[RECIPE_LEVEL_KEY] ?: 0,
            ovenQualityLevel = prefs[OVEN_QUALITY_LEVEL_KEY] ?: 0,
            ovenSizeLevel = prefs[OVEN_SIZE_LEVEL_KEY] ?: 0
        )
    }
    
    /**
     * 코인 업데이트
     */
    suspend fun updateCoins(amount: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[COINS_KEY] ?: 0
            prefs[COINS_KEY] = current + amount
        }
    }
    
    /**
     * 코인 설정 (절대값)
     */
    suspend fun setCoins(amount: Int) {
        context.dataStore.edit { prefs ->
            prefs[COINS_KEY] = amount
        }
    }
    
    /**
     * 업그레이드 레벨 업데이트
     */
    suspend fun updateUpgradeLevel(upgradeId: String, newLevel: Int) {
        context.dataStore.edit { prefs ->
            val key = when (upgradeId) {
                "recipe" -> RECIPE_LEVEL_KEY
                "oven_quality" -> OVEN_QUALITY_LEVEL_KEY
                "oven_size" -> OVEN_SIZE_LEVEL_KEY
                else -> return@edit
            }
            prefs[key] = newLevel
        }
    }
    
    /**
     * 모든 데이터 초기화 (디버그용)
     */
    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
