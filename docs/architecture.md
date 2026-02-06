# 🏗️ Bakery Rhythm - 아키텍처 설계

## 개요

이 문서는 Android용 캐주얼 리듬-타이쿤 모바일 게임인 Bakery Rhythm의 아키텍처를 정의합니다.

**아키텍처 패턴**: 간소화된 MVVM + 게임 로직 레이어

**핵심 원칙**:
- 1인 개발에 적합한 단순하고 유지보수 가능한 구조
- 명확한 관심사 분리
- 테스트 가능한 게임 로직
- Compose 우선 반응형 UI
- 최소한의 의존성

---

## 아키텍처 레이어

```
┌─────────────────────────────────────┐
│      UI Layer (Compose)             │
│   Screens + ViewModels              │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│      Game Logic Layer               │
│   RhythmEngine, Economy, Upgrades   │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│      Data Layer                     │
│   Repository + DataStore            │
└─────────────────────────────────────┘
```

---

## 1. UI Layer (프레젠테이션)

### 책임
- Jetpack Compose로 UI 렌더링
- 사용자 입력 처리
- ViewModel의 상태를 관찰하고 표시
- 화면 간 네비게이션

### 컴포넌트

#### Screens (화면)
- `BakeryScreen` - 메인 베이커리 화면
- `RhythmGameScreen` - 리듬 게임 플레이 화면
- `ResultScreen` - 게임 결과 화면

#### ViewModels (뷰모델)
각 화면은 대응하는 ViewModel을 가집니다:
- `BakeryViewModel` - 베이커리 상태, 업그레이드, 코인 관리
- `RhythmViewModel` - 리듬 게임 상태, 노트, 판정 관리
- `ResultViewModel` - 결과 계산 및 표시

### 상태 관리

**패턴**: 단방향 데이터 흐름 (UDF)

```kotlin
// 예시: RhythmViewModel
class RhythmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RhythmUiState())
    val uiState: StateFlow<RhythmUiState> = _uiState.asStateFlow()
    
    fun onEvent(event: RhythmEvent) {
        when (event) {
            is RhythmEvent.NoteTapped -> handleNoteTap()
            is RhythmEvent.NoteHeld -> handleNoteHold()
            // ...
        }
    }
}
```

**UI State 클래스**:
```kotlin
data class BakeryUiState(
    val coins: Int = 0,
    val upgrades: List<Upgrade> = emptyList(),
    val isLoading: Boolean = false
)

data class RhythmUiState(
    val notes: List<Note> = emptyList(),
    val currentTime: Float = 0f,
    val score: Int = 0,
    val combo: Int = 0,
    val judgement: Judgement? = null,
    val isPlaying: Boolean = false
)

data class ResultUiState(
    val accuracy: Float = 0f,
    val coinsEarned: Int = 0,
    val breadQuality: BreadQuality = BreadQuality.NORMAL
)
```

### 네비게이션

Compose Navigation과 간단한 NavHost 사용:

```kotlin
sealed class Screen(val route: String) {
    object Bakery : Screen("bakery")
    object RhythmGame : Screen("rhythm_game")
    object Result : Screen("result/{accuracy}/{score}")
}
```

---

## 2. Game Logic Layer (게임 로직)

### 책임
- 순수 게임 로직 (Android 의존성 없음)
- 리듬 엔진 타이밍 및 판정
- 점수 계산
- 경제 및 업그레이드 로직
- **유닛 테스트 가능**

### 컴포넌트

#### 리듬 시스템

**RhythmEngine**
- 핵심 타이밍 엔진
- BPM: 120 (박자당 500ms)
- 틱 레이트: 60 FPS (프레임당 16.67ms)
- 게임 시계 및 노트 생성 관리

```kotlin
class RhythmEngine(
    private val bpm: Int = 120,
    private val songDuration: Float = 25f
) {
    private var currentTime: Float = 0f
    private val beatInterval = 60f / bpm // 120 BPM에서 0.5초
    
    fun update(deltaTime: Float): RhythmState {
        currentTime += deltaTime
        // 노트 위치 업데이트, 판정 체크
        return RhythmState(...)
    }
    
    fun reset() {
        currentTime = 0f
    }
}
```

**JudgementSystem**
- 타이밍 정확도 계산
- 판정 타입 반환 (Perfect/Good/Miss)

```kotlin
class JudgementSystem {
    companion object {
        const val PERFECT_WINDOW = 80f // ms
        const val GOOD_WINDOW = 160f // ms
    }
    
    fun judge(tapTime: Float, noteTime: Float): Judgement {
        val diff = abs(tapTime - noteTime)
        return when {
            diff <= PERFECT_WINDOW -> Judgement.PERFECT
            diff <= GOOD_WINDOW -> Judgement.GOOD
            else -> Judgement.MISS
        }
    }
}

enum class Judgement(val score: Int) {
    PERFECT(100),
    GOOD(50),
    MISS(0)
}
```

