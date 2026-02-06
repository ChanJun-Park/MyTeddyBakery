package com.my.teddy.bakery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.my.teddy.bakery.ui.navigation.NavGraph
import com.my.teddy.bakery.ui.theme.MyTeddyBakeryTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 액티비티
 * 
 * @AndroidEntryPoint: Hilt를 통해 의존성 주입을 받을 수 있게 함
 * - 이 액티비티와 연결된 프래그먼트, 뷰, ViewModel 등에 주입 가능
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MyTeddyBakeryTheme {
				NavGraph()
			}
		}
	}
}