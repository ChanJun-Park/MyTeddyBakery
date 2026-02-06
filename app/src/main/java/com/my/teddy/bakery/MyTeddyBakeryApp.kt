package com.my.teddy.bakery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * MyTeddyBakery 게임의 Application 클래스
 * 
 * @HiltAndroidApp: Hilt의 코드 생성을 트리거하는 어노테이션
 * - 애플리케이션 수준의 의존성 컨테이너 생성
 * - 모든 Hilt 컴포넌트의 부모 역할
 */
@HiltAndroidApp
class MyTeddyBakeryApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 필요시 초기화 로직 추가
    }
}