**ScoreCalculator**
- 총점 및 정확도 계산
- 공식: `accuracy = earnedScore / maxScore`

```kotlin
class ScoreCalculator {
    fun calculateAccuracy(
        perfectCount: Int,
        goodCount: Int,
        missCount: Int
    ): Float {
        val totalNotes = perfectCount + goodCount + missCount
        if (totalNotes == 0) return 0f
        
        val earnedScore = (perfectCount * 100) + (goodCount * 50)
        val maxScore = totalNotes * 100
        return earnedScore.toFloat() / maxScore
    }
}
```

**NoteManager**
- 노트 차트 로드 (MVP에서는 하드코딩된 패턴)
- 타이밍에 따라 노트 생성

```kotlin
data class Note(
    val id: Int,
    val type: NoteType,
    val time: Float, // 박자 타이밍
    val lane: Int = 0 // 향후 멀티 레인 지원용
)

enum class NoteType {
    TAP,
    HOLD,
    SWIPE_LEFT,
    SWIPE_RIGHT
}

class NoteManager {
    fun generateNotes(duration: Float): List<Note> {
        // MVP용: 간단한 패턴 생성
        // 나중에: JSON에서 로드
        return buildList {
            // 예시: 매 박자마다 탭 노트 생성
            var beatTime = 1f
            var id = 0
            while (beatTime < duration) {
                add(Note(id++, NoteType.TAP, beatTime))
                beatTime += 0.5f // 120 BPM에서 반 박자마다
            }
        }
    }
}
```

#### 경제 시스템

**BreadPriceCalculator**
- 정확도를 빵 품질로 변환
- 품질에 따른 배율 적용

```kotlin
enum class BreadQuality(val multiplier: Float) {
    EXCELLENT(1.5f),  // 95-100%
    GOOD(1.2f),       // 80-94%
    NORMAL(1.0f),     // 60-79%
    POOR(0.5f)        // <60%
}

class BreadPriceCalculator {
    fun calculatePrice(
        basePrice: Int,
        accuracy: Float,
        breadsPerPlay: Int
    ): Int {
        val quality = getQuality(accuracy)
        return (basePrice * quality.multiplier * breadsPerPlay).toInt()
    }
    
    private fun getQuality(accuracy: Float): BreadQuality {
        return when {
            accuracy >= 0.95f -> BreadQuality.EXCELLENT
            accuracy >= 0.80f -> BreadQuality.GOOD
            accuracy >= 0.60f -> BreadQuality.NORMAL
            else -> BreadQuality.POOR
        }
    }
}
```

**UpgradeManager**
- 업그레이드 비용 및 효과 관리
- 구매 유효성 검증
- 업그레이드 효과 적용

```kotlin
sealed class Upgrade(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Int
) {
    object RecipeUpgrade : Upgrade(
        id = "recipe",
        name = "좋은 레시피",
        description = "빵 가격 +10%",
        baseCost = 500
    )
    
    object BetterOven : Upgrade(
        id = "oven_quality",
        name = "좋은 오븐",
        description = "Perfect 판정 범위 증가",
        baseCost = 1000
    )
    
    object BiggerOven : Upgrade(
        id = "oven_size",
        name = "대형 오븐",
        description = "플레이당 더 많은 빵 생산",
        baseCost = 2000
    )
}

data class UpgradeState(
    val upgrade: Upgrade,
    val level: Int = 0,
    val currentCost: Int = upgrade.baseCost
)

class UpgradeManager {
    fun canAfford(coins: Int, upgrade: UpgradeState): Boolean {
        return coins >= upgrade.currentCost
    }
    
    fun calculateNextCost(currentCost: Int): Int {
        return (currentCost * 1.5f).toInt()
    }
    
    fun applyUpgrade(upgrade: Upgrade, level: Int): UpgradeEffect {
        return when (upgrade) {
            is Upgrade.RecipeUpgrade -> 
                UpgradeEffect.PriceIncrease(1.0f + (level * 0.1f))
            is Upgrade.BetterOven -> 
                UpgradeEffect.JudgementWindowIncrease(80f + (level * 10f))
            is Upgrade.BiggerOven -> 
                UpgradeEffect.BreadsPerPlayIncrease(1 + level)
        }
    }
}

sealed class UpgradeEffect {
    data class PriceIncrease(val multiplier: Float) : UpgradeEffect()
    data class JudgementWindowIncrease(val windowMs: Float) : UpgradeEffect()
    data class BreadsPerPlayIncrease(val count: Int) : UpgradeEffect()
}
```

---

## 3. Data Layer (데이터)

