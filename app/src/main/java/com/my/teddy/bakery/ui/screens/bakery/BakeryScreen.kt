package com.my.teddy.bakery.ui.screens.bakery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 메인 베이커리 화면
 * 
 * 코인 표시, 빵 굽기 시작 버튼, 업그레이드 버튼들을 포함
 */
@Composable
fun BakeryScreen(
    onStartBaking: () -> Unit,
    viewModel: BakeryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 코인 표시
            Text(
                text = "💰 ${uiState.coins} 코인",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 캐릭터 영역 (임시)
            Card(
                modifier = Modifier
                    .size(200.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🐻",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 빵 굽기 시작 버튼
            Button(
                onClick = onStartBaking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "🍞 빵 굽기 시작",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 업그레이드 섹션
            Text(
                text = "업그레이드",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 업그레이드 버튼들
            uiState.upgrades.forEach { upgrade ->
                UpgradeCard(
                    upgradeState = upgrade,
                    currentCoins = uiState.coins,
                    onPurchase = { viewModel.purchaseUpgrade(upgrade) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * 업그레이드 카드 컴포넌트
 */
@Composable
fun UpgradeCard(
    upgradeState: com.my.teddy.bakery.game.economy.models.UpgradeState,
    currentCoins: Int,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${upgradeState.upgrade.name} Lv.${upgradeState.level}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = upgradeState.upgrade.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(
                onClick = onPurchase,
                enabled = currentCoins >= upgradeState.currentCost
            ) {
                Text("${upgradeState.currentCost}")
            }
        }
    }
}
