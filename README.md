# 🍞 MyTeddyBakery

귀여운 곰이 운영하는 베이커리 리듬 게임

## 📱 프로젝트 소개

MyTeddyBakery는 Android용 **캐주얼 리듬 + 타이쿤 모바일 게임**입니다.

리듬 게임을 플레이하여 빵을 만들고, 돈을 벌어 베이커리를 성장시키는 힐링 게임입니다.

## 🎮 게임 특징

- **짧은 플레이 세션**: 한 판당 약 25초
- **스트레스 제로**: 게임 오버 없음, 실패 패널티 없음
- **완전 능동형**: 플레이할 때만 돈을 벌 수 있음
- **단순한 성장**: 3가지 업그레이드로 점진적 성장

## 🛠️ 기술 스택

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Game Logic Layer
- **DI**: Hilt
- **Data**: DataStore Preferences
- **Navigation**: Compose Navigation

## 📂 프로젝트 구조

```
app/src/main/java/com/my/teddy/bakery/
├── ui/              # UI Layer (Compose Screens & ViewModels)
├── game/            # Game Logic Layer (순수 게임 로직)
├── data/            # Data Layer (Repository & DataStore)
└── di/              # Dependency Injection (Hilt Modules)
```

## 📖 문서

- [프로젝트 컨텍스트](docs/PROJECT_CONTEXT.md) - 게임 개요 및 MVP 범위
- [아키텍처 설계](docs/architecture.md) - 상세 아키텍처 문서
- [GDD](docs/GDD.md) - 게임 디자인 문서
- [리듬 시스템](docs/rhythm-system.md) - 리듬 게임 시스템 상세
- [리듬 게임 구현](docs/rhythm-game-implementation.md) - 구현 완료 문서 ✨

## 🎯 MVP 범위

첫 출시 버전에는 다음만 포함:

### 화면
1. **Main_Bakery** - 메인 베이커리 화면
2. **Rhythm_Game** - 리듬 게임 플레이 화면
3. **Result** - 게임 결과 화면

### 기능
- 리듬 미니게임 (탭/홀드/스와이프)
- 빵 품질 계산 (정확도 기반)
- 코인 시스템
- 3가지 업그레이드 (레시피/좋은 오븐/대형 오븐)

## 🚀 빌드 방법

```bash
# 프로젝트 클론
git clone [repository-url]

# 의존성 다운로드 및 빌드
./gradlew build

# 디버그 APK 설치
./gradlew installDebug
```

## 📋 개발 현황

- [x] 프로젝트 초기 설정
- [x] Hilt 의존성 주입 설정
- [x] 아키텍처 문서 작성
- [x] 디렉토리 구조 생성
- [x] 데이터 모델 구현
- [x] 리듬 엔진 구현
- [x] UI 화면 구현 (3개 화면)
- [x] 게임 로직 통합
- [x] 리듬 게임 플레이 가능
- [x] 터치 인식 문제 해결
- [x] 성능 최적화 (currentTime 분리)
- [x] 판정 애니메이션 수정
- [ ] 사운드 추가
- [ ] 애니메이션 개선
- [ ] 테스트 작성
- [ ] 출시 준비

**현재 상태: 플레이 가능한 프로토타입 완성 (MVP 90%)**

## 👤 개발자

1인 인디 프로젝트

## 📄 라이선스

Private Project