### 책임
- 게임 데이터 저장 및 조회
- 게임 상태의 단일 진실 공급원
- 데이터 작업 처리

### 컴포넌트

**GameDataRepository**
- 데이터 작업의 파사드
- ViewModel과 DataStore 간 조정

```kotlin
class GameDataRepository(
    private val dataStore: GameDataStore
) {
    val gameState: Flow<GameState> = dataStore.gameStateFlow
    
    suspend fun updateCoins(amount: Int) {
        dataStore.updateCoins(amount)
    }
    
    suspend fun purchaseUpgrade(upgradeId: String) {
        dataStore.purchaseUpgrade(upgradeId)
    }
    
    suspend fun saveRhythmResult(result: RhythmResult) {
        dataStore.saveLastResult(result)
    }
}
```

**GameDataStore**
- 간단한 키-값 저장을 위해 DataStore Preferences 사용
- Room 데이터베이스는 불필요 (이 게임에는 과함)

```kotlin
class GameDataStore(private val context: Context) {
    private val dataStore = context.dataStore
    
    val gameStateFlow: Flow<GameState> = dataStore.data.map { prefs ->
        GameState(
            coins = prefs[COINS_KEY] ?: 0,
            recipeLevel = prefs[RECIPE_LEVEL_KEY] ?: 0,
            ovenQualityLevel = prefs[OVEN_QUALITY_LEVEL_KEY] ?: 0,
            ovenSizeLevel = prefs[OVEN_SIZE_LEVEL_KEY] ?: 0
        )
    }
    
    suspend fun updateCoins(amount: Int) {
        dataStore.edit { prefs ->
            val current = prefs[COINS_KEY] ?: 0
            prefs[COINS_KEY] = current + amount
        }
    }
    
    // ... 기타 작업
    
    companion object {
        private val Context.dataStore by preferencesDataStore("game_data")
        private val COINS_KEY = intPreferencesKey("coins")
        private val RECIPE_LEVEL_KEY = intPreferencesKey("recipe_level")
        private val OVEN_QUALITY_LEVEL_KEY = intPreferencesKey("oven_quality_level")
        private val OVEN_SIZE_LEVEL_KEY = intPreferencesKey("oven_size_level")
    }
}
```

### 데이터 모델

```kotlin
data class GameState(
    val coins: Int = 0,
    val recipeLevel: Int = 0,
    val ovenQualityLevel: Int = 0,
    val ovenSizeLevel: Int = 0
)

data class RhythmResult(
    val score: Int,
    val accuracy: Float,
    val perfectCount: Int,
    val goodCount: Int,
    val missCount: Int,
    val maxCombo: Int,
    val coinsEarned: Int
)
```

---

## 4. 의존성 관리

### 전략

MVP에서는 **수동 의존성 주입** 사용 (Hilt/Koin 없음).

3개 화면에는 간단하고 충분합니다.

```kotlin
// Application 클래스
class BakeryRhythmApp : Application() {
    lateinit var repository: GameDataRepository
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        val dataStore = GameDataStore(this)
        repository = GameDataRepository(dataStore)
    }
}

// ViewModel Factory
class ViewModelFactory(
    private val repository: GameDataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(BakeryViewModel::class.java) -> {
                BakeryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RhythmViewModel::class.java) -> {
                RhythmViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
```

**참고**: 프로젝트가 MVP를 넘어 성장하면 Hilt로 마이그레이션.

---

## 5. 테스트 전략

### 유닛 테스트 (게임 로직 레이어)

순수 로직에 집중 - Android 의존성 없음:

```kotlin
class ScoreCalculatorTest {
    private lateinit var calculator: ScoreCalculator
    
    @Before
    fun setup() {
        calculator = ScoreCalculator()
    }
    
    @Test
    fun `모두 perfect일 때 정확도는 100%`() {
        val accuracy = calculator.calculateAccuracy(
            perfectCount = 10,
            goodCount = 0,
            missCount = 0
        )
        assertEquals(1.0f, accuracy, 0.01f)
    }
    
    @Test
    fun `모두 good일 때 정확도는 50%`() {
        val accuracy = calculator.calculateAccuracy(
            perfectCount = 0,
            goodCount = 10,
            missCount = 0
        )
        assertEquals(0.5f, accuracy, 0.01f)
    }
}

class BreadPriceCalculatorTest {
    // 품질 배율 테스트
    // 가격 계산 테스트
}

class JudgementSystemTest {
    // 타이밍 윈도우 테스트
}
```

### UI 테스트 (MVP에서는 선택사항)

주요 플로우에 Compose Testing 사용:

```kotlin
@Test
fun bakeryScreen_코인_개수_표시() {
    composeTestRule.setContent {
        BakeryScreen(/* ... */)
    }
    
    composeTestRule
        .onNodeWithText("100 코인")
        .assertIsDisplayed()
}
```

