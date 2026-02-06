package com.my.teddy.bakery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					Greeting(
						name = "Android",
						modifier = Modifier.padding(innerPadding)
					)
				}
			}
		}
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	MyTeddyBakeryTheme {
		Greeting("Android")
	}
}