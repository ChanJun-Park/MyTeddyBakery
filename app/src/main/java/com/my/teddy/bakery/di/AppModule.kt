package com.my.teddy.bakery.di

import android.content.Context
import com.my.teddy.bakery.data.local.GameDataStore
import com.my.teddy.bakery.data.repository.GameDataRepository
import com.my.teddy.bakery.game.economy.BreadPriceCalculator
import com.my.teddy.bakery.game.economy.UpgradeManager
import com.my.teddy.bakery.game.baking.JudgementSystem
import com.my.teddy.bakery.game.baking.NoteManager
import com.my.teddy.bakery.game.baking.BakingEngine
import com.my.teddy.bakery.game.baking.ScoreCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt 의존성 주입 모듈
 * 
 * 애플리케이션 전체에서 사용되는 싱글톤 객체들을 제공
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // ===== Data Layer =====
    
    /**
     * GameDataStore 제공
     */
    @Provides
    @Singleton
    fun provideGameDataStore(
        @ApplicationContext context: Context
    ): GameDataStore {
        return GameDataStore(context)
    }
    
    /**
     * GameDataRepository 제공
     */
    @Provides
    @Singleton
    fun provideGameDataRepository(
        dataStore: GameDataStore
    ): GameDataRepository {
        return GameDataRepository(dataStore)
    }
    
    // ===== Game Logic Layer - Baking =====
    
    /**
     * BakingEngine 제공 (매번 새 인스턴스)
     */
    @Provides
    fun provideBakingEngine(): BakingEngine {
        return BakingEngine()
    }
    
    /**
     * JudgementSystem 제공
     */
    @Provides
    @Singleton
    fun provideJudgementSystem(): JudgementSystem {
        return JudgementSystem()
    }
    
    /**
     * ScoreCalculator 제공
     */
    @Provides
    @Singleton
    fun provideScoreCalculator(): ScoreCalculator {
        return ScoreCalculator()
    }
    
    /**
     * NoteManager 제공
     */
    @Provides
    @Singleton
    fun provideNoteManager(): NoteManager {
        return NoteManager()
    }
    
    // ===== Game Logic Layer - Economy =====
    
    /**
     * BreadPriceCalculator 제공
     */
    @Provides
    @Singleton
    fun provideBreadPriceCalculator(): BreadPriceCalculator {
        return BreadPriceCalculator()
    }
    
    /**
     * UpgradeManager 제공
     */
    @Provides
    @Singleton
    fun provideUpgradeManager(): UpgradeManager {
        return UpgradeManager()
    }
}