---

## 6. 성능 고려사항

### 리듬 게임 성능

**중요**: 리듬 게임은 일관된 프레임 타이밍이 필요합니다.

**전략**:

1. **프레임 델타가 아닌 시스템 시간 사용**
   - 정확한 타이밍을 위해 `System.nanoTime()` 사용
   - Compose 리컴포지션 타이밍에 의존하지 않음

2. **게임 루프를 UI와 분리**
   ```kotlin
   LaunchedEffect(isPlaying) {
       while (isPlaying) {
           withFrameNanos { frameTime ->
               val deltaTime = (frameTime - lastFrameTime) / 1_000_000f
               rhythmEngine.update(deltaTime)
               lastFrameTime = frameTime
           }
       }
   }
   ```

3. **리컴포지션 최소화**
   - `remember` 및 `derivedStateOf` 사용
   - 자주 업데이트되는 상태 분리 (점수, 콤보)

4. **노트 렌더링 최적화**
   - 화면에 보이는 노트만 렌더링
   - 뷰포트 밖 노트 컬링

---

## 7. 디렉토리 구조

```
app/src/main/java/com/my/teddy/bakery/
│
├── ui/
│   ├── screens/
│   │   ├── bakery/
│   │   │   ├── BakeryScreen.kt
│   │   │   ├── BakeryViewModel.kt
│   │   │   └── components/
│   │   │       ├── CoinDisplay.kt
│   │   │       ├── UpgradeButton.kt
│   │   │       └── BakeryStage.kt
│   │   │
│   │   ├── rhythm/
│   │   │   ├── RhythmGameScreen.kt
│   │   │   ├── RhythmViewModel.kt
│   │   │   └── components/
│   │   │       ├── NoteLane.kt
│   │   │       ├── NoteItem.kt
│   │   │       ├── JudgementDisplay.kt
│   │   │       ├── ProgressBar.kt
│   │   │       └── ScoreDisplay.kt
│   │   │
│   │   └── result/
│   │       ├── ResultScreen.kt
│   │       └── ResultViewModel.kt
│   │
│   ├── navigation/
│   │   └── NavGraph.kt
│   │
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── game/
│   ├── rhythm/
│   │   ├── RhythmEngine.kt
│   │   ├── JudgementSystem.kt
│   │   ├── ScoreCalculator.kt
│   │   ├── NoteManager.kt
│   │   └── models/
│   │       ├── Note.kt
│   │       ├── Judgement.kt
│   │       └── RhythmState.kt
│   │
│   └── economy/
│       ├── BreadPriceCalculator.kt
│       ├── UpgradeManager.kt
│       └── models/
│           ├── BreadQuality.kt
│           ├── Upgrade.kt
│           └── UpgradeEffect.kt
│
├── data/
│   ├── repository/
│   │   └── GameDataRepository.kt
│   │
│   ├── local/
│   │   └── GameDataStore.kt
│   │
│   └── model/
│       ├── GameState.kt
│       └── RhythmResult.kt
│
├── di/
│   └── ViewModelFactory.kt
│
├── BakeryRhythmApp.kt
└── MainActivity.kt
```

---

## 8. 향후 고려사항

### MVP 출시 이후

1. **분석**: 플레이어 행동을 위한 Firebase Analytics 추가
2. **수익화**: 광고 통합 (코인 부스트용 리워드 비디오)
3. **더 많은 콘텐츠**:
   - 여러 곡/레시피
   - 더 많은 업그레이드 타입
   - 캐릭터 커스터마이징
4. **아키텍처 진화**:
   - DI가 복잡해지면 Hilt로 마이그레이션
   - 비즈니스 로직이 커지면 Use Cases 레이어 추가
   - 기능 세트가 확장되면 멀티 모듈 고려

### 확장 가능성 포인트

아키텍처는 확장 가능하도록 설계됨:
- **Game Logic Layer**는 Android에 무관 → 필요시 iOS와 공유 가능
- **Repository 패턴** → 나중에 원격 데이터 소스 추가 용이
- **모듈형 구조** → 모듈 추출 용이

---

## 요약

| 레이어 | 패턴 | 주요 기술 |
|-------|---------|------------------|
| UI | MVVM + UDF | Jetpack Compose, ViewModel, StateFlow |
| 게임 로직 | 순수 Kotlin 클래스 | 테스트 가능, 재사용 가능 |
| 데이터 | Repository 패턴 | DataStore Preferences |
| DI | 수동 (MVP용) | Application 클래스 |
| 네비게이션 | Compose Navigation | NavHost, NavController |

**아키텍처 목표**: 빠른 MVP 개발에 충분히 간단하면서도, 출시 후 확장할 수 있을 만큼 구조화된 견고한 기반 구축.
